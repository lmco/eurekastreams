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
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * EntityCacheUpdater for DomainGroups.
 * 
 */
public class PersonEntityCacheUpdater extends CachedDomainMapper implements EntityCacheUpdater<Person>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.make();

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
        getCache().delete(CacheKeys.PERSON_BY_ID + inUpdatedPerson.getId());
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

}
