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
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Gets a list of composite stream ids for a given user.
 */
public class UserCompositeStreamIdsMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(UserCompositeStreamIdsMapper.class);
    
    /**
     * Looks in the cache for composite streams for a given user. If data is not cached, goes to database.
     * 
     * @param userId
     *            the user id to find composite stream ids for.
     * @return the list of composite stream ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final long userId)
    {
        String key = CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + userId;

        // Looks for the item in the cache
        List<Long> compositeStreamKeys = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (compositeStreamKeys == null)
        {
            // Get the person entity - wanted to do this by querying for streamViews directly
            // but maintaining the correct indexed order is much harder than it would seem.
            Query q = getEntityManager().createQuery("from Person p WHERE p.id =:personId").setParameter("personId",
                    userId);

            try
            {
                compositeStreamKeys = new ArrayList<Long>();
                Person person = (Person) q.getSingleResult();
                List<StreamView> results = person.getStreamViews();

                for (StreamView stream : results)
                {
                    compositeStreamKeys.add(stream.getId());
                }

                getCache().setList(key, compositeStreamKeys);
            }
            // Person was not found, so no composite stream searches to populate.
            catch (NoResultException e)
            {
                log.info("Person not found.");
            }

        }

        return compositeStreamKeys;
    }
}
