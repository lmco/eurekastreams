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

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.GetRelatedOrganizationIdsByPersonId;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

/**
 * Cache loader for PersonCache.
 */
public class PersonCacheLoader extends CachedDomainMapper implements EntityCacheUpdater<Person>, CacheWarmer
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(PersonCacheLoader.class);

    /**
     * Fetch size.
     */
    private static final int FETCH_SIZE = 100;

    /**
     * Strategy for querying a person model view from the database.
     */
    private PersonQueryStrategy personQueryStrategy;

    /**
     * Mapper to remove a person from cache.
     */
    private RemovePersonFromCacheMapper removePersonFromCacheMapper;

    /**
     * Constructor.
     * 
     * @param inPersonQueryStrategy
     *            the person query strategy to set.
     * @param inGetRelatedOrganizationIdsByPersonIdMapper
     *            mapper to get related org ids for people
     * @param inRemovePersonFromCacheMapper
     *            mapper to remove person from cache
     */
    public PersonCacheLoader(final PersonQueryStrategy inPersonQueryStrategy,
            final GetRelatedOrganizationIdsByPersonId inGetRelatedOrganizationIdsByPersonIdMapper,
            final RemovePersonFromCacheMapper inRemovePersonFromCacheMapper)
    {
        personQueryStrategy = inPersonQueryStrategy;
        getRelatedOrganizationIdsByPersonIdMapper = inGetRelatedOrganizationIdsByPersonIdMapper;
        removePersonFromCacheMapper = inRemovePersonFromCacheMapper;
    }

    /**
     * Mapper to get related org ids by person id.
     */
    private GetRelatedOrganizationIdsByPersonId getRelatedOrganizationIdsByPersonIdMapper;

    /**
     * Initialize the Person cache - intended to run on system start-up.
     */
    public void initialize()
    {
        log.info("Initializing the Person Cache");

        long start = System.currentTimeMillis();
        long stepStart;

        // log.info("Querying and building person cache");
        // stepStart = System.currentTimeMillis();
        // queryAllPeople();
        // log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building following targets for person cache");
        stepStart = System.currentTimeMillis();
        queryAllFollowers();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Person cache initialization completed - " + (System.currentTimeMillis() - start) + " ms.");
    }

    /**
     * Query the database for all people, only requesting the fields that we're caching, paging for effeciency.
     */
    private void queryAllPeople()
    {
        Criteria criteria = personQueryStrategy.getCriteria(getHibernateSession());

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
                log.info("Loading " + recordCounter + "th person record, clearing session.");
                getHibernateSession().clear();
            }

            PersonModelView result = (PersonModelView) scroll.get(0);
            result.setRelatedOrganizationIds(getRelatedOrganizationIdsByPersonIdMapper.execute(result.getEntityId()));

            getCache().set(CacheKeys.PERSON_BY_ID + result.getEntityId(), result);
            getCache().set(CacheKeys.PERSON_BY_ACCOUNT_ID + result.getAccountId(), result.getEntityId());
            getCache().set(CacheKeys.PERSON_BY_OPEN_SOCIAL_ID + result.getOpenSocialId(), result.getEntityId());
        }
    }

    /**
     * Load up all following target ids for all persons, updating the cache. This creates the list of person ids that a
     * given person is following.
     */
    @SuppressWarnings("unchecked")
    private void queryAllFollowers()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        String queryString = "SELECT f.pk.followerId, f.pk.followingId from Follower f ";
        Query query = getEntityManager().createQuery(queryString);
        List<Object[]> results = query.getResultList();

        storeResultsInCache(results, CacheKeys.FOLLOWERS_BY_PERSON, CacheKeys.PEOPLE_FOLLOWED_BY_PERSON);

    }

    /**
     * Person updater implementation - fired when a person entity is updated.
     * 
     * @param inUpdatedPerson
     *            the person just updated
     */
    @Override
    public void onPostUpdate(final Person inUpdatedPerson)
    {
        if (log.isInfoEnabled())
        {
            log.info("Person.onPostUpdate - removing person #" + inUpdatedPerson.getId() + " from cache");
        }
        removePersonFromCacheMapper.execute(inUpdatedPerson);
    }

    /**
     * Person persist implementation - fired when a person entity is persisted.
     * 
     * @param inPersistedPerson
     *            the person just persisted
     */
    @Override
    public void onPostPersist(final Person inPersistedPerson)
    {
        if (log.isInfoEnabled())
        {
            log.info("Person.onPostPersist - person with accountId " + inPersistedPerson.getAccountId()
                    + " - doing nothing.");
        }
        // no-op
    }

    @Override
    public void execute(final List<UserActionRequest> inRequests)
    {
        initialize();
    }
}
