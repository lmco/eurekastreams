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
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamsByIds;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

/**
 * Cache loader for Streams.
 */
public class StreamCacheLoader extends CachedDomainMapper implements CacheWarmer
{
    /**
     * Maximum number of items for the activity id lists.
     */
    private static final int MAX_RESULTS = 1000;

    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(StreamCacheLoader.class);

    /**
     * Fetch size.
     */
    private static final int FETCH_SIZE = 5000;

    /**
     * Mapper to get all streams.
     */
    GetStreamsByIds streamsMapper;

    /**
     * Mapper to get activity ids for a given composite stream.
     */
    private CompositeStreamActivityIdsMapper idsMapper;

    /**
     * Loader for creating followed activity id lists.
     */
    private FollowedActivityIdsLoader followedActivityIdListLoader;

    /**
     * Constructor.
     * 
     * @param inStreamsMapper
     *            streams mapper to set.
     * @param inActivityIdsMapper
     *            activity ids mapper to set.
     * @param inFollowedActivityIdListLoader
     *            Followed activity id list loader.
     */
    public StreamCacheLoader(final GetStreamsByIds inStreamsMapper,
            final CompositeStreamActivityIdsMapper inActivityIdsMapper,
            final FollowedActivityIdsLoader inFollowedActivityIdListLoader)
    {
        streamsMapper = inStreamsMapper;
        idsMapper = inActivityIdsMapper;
        followedActivityIdListLoader = inFollowedActivityIdListLoader;
    }

    /**
     * Initialize the stream cache - intended to run on system start-up.
     */
    public void initialize()
    {
        log.info("Initializing the Stream Cache");

        long start = System.currentTimeMillis();
        long stepStart;

        log.info("Querying and caching 'core' stream view ids");
        stepStart = System.currentTimeMillis();
        queryCoreStreamViews();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building 'everyone' activity id list");
        stepStart = System.currentTimeMillis();
        queryEveryoneStream();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building composite stream cache");
        stepStart = System.currentTimeMillis();
        queryAllCompositeStreams();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building composite stream search cache");
        stepStart = System.currentTimeMillis();
        queryAllCompositeStreamSearches();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building starred activities cache");
        stepStart = System.currentTimeMillis();
        queryAllStarredActivities();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building stream cache");
        stepStart = System.currentTimeMillis();
        streamsMapper.execute();
        streamsMapper.setCache(getCache());
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and building person activity id list cache");
        stepStart = System.currentTimeMillis();
        queryPersonActivityIdLists();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Querying and group person activity id list cache");
        stepStart = System.currentTimeMillis();
        queryGroupActivityIdLists();
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Stream cache initialization completed - " + (System.currentTimeMillis() - start) + " ms.");
    }

    /**
     * Cache the Core StreamView types. This excludes the everyone streamview id, because that streamview is already
     * warmed in the Everyone section.
     */
    private void queryCoreStreamViews()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        Query coreStreamViewIdsQuery = getEntityManager().createQuery("SELECT id from StreamView where type =:type");

        // Cache the static PARENTORG streamview type.
        coreStreamViewIdsQuery.setParameter("type", Type.PARENTORG);
        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG, coreStreamViewIdsQuery.getSingleResult());

        // Cache the static PEOPLEFOLLOW streamview type.
        coreStreamViewIdsQuery.setParameter("type", Type.PEOPLEFOLLOW);
        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW, coreStreamViewIdsQuery.getSingleResult());

        // Cache the static STARRED streamview type.
        coreStreamViewIdsQuery.setParameter("type", Type.STARRED);
        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_STARRED, coreStreamViewIdsQuery.getSingleResult());
    }

    /**
     * Query the database for the ids of the activities on the "everyone" stream.
     */
    @SuppressWarnings("unchecked")
    private void queryEveryoneStream()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        String everyoneQueryString = "SELECT id from StreamView where type = :type";
        Query everyoneQuery = getEntityManager().createQuery(everyoneQueryString).setParameter("type",
                StreamView.Type.EVERYONE);
        long everyoneStreamId = (Long) everyoneQuery.getSingleResult();

        // Caches the Everyone stream
        log.info("Caching everyone composite stream");

        String idsQueryString = "select id FROM Activity ORDER BY PostedTime desc";
        Query idsQuery = getEntityManager().createQuery(idsQueryString);
        idsQuery.setMaxResults(MAX_RESULTS);
        List<Long> ids = idsQuery.getResultList();

        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE, everyoneStreamId);
        getCache().setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + everyoneStreamId, ids);
    }

    /**
     * Query the database for ids of activity on various lists of person activity ids.
     */
    @SuppressWarnings("unchecked")
    private void queryPersonActivityIdLists()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        Criteria criteria = getHibernateSession().createCriteria(Person.class);

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
                log.info("Loading " + recordCounter + "th person stream info");
            }

            Person person = (Person) scroll.get(0);
            long personId = person.getId();

            // Caches the list of composite streams for this user
            List<StreamView> streamViews = person.getStreamViews();
            List<Long> streamViewIds = new ArrayList();
            for (StreamView streamView : streamViews)
            {
                if (streamView != null)
                {
                    streamViewIds.add(streamView.getId());
                }
                else
                {
                    log.info("streamView for person " + personId + " was null");
                }
            }
            log.info("Caching composite stream ids of size " + streamViewIds.size() + " for user " + personId);
            getCache().setList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + personId, streamViewIds);

            // Caches the personal stream for this user
            log.info("Caching personal stream for user " + personId);
            List<Long> activities = getActivities(person.getAccountId());
            getCache().setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + person.getEntityStreamView().getId(),
                    activities);

            getCache().set(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + person.getId(),
                    person.getEntityStreamView().getId());

            // Caches the following stream for this user
            List<Long> followedActivityIds = followedActivityIdListLoader.getFollowedActivityIds(personId, MAX_RESULTS);
            getCache().setList(CacheKeys.ACTIVITIES_BY_FOLLOWING + personId, followedActivityIds);
        }
    }

    /**
     * Query the database for ids of activity on various lists of group activity ids.
     */
    private void queryGroupActivityIdLists()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        Criteria criteria = getHibernateSession().createCriteria(DomainGroup.class);

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
                log.info("Loading " + recordCounter + "th group stream info");
            }

            DomainGroup group = (DomainGroup) scroll.get(0);

            // Caches the stream for this group
            log.info("Caching stream for group " + group.getId());
            List<Long> activities = getActivities(group.getShortName());
            getCache().setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + group.getEntityStreamView().getId(),
                    activities);

            getCache()
                    .set(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + group.getId(), group.getEntityStreamView().getId());
        }
    }

    /**
     * Gets activities for a given stream (person or group).
     * 
     * @param uniqueKey
     *            the accountId or shortName.
     * @return the list of activity ids.
     */
    @SuppressWarnings("unchecked")
    private List<Long> getActivities(final String uniqueKey)
    {
        String idsQueryString = "select a.id from Activity a, StreamScope s "
                + "where a.recipientStreamScope.id = s.id and s.uniqueKey = :uniqueKey order by a.id desc";
        Query idsQuery = getEntityManager().createQuery(idsQueryString).setParameter("uniqueKey", uniqueKey);
        idsQuery.setMaxResults(MAX_RESULTS);
        List<Long> activities = idsQuery.getResultList();
        return activities;
    }

    /**
     * Query the database for all composite streams.
     */
    @SuppressWarnings("unchecked")
    private void queryAllCompositeStreams()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        String queryString = "from StreamView sv left join fetch sv.includedScopes";
        Query query = getEntityManager().createQuery(queryString);
        List<StreamView> results = query.getResultList();

        for (StreamView result : results)
        {
            getCache().set(CacheKeys.COMPOSITE_STREAM_BY_ID + result.getId(), result);
        }
    }

    /**
     * Query the database for all composite stream searches.
     */
    @SuppressWarnings("unchecked")
    private void queryAllCompositeStreamSearches()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        String queryString = "from StreamSearch";
        Query query = getEntityManager().createQuery(queryString);
        List<StreamSearch> results = query.getResultList();

        for (StreamSearch result : results)
        {
            getCache().set(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + result.getId(), result);
        }
    }

    /**
     * Query the database for all starred activities for all users.
     */
    @SuppressWarnings("unchecked")
    private void queryAllStarredActivities()
    {
        getHibernateSession().clear();
        getEntityManager().clear();

        Query q = getEntityManager().createQuery("from StarredActivity ORDER BY personId, activityId DESC");
        List<StarredActivity> results = q.getResultList();

        List<Long> keys = new ArrayList<Long>();
        long lastPersonId = 0;

        for (StarredActivity s : results)
        {
            if (lastPersonId != s.getPersonId())
            {
                if (lastPersonId != 0)
                {
                    getCache().setList(CacheKeys.STARRED_BY_PERSON_ID + lastPersonId, keys);
                    keys.clear();
                }
                keys.add(s.getActivityId());
                lastPersonId = s.getPersonId();
            }
            else
            {
                keys.add(s.getActivityId());
            }
        }
        // sets the last group in cache
        getCache().setList(CacheKeys.STARRED_BY_PERSON_ID + lastPersonId, keys);
    }

    @Override
    public void execute(final List<UserActionRequest> inRequests)
    {
        initialize();
    }
}
