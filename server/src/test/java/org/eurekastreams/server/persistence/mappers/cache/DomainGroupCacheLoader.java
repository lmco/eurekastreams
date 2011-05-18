/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

/**
 * Cache loader for Groups.
 * 
 * @deprecated This is only still around until it can be refactored out of the unit tests. New cache warming and
 *             EntityCacheUpdaters are in place in production code.
 */
@Deprecated
public class DomainGroupCacheLoader extends CachedDomainMapper implements EntityCacheUpdater<DomainGroup>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(DomainGroupCacheLoader.class);

    /**
     * Fetch size.
     */
    private static final int FETCH_SIZE = 100;

    /**
     * Strategy for querying a domain group model view from the database.
     */
    private DomainGroupQueryStrategy domainGroupQueryStrategy;

    /**
     * Constructor.
     * 
     * @param inDomainGroupQueryStrategy
     *            the person query strategy to set.
     */
    public DomainGroupCacheLoader(final DomainGroupQueryStrategy inDomainGroupQueryStrategy)
    {
        domainGroupQueryStrategy = inDomainGroupQueryStrategy;
    }

    /**
     * Initialize the Domain Group hierarchy cache - intended to run on system start-up.
     */
    public void initialize()
    {
        log.info("Initializing the Domain Group Cache");
        queryAllDomainGroups();
        queryAllCoordinators();
        queryAllFollowers();
    }

    /**
     * Query all domain groups, loading them in the cache.
     */
    private void queryAllDomainGroups()
    {
        long start = System.currentTimeMillis();
        log.info("Loading up all domain groups with a single query");

        Criteria criteria = domainGroupQueryStrategy.getCriteria(getHibernateSession());

        // page the data
        criteria.setFetchSize(FETCH_SIZE);
        criteria.setCacheMode(CacheMode.IGNORE);
        ScrollableResults scroll = criteria.scroll(ScrollMode.FORWARD_ONLY);

        // loop through the results and store in cache
        long recordCounter = 0;
        while (scroll.next())
        {
            if (++recordCounter % FETCH_SIZE == 0)
            {
                log.info("Loading " + recordCounter + "th domainGroup record, clearing session.");
                getHibernateSession().clear();
            }

            DomainGroupModelView result = (DomainGroupModelView) scroll.get(0);
            getCache().set(CacheKeys.GROUP_BY_ID + result.getEntityId(), result);
            getCache().set(CacheKeys.GROUP_BY_SHORT_NAME + result.getShortName(), result.getEntityId());
        }

        log.info("Completed loading all domain groups in " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

    /**
     * Load up all coordinators for all domain groups, updating the cache.
     */
    @SuppressWarnings("unchecked")
    private void queryAllCoordinators()
    {
        log.info("Loading up all coordinators for all groups.");
        long start = System.currentTimeMillis();

        // single query to get all coordinator->group
        String queryString = "SELECT p.id, g.id FROM Person p, DomainGroup g " + " WHERE p member of g.coordinators";
        Query query = getEntityManager().createQuery(queryString);
        List<Object[]> results = query.getResultList();
        log.info("Found " + results.size() + " coordinators in " + (System.currentTimeMillis() - start)
                + " milliseconds.  Populating cache now.");

        // loop across the results, storing them all locally
        log.info("Grouping coordinators by DomainGroup.");
        Map<Long, List<Long>> groupCoordinators = new HashMap<Long, List<Long>>();
        for (Object[] result : results)
        {
            Long coordinatorId = (Long) result[0];
            Long groupId = (Long) result[1];

            if (!groupCoordinators.containsKey(groupId))
            {
                groupCoordinators.put(groupId, new ArrayList<Long>());
            }
            groupCoordinators.get(groupId).add(coordinatorId);
        }

        log.info("Writing cache for each DomainGroup.");
        // loop across the lists, pushing them to cache
        for (Long groupId : groupCoordinators.keySet())
        {
            getCache().setList(CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + groupId, groupCoordinators.get(groupId));
        }
        log.info("Completed coordinator cache loading in " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

    /**
     * Load up all followers for all domain groups, updating the cache. This assumes that the group cache was just
     * cleared and re-populated without any followers.
     */
    @SuppressWarnings("unchecked")
    private void queryAllFollowers()
    {
        // single query to get all follower->group
        String queryString = "SELECT f.pk.followerId, f.pk.followingId from GroupFollower f";
        Query query = getEntityManager().createQuery(queryString);
        List<Object[]> results = query.getResultList();

        storeResultsInCache(results, CacheKeys.FOLLOWERS_BY_GROUP, CacheKeys.GROUPS_FOLLOWED_BY_PERSON);
    }

    /**
     * Domain Group updater implementation - fired when an existing domain group entity is updated.
     * 
     * @param inUpdatedDomainGroup
     *            the domain group just updated
     */
    @Override
    public void onPostUpdate(final DomainGroup inUpdatedDomainGroup)
    {
        if (log.isInfoEnabled())
        {
            log.info("DomainGroup.onPostUpdate - removing group #" + inUpdatedDomainGroup.getId() + " from cache");
        }
        getCache().delete(CacheKeys.GROUP_BY_ID + inUpdatedDomainGroup.getId());
    }

    /**
     * Domain Group persist implementation - fired when a new domain group entity is persisted.
     * 
     * @param inDomainGroup
     *            the domainGroup just persisted
     */
    @Override
    public void onPostPersist(final DomainGroup inDomainGroup)
    {
        if (log.isInfoEnabled())
        {
            log.info("DomainGroup.onPostPersist - group with shortName " + inDomainGroup.getShortName()
                    + " - doing nothing.");
        }
        // no-op - cache will be loaded when someone requests this domain group
    }

}
