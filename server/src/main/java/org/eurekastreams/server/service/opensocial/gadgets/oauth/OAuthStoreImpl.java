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
package org.eurekastreams.server.service.opensocial.gadgets.oauth;

import net.oauth.OAuth;
import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;
import org.eurekastreams.server.persistence.OAuthTokenMapper;

import com.google.inject.Inject;

/**
 * Implementation of OAuthStore that uses mappers to retrieve and persist OAuth tokens and consumers.
 */
public class OAuthStoreImpl implements OAuthStore
{
    /**
     * Instance of OAuth consumer mapper injected by spring.
     */
    private OAuthConsumerMapper consumerMapper;

    /**
     * Instance of OAuth token mapper injected by spring.
     */
    private OAuthTokenMapper tokenMapper;

    /**
     * Instance of the transaction manager injected by spring for mapper calls.
     */
    private PlatformTransactionManager transMgr = null;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            the consumer mapper to initialize with.
     * @param inTokenMapper
     *            the token mapper to initialize with.
     * @param inTransMgr
     *            the transaction manager to initialize with.
     */
    @Inject
    public OAuthStoreImpl(final OAuthConsumerMapper inConsumerMapper, final OAuthTokenMapper inTokenMapper,
            final PlatformTransactionManager inTransMgr)
    {
        this.consumerMapper = inConsumerMapper;
        this.tokenMapper = inTokenMapper;
        this.transMgr = inTransMgr;
    }

    /**
     * Retrieves information about the OAuth consumer, most importantly the consumer key and secret.
     * 
     * @param securityToken
     *            the token itself.
     * @param serviceName
     *            the nickname for the service provider.
     * @param provider
     *            information about the OAuth service provider.
     * @return the persisted consumer information.
     * @throws GadgetException
     *             if the consumer was not configured.
     */
    public ConsumerInfo getConsumerKeyAndSecret(final SecurityToken securityToken, final String serviceName,
            final OAuthServiceProvider provider) throws GadgetException
    {
        OAuthConsumer oauthConsumer = consumerMapper.findConsumerByServiceNameAndGadgetUrl(serviceName, securityToken
                .getAppUrl());
        if (oauthConsumer == null)
        {
            throw new GadgetException(GadgetException.Code.INVALID_CONFIG, "OAuth Consumer was not found");
        }
        net.oauth.OAuthConsumer consumer = new net.oauth.OAuthConsumer(oauthConsumer.getCallbackURL(), oauthConsumer
                .getConsumerKey(), oauthConsumer.getConsumerSecret(), provider);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, oauthConsumer.getSignatureMethod());
        return new ConsumerInfo(consumer, null, oauthConsumer.getCallbackURL());
    }

    /**
     * Puts a token into the store.
     * 
     * @param securityToken
     *            the token itself.
     * @param consumerInfo
     *            information about the consumer.
     * @param serviceName
     *            the nickname for the service provider.
     * @param tokenName
     *            the name for the token.
     * @param tokenInfo
     *            information about the token.
     * @throws GadgetException
     *             if the consumer was not configured.
     */
    public void setTokenInfo(final SecurityToken securityToken, final ConsumerInfo consumerInfo,
            final String serviceName, final String tokenName, final TokenInfo tokenInfo) throws GadgetException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("UpdateTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);
        boolean isNullConsumer = true;
        try
        {
            OAuthConsumer consumer = consumerMapper.findConsumerByServiceNameAndGadgetUrl(serviceName, securityToken
                    .getAppUrl());
            if (consumer != null)
            {
                isNullConsumer = false;
                OAuthToken token = new OAuthToken(consumer, securityToken.getViewerId(), securityToken.getOwnerId(),
                        tokenInfo.getAccessToken(), tokenInfo.getTokenSecret());
                token.setTokenExpireMillis(tokenInfo.getTokenExpireMillis());
                tokenMapper.insert(token);
                transMgr.commit(transStatus);
            }
        }
        catch (Exception ex)
        {
            transMgr.rollback(transStatus);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, "An error occurred saving the data.");
        }

        if (isNullConsumer)
        {
            throw new GadgetException(GadgetException.Code.INVALID_CONFIG, "OAuth Consumer was not found");
        }
    }

    /**
     * Retreives the token from the store or null if the token was not found or has expired.
     * 
     * @param securityToken
     *            the token itself.
     * @param consumerInfo
     *            information about the consumer.
     * @param serviceName
     *            the nickname for the service provider.
     * @param tokenName
     *            the name for the token.
     * @return the persisted token.
     * @throws GadgetException
     *             if the consumer was not configured.
     */
    public TokenInfo getTokenInfo(final SecurityToken securityToken, final ConsumerInfo consumerInfo,
            final String serviceName, final String tokenName) throws GadgetException
    {
        OAuthConsumer consumer = consumerMapper.findConsumerByServiceNameAndGadgetUrl(serviceName, securityToken
                .getAppUrl());
        if (consumer == null)
        {
            throw new GadgetException(GadgetException.Code.INVALID_CONFIG, "OAuth Consumer was not found");
        }
        OAuthToken token = tokenMapper.findToken(consumer, securityToken.getViewerId(), securityToken.getOwnerId());
        if (token != null)
        {
            return new TokenInfo(token.getAccessToken(), token.getTokenSecret(), null, token.getTokenExpireMillis());
        }
        return null;
    }

    /**
     * Remove the token from the store.
     * 
     * @param securityToken
     *            the token itself.
     * @param consumerInfo
     *            information about the consumer.
     * @param serviceName
     *            the nickname for the service provider.
     * @param tokenName
     *            the name for the token.
     * @throws GadgetException
     *             if the consumer was not configured.
     */
    public void removeToken(final SecurityToken securityToken, final ConsumerInfo consumerInfo,
            final String serviceName, final String tokenName) throws GadgetException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("RemoveTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);
        boolean isNullConsumer = true;
        try
        {
            OAuthConsumer consumer = consumerMapper.findConsumerByServiceNameAndGadgetUrl(serviceName, securityToken
                    .getAppUrl());
            if (consumer != null)
            {
                isNullConsumer = false;
                tokenMapper.delete(consumer, securityToken.getViewerId(), securityToken.getOwnerId());
                transMgr.commit(transStatus);
            }
        }
        catch (Exception ex)
        {
            transMgr.rollback(transStatus);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR,
                    "An error occurred removing the data.");
        }

        if (isNullConsumer)
        {
            throw new GadgetException(GadgetException.Code.INVALID_CONFIG, "OAuth Consumer was not found");
        }
    }
}
