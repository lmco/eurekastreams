/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * Sets showInStream field to false in DB and purges critical cache keys.
 * 
 */
public class HideResourceActivityMapper extends BaseArgCachedDomainMapper<Long, Void>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Sets showInStream field to false in DB and purges critical cache keys.
     * 
     * @param inRequest
     *            activity id.
     * @return null.
     */
    @Override
    public Void execute(final Long inRequest)
    {
        // set showInStream to false for activity.
        getEntityManager().createQuery("UPDATE Activity a SET a.showInStream = :showInStream WHERE id = :activityId")
                .setParameter("showInStream", false).setParameter("activityId", inRequest).executeUpdate();

        // remove from everyone list.
        getCache().removeFromList(CacheKeys.EVERYONE_ACTIVITY_IDS, inRequest);

        // remove activity from cache.
        log.info("Removing activity with id #" + inRequest + " from cache.");
        getCache().delete(CacheKeys.ACTIVITY_BY_ID + inRequest);

        log.info("Removing activity security dto with activity id #" + inRequest + " from cache.");
        getCache().delete(CacheKeys.ACTIVITY_SECURITY_BY_ID + inRequest);

        return null;
    }
}
