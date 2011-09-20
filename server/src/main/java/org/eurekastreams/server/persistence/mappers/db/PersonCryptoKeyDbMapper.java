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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Gets a user's encryption key.
 */
public class PersonCryptoKeyDbMapper extends BaseArgDomainMapper<Long, byte[]>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] execute(final Long inRequest)
    {
        String q = "SELECT cryptoKey FROM PersonCryptoKey WHERE personId = :personId";

        Query query = getEntityManager().createQuery(q).setParameter("personId", inRequest);
        List<byte[]> results = query.getResultList();
        return results.size() == 1 ? results.get(0) : null;
    }
}
