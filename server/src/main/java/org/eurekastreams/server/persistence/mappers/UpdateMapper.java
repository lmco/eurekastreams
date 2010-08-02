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
package org.eurekastreams.server.persistence.mappers;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Mapper used for updating DomainEntities.
 *
 * @param <TDomainEntityType>
 *            Type of DomainEntity.
 */
@SuppressWarnings("unchecked")
public class UpdateMapper<TDomainEntityType extends DomainEntity> extends
        BaseArgDomainMapper<PersistenceRequest, Boolean>
{
    /**
     * Additional updater to use, or ignore if null.
     */
    private DomainMapper<PersistenceRequest, Boolean> wrappedUpdater;

    /**
     * Empty constructor when no wrapped updater.
     */
    public UpdateMapper()
    {
    }

    /**
     * Constructor with a wrapped updater to call after calling self.
     *
     * @param inWrappedUpdater
     *            the updater to call after self
     */
    public UpdateMapper(final DomainMapper<PersistenceRequest, Boolean> inWrappedUpdater)
    {
        wrappedUpdater = inWrappedUpdater;
    }

    /**
     * Updates the DomainEntity.
     *
     * @param inRequest
     *            The MapperRequest.
     * @return true.
     */
    public Boolean execute(final PersistenceRequest inRequest)
    {
        flush();
        if (wrappedUpdater != null)
        {
            wrappedUpdater.execute(inRequest);
        }
        return true;
    }
}
