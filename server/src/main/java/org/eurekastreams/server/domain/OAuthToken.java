/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Cascade;

/**
 * Represents the OAuth access token and related information needed during an OAuth request.
 */
@SuppressWarnings("serial")
@Entity
public class OAuthToken extends DomainEntity implements Serializable
{
    /**
     * The OAuthConsumer that initiated the request for this token.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumerId")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private OAuthConsumer consumer;

    /**
     * The gadget viewer requesting the token.
     */
    @Column(nullable = false)
    private String viewerId;

    /**
     * The gadget owner requesting the token.
     */
    @Column(nullable = false)
    private String ownerId;

    /**
     * The access token given by the service provider.
     */
    @Column(nullable = false)
    private String accessToken;

    /**
     * The token secret given by the service provider.
     */
    @Column(nullable = false)
    private String tokenSecret;

    /**
     * The time (in milliseconds since epoch) when the token expires.
     */
    @Column(nullable = true)
    private Long tokenExpireMillis;

    /**
     * Default constructor.
     */
    protected OAuthToken()
    {
    }

    /**
     * Default constructor.
     * 
     * @param inConsumer
     *            the related OAuth consumer.
     * @param inViewerId
     *            the viewer id.
     * @param inOwnerId
     *            the owner id.
     * @param inAccessToken
     *            the access token.
     * @param inTokenSecret
     *            the token secret.
     */
    public OAuthToken(final OAuthConsumer inConsumer, final String inViewerId, final String inOwnerId,
            final String inAccessToken, final String inTokenSecret)
    {
        consumer = inConsumer;
        viewerId = inViewerId;
        ownerId = inOwnerId;
        accessToken = inAccessToken;
        tokenSecret = inTokenSecret;
    }

    /**
     * @return the consumer
     */
    public OAuthConsumer getConsumer()
    {
        return consumer;
    }

    /**
     * @param inConsumer
     *            the consumer to set
     */
    public void setConsumer(final OAuthConsumer inConsumer)
    {
        consumer = inConsumer;
    }

    /**
     * @return the viewerId
     */
    public String getViewerId()
    {
        return viewerId;
    }

    /**
     * @param inViewerId
     *            the viewerId to set
     */
    public void setViewerId(final String inViewerId)
    {
        viewerId = inViewerId;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId()
    {
        return ownerId;
    }

    /**
     * @param inOwnerId
     *            the ownerId to set
     */
    public void setOwnerId(final String inOwnerId)
    {
        ownerId = inOwnerId;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken()
    {
        return accessToken;
    }

    /**
     * @param inAccessToken
     *            the accessToken to set
     */
    public void setAccessToken(final String inAccessToken)
    {
        accessToken = inAccessToken;
    }

    /**
     * @return the tokenSecret
     */
    public String getTokenSecret()
    {
        return tokenSecret;
    }

    /**
     * @param inTokenSecret
     *            the tokenSecret to set
     */
    public void setTokenSecret(final String inTokenSecret)
    {
        tokenSecret = inTokenSecret;
    }

    /**
     * @return the tokenExpireMillis
     */
    public Long getTokenExpireMillis()
    {
        return tokenExpireMillis;
    }

    /**
     * @param inTokenExpireMillis
     *            the tokenExpireMillis to set
     */
    public void setTokenExpireMillis(final Long inTokenExpireMillis)
    {
        tokenExpireMillis = inTokenExpireMillis;
    }
}
