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

import org.eurekastreams.server.domain.PersonCryptoKey;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;

/**
 * Saves a user's encryption key.
 */
public class PersonCryptoKeyDbRefreshStrategy extends BaseDomainMapper implements RefreshStrategy<Long, byte[]>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final Long inRequest, final byte[] inResponse)
    {
        PersonCryptoKey entity = new PersonCryptoKey(inRequest, inResponse);
        getEntityManager().persist(entity);
    }
}
