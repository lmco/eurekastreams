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
package org.eurekastreams.server.persistence.mappers;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Mapper which creates an encryption key.
 */
public class CreateCryptoKeyMapper implements DomainMapper<Long, byte[]>
{
    /** Key generator. */
    private final KeyGenerator keyGenerator;

    /**
     * Constructor.
     * 
     * @param inKeyGenerator
     *            Key generator.
     */
    public CreateCryptoKeyMapper(final KeyGenerator inKeyGenerator)
    {
        keyGenerator = inKeyGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] execute(final Long inRequest)
    {
        SecretKey key = keyGenerator.generateKey();
        byte[] keyBytes = key.getEncoded();
        return keyBytes;
    }
}
