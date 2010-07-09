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
package org.eurekastreams.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents persistent login information for a user, used for "remember-me"
 * functionality.
 */
@SuppressWarnings("serial")
@Entity
public class PersistentLogin extends DomainEntity
{
    /**
     * The accountId of user (username).
     */
    @Column(nullable = false, unique = true)
    private String accountId;

    /**
     * The token expiration date (in milliseconds).
     */
    @Column(nullable = false)
    private long tokenExpirationDate;

    /**
     * The token value to be compared against user supplied value.
     */
    @Column(nullable = false, unique = true)
    private String tokenValue;

    /**
     * No-op constructor for ORM.
     */
    @SuppressWarnings("unused")
    private PersistentLogin()
    {
        // no-op.
    }

    /**
     * Constructor.
     *
     * @param inAccountId
     *            Id of user (username).
     * @param inTokenValue
     *            Token value.
     * @param inTokenExpirationDate
     *            Token expiration date.
     */
    public PersistentLogin(final String inAccountId, final String inTokenValue,
            final long inTokenExpirationDate)
    {
        accountId = inAccountId;
        tokenValue = inTokenValue;
        tokenExpirationDate = inTokenExpirationDate;
    }

    /**
     * Getter for accountId.
     *
     * @return The accountId.
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * Getter for tokenExpirationDate.
     *
     * @return The token expiration date (in milliseconds).
     */
    public long getTokenExpirationDate()
    {
        return tokenExpirationDate;
    }

    /**
     * Setter for tokenExpirationDate (milliseconds).
     *
     * @param inTokenExpirationDate
     *            The token expiration date.
     */
    public void setTokenExpirationDate(final long inTokenExpirationDate)
    {
        tokenExpirationDate = inTokenExpirationDate;
    }

    /**
     * Getter for tokenValue.
     *
     * @return The token value.
     */
    public String getTokenValue()
    {
        return tokenValue;
    }

    /**
     * Setter for tokenValue.
     *
     * @param inTokenValue
     *            The token value.
     */
    public void setTokenValue(final String inTokenValue)
    {
        tokenValue = inTokenValue;
    }

}
