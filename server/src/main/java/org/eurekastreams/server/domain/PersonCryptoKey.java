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
package org.eurekastreams.server.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Stores a person's encryption key.
 *
 * Maintained as a separate entity to prevent the key from being leaked through the API (GWT, Connect, or otherwise).
 */
@Entity
@Table(name = "Person_CryptoKey", uniqueConstraints = @UniqueConstraint(columnNames = { "personId" }))
@SuppressWarnings("serial")
public class PersonCryptoKey extends DomainEntity
{
    /** Person owning the key. */
    @Basic
    private long personId;

    /** Encryption key. */
    @Basic
    private byte[] cryptoKey;

    /**
     * Constructor.
     *
     * @param inPersonId
     *            Person owning the key.
     * @param inKey
     *            Encryption key.
     */
    public PersonCryptoKey(final long inPersonId, final byte[] inKey)
    {
        personId = inPersonId;
        cryptoKey = inKey;
    }

    /**
     * Constructor (no-op for ORM).
     */
    @SuppressWarnings("unused")
    private PersonCryptoKey()
    {
    }

    /**
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * @return the key
     */
    public byte[] getKey()
    {
        return cryptoKey;
    }

    /**
     * @param inKey
     *            the key to set
     */
    public void setKey(final byte[] inKey)
    {
        cryptoKey = inKey;
    }
}
