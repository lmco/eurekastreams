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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Gets a list of stream objects for a given list of stream ids.
 */
public class GetStreamsByIds extends CachedDomainMapper
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(GetStreamsByIds.class);

    /**
     * People mapper.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * Get groups by shortname mapper.
     */
    private GetDomainGroupsByShortNames groupsMapper;

    /**
     * Constructor.
     *
     * @param inPeopleMapper
     *            the people mapper to set.
     * @param inGroupsMapper
     *            the group mapper to set.
     */
    public GetStreamsByIds(final GetPeopleByAccountIds inPeopleMapper, final GetDomainGroupsByShortNames inGroupsMapper)
    {
        peopleMapper = inPeopleMapper;
        groupsMapper = inGroupsMapper;
    }

    /**
     * Refresh all Streams in cache from database. Should be called during cache warming.
     *
     * @return list of stream objects.
     */
    public List<StreamScope> execute()
    {
        Map<String, StreamScope> streams = refresh(null);
        return new ArrayList<StreamScope>(streams.values());
    }

    /**
     * Looks in cache for the necessary items and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     *
     * @param streamIds
     *            the list of ids that should be found.
     * @return list of stream objects.
     */
    @SuppressWarnings("unchecked")
    public List<StreamScope> execute(final List<Long> streamIds)
    {
        // Checks to see if there's any real work to do
        if (streamIds == null || streamIds.size() == 0)
        {
            return new ArrayList<StreamScope>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : streamIds)
        {
            stringKeys.add(CacheKeys.STREAM_BY_ID + key);
        }

        // Finds streams in the cache.
        Map<String, StreamScope> streams = (Map<String, StreamScope>) (Map<String, ? >) getCache().multiGet(stringKeys);

        // Determines if any of the streams were missing from the cache
        List<Long> uncached = new ArrayList<Long>();
        for (long streamId : streamIds)
        {
            String cacheKey = CacheKeys.STREAM_BY_ID + streamId;
            if (!streams.containsKey(cacheKey) || streams.get(cacheKey) == null)
            {
                uncached.add(streamId);
            }
        }

        // One or more of the streams were missing in the cache so go to the
        // database
        if (uncached.size() != 0)
        {
            streams.putAll(refresh(uncached));
        }

        return new ArrayList<StreamScope>(streams.values());
    }

    /**
     * Gets items from database and stores them in cache.
     *
     * @param uncached
     *            list of items to be refreshed. If this is null, all items will be refreshed from database.
     * @return the map of refreshed items.
     */
    @SuppressWarnings("unchecked")
    private Map<String, StreamScope> refresh(final List<Long> uncached)
    {
        Map<String, StreamScope> streamMap = new HashMap<String, StreamScope>();

        StringBuilder query = new StringBuilder();

        if (uncached != null)
        {
            query.append("FROM StreamScope WHERE ");
            for (int i = 0; i < uncached.size(); i++)
            {
                long key = uncached.get(i);
                query.append("id=").append(key);
                if (i != uncached.size() - 1)
                {
                    query.append(" OR ");
                }
            }

            Query q = getEntityManager().createQuery(query.toString());

            List<StreamScope> results = q.getResultList();

            for (StreamScope stream : results)
            {
                log.info("processing stream: " + stream.getUniqueKey());

                // Populates the displayname from cache based on type
                if (stream.getScopeType() == ScopeType.PERSON)
                {
                    PersonModelView person = peopleMapper.fetchUniqueResult(stream.getUniqueKey());
                    if (person != null)
                    {
                        stream.setDisplayName(person.getDisplayName());
                        stream.setDestinationEntityId(person.getEntityId());
                    }
                }
                else if (stream.getScopeType() == ScopeType.GROUP)
                {
                    DomainGroupModelView group = groupsMapper.fetchUniqueResult(stream.getUniqueKey());
                    if (group != null)
                    {
                        stream.setDisplayName(group.getName());
                        stream.setDestinationEntityId(group.getEntityId());
                    }
                }
                streamMap.put(CacheKeys.STREAM_BY_ID + stream.getId(), stream);
            }
        }

        // Process all streams
        else
        {
            // The work below is broken out into 3 separate queries so we can
            // find the displayName
            // without having to use the mappers, which would force a loop over
            // all rows.

            // group query
            String groupQuery = "select new "
                    + "org.eurekastreams.server.domain.stream.StreamScope(g.name, ss.scopeType, "
                    + "ss.uniqueKey, ss.id, g.id) " + "FROM StreamScope ss, DomainGroup g "
                    + "where g.shortName = ss.uniqueKey and ss.scopeType = 'GROUP'";
            Query q = getEntityManager().createQuery(groupQuery);
            List<StreamScope> results = q.getResultList();

            // person query
            String personQuery = "select new "
                    + "org.eurekastreams.server.domain.stream.StreamScope(concat(p.preferredName, ' ', p.lastName), "
                    + "ss.scopeType, ss.uniqueKey, ss.id, p.id) "
                    + "FROM StreamScope ss, Person p where p.accountId = ss.uniqueKey and ss.scopeType = 'PERSON'";
            q = getEntityManager().createQuery(personQuery);
            results.addAll(q.getResultList());

            // all other scopeTypes query
            q = getEntityManager().createQuery(
                    "FROM StreamScope " + "where scopeType != 'GROUP' and scopeType != 'PERSON'");
            results.addAll(q.getResultList());

            for (StreamScope stream : results)
            {
                streamMap.put(CacheKeys.STREAM_BY_ID + stream.getId(), stream);
            }
        }

        for (String key : streamMap.keySet())
        {
            getCache().set(key, streamMap.get(key));
        }

        return streamMap;
    }
}
