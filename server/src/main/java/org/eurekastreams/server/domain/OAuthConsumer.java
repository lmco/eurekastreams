/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Cascade;

/**
 * Represents a consumer of data from an OAuth-enabled service provider.
 */
@SuppressWarnings("serial")
@Entity
public class OAuthConsumer extends DomainEntity implements Serializable, Identifiable
{
    /**
     * The name of the service provider - must match the name defined in the gadget definition.
     */
    @Column(nullable = false)
    private String serviceProviderName;

    /**
     * The url of the gadget acting as the OAuth consumer.
     */
    @Column(nullable = false)
    private String gadgetUrl;

    /**
     * The key (a username, essentially) negoatiated between the consumer and the service provider.
     */
    @Column(nullable = false)
    private String consumerKey;

    /**
     * The secret negotiated between the consumer and the service provider.
     */
    @Column(nullable = false)
    private String consumerSecret;

    /**
     * The method used to sign requests (HMAC-SHA1 or RSA-SHA1).
     */
    @Column(nullable = false)
    private String signatureMethod;

    /**
     * The callback URL - used by the service provider in 1.0 A version of the OAuth spec.
     */
    @Column(nullable = true)
    private String callbackURL;

    /**
     * The consumer title.
     */
    @Column(nullable = true)
    private String title;

    /**
     * The tokens assigned for this consumer.
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "consumerId")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL,
                org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<OAuthToken> tokens;

    /**
     * Default constructor.
     */
    protected OAuthConsumer()
    {
    }

    /**
     * Default constructor.
     *
     * @param inServiceProviderName
     *            the service provider name.
     * @param inGadgetUrl
     *            the gadget url.
     * @param inConsumerKey
     *            the consumer key.
     * @param inConsumerSecret
     *            the consumer secret.
     * @param inSignatureMethod
     *            the signature method.
     */
    public OAuthConsumer(final String inServiceProviderName, final String inGadgetUrl, final String inConsumerKey,
            final String inConsumerSecret, final String inSignatureMethod)
    {
        serviceProviderName = inServiceProviderName;
        gadgetUrl = inGadgetUrl;
        consumerKey = inConsumerKey;
        consumerSecret = inConsumerSecret;
        signatureMethod = inSignatureMethod;
        tokens = new ArrayList<OAuthToken>();
    }

    /**
     * @return the tokens
     */
    public List<OAuthToken> getTokens()
    {
        return tokens;
    }

    /**
     * @param inTokens
     *          the tokens to set
     */
    public void setTokens(final List<OAuthToken> inTokens)
    {
        tokens = inTokens;
    }

    /**
     * @return the serviceProviderName
     */
    public String getServiceProviderName()
    {
        return serviceProviderName;
    }

    /**
     * @param inServiceProviderName
     *            the serviceProviderName to set
     */
    public void setServiceProviderName(final String inServiceProviderName)
    {
        serviceProviderName = inServiceProviderName;
    }

    /**
     * @return the gadgetUrl
     */
    public String getGadgetUrl()
    {
        return gadgetUrl;
    }

    /**
     * @param inGadgetUrl
     *            the gadgetUrl to set
     */
    public void setGadgetUrl(final String inGadgetUrl)
    {
        gadgetUrl = inGadgetUrl;
    }

    /**
     * @return the consumerKey
     */
    public String getConsumerKey()
    {
        return consumerKey;
    }

    /**
     * @param inConsumerKey
     *            the consumerKey to set
     */
    public void setConsumerKey(final String inConsumerKey)
    {
        consumerKey = inConsumerKey;
    }

    /**
     * @return the consumerSecret
     */
    public String getConsumerSecret()
    {
        return consumerSecret;
    }

    /**
     * @param inConsumerSecret
     *            the consumerSecret to set
     */
    public void setConsumerSecret(final String inConsumerSecret)
    {
        consumerSecret = inConsumerSecret;
    }

    /**
     * @return the signatureMethod
     */
    public String getSignatureMethod()
    {
        return signatureMethod;
    }

    /**
     * @param inSignatureMethod
     *            the signatureMethod to set
     */
    public void setSignatureMethod(final String inSignatureMethod)
    {
        signatureMethod = inSignatureMethod;
    }

    /**
     * @return the callbackURL
     */
    public String getCallbackURL()
    {
        return callbackURL;
    }

    /**
     * @param inCallbackURL
     *            the callbackURL to set
     */
    public void setCallbackURL(final String inCallbackURL)
    {
        callbackURL = inCallbackURL;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        title = inTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityId()
    {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId()
    {
        return consumerKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType()
    {
        return EntityType.APPLICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName()
    {
        return title;
    }
}
