/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Remove person from cache for later update.
 */
public class RemovePersonFromCacheMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Remove a person from cache.
     *
     * @param inPerson
     *            the person to remove from cache
     */
    public void execute(final Person inPerson)
    {
        // clear the cache
        if (log.isInfoEnabled())
        {
            log.info("Removing person with id #" + inPerson.getId() + " from cache onUpdate.");
        }

        getCache().delete(CacheKeys.PERSON_BY_ID + inPerson.getId());

        // Removes the streams and searches for the user if this update was
        // triggered by a change in order of the lists on the activity page.
        getCache().delete(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + inPerson.getId());
        getCache().delete(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + inPerson.getId());

        if (log.isInfoEnabled())
        {
            log.info("Need to clear out the stream scope for person with id:" + inPerson.getId() + " - finding it by "
                    + inPerson.getAccountId());
        }

        StreamScope personScope = inPerson.getStreamScope();
        getCache().delete(CacheKeys.STREAM_BY_ID + personScope.getId());
        if (log.isInfoEnabled())
        {
            log.info("StreamScope for person with id: " + inPerson.getId() + ", stream scope id: "
                    + personScope.getId() + " deleted from cache.");
        }
    }
}
