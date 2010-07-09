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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Add a person as a coordinator for a domain group - in the cache.
 */
public class SaveDomainGroupCoordinatorsListToCache extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SaveDomainGroupCoordinatorsListToCache.class);

    /**
     * Save a set of coordinator person ids for a domain group in cache.
     * 
     * @param inDomainGroup
     *            the domain group to set the coordinator cache for
     * @return the list of coordinator people ids
     */
    public List<Long> execute(final DomainGroup inDomainGroup)
    {
        // build up the list of of coordinator person ids
        List<Long> coordinatorPeopleIds = new ArrayList<Long>();
        for (Person coordinator : inDomainGroup.getCoordinators())
        {
            coordinatorPeopleIds.add(coordinator.getId());
        }

        log.info("Saving group #" + inDomainGroup.getId()
                + "'s cordinators to cache - coordinator ids: "
                + coordinatorPeopleIds.toString());

        getCache().setList(
                CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID
                        + inDomainGroup.getId(), coordinatorPeopleIds);

        return coordinatorPeopleIds;
    }
}
