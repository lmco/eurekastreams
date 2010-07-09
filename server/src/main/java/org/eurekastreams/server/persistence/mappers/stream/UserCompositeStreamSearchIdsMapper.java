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
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Gets a list of composite stream search ids for a given user.
 */
public class UserCompositeStreamSearchIdsMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(UserCompositeStreamSearchIdsMapper.class);

    /**
     * Looks in the cache for composite stream searches for a given user. If data is not cached, goes to database.
     * 
     * @param userId
     *            the user id to find composite stream search ids for.
     * @return the list of composite stream search ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final long userId)
    {
        String key = CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + userId;

        // Looks for the item in the cache
        List<Long> compositeStreamSearchKeys = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (compositeStreamSearchKeys == null)
        {
            // Get the person entity - wanted to do this by querying for streamViews directly
            // but maintaining the correct indexed order is much harder than it would seem.
            Query q = getEntityManager().createQuery("from Person p WHERE p.id =:personId").setParameter("personId",
                    userId);

            try
            {
                compositeStreamSearchKeys = new ArrayList<Long>();
                Person person = (Person) q.getSingleResult();
                List<StreamSearch> results = person.getStreamSearches();

                for (StreamSearch stream : results)
                {
                    compositeStreamSearchKeys.add(stream.getId());
                }

                getCache().setList(key, compositeStreamSearchKeys);
            }
            // Person was not found, so no composite stream searches to populate.
            catch (NoResultException e)
            {
                log.info("Person not found.");
            }
        }

        return compositeStreamSearchKeys;
    }
}
