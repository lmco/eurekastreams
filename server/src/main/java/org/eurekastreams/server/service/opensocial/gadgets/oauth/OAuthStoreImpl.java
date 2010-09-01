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
package org.eurekastreams.server.service.opensocial.gadgets.oauth;

import net.oauth.OAuthServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.action.request.opensocial.GetConsumerInfoRequest;
import org.eurekastreams.server.action.request.opensocial.GetConsumerTokenInfoRequest;
import org.eurekastreams.server.action.request.opensocial.RemoveConsumerTokenRequest;
import org.eurekastreams.server.action.request.opensocial.SetConsumerTokenInfoRequest;
import org.eurekastreams.server.action.response.opensocial.ConsumerInfoResponse;
import org.eurekastreams.server.action.response.opensocial.TokenInfoResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * {@link OAuthStore} implementation that uses action framework to retrieve and persist OAuth tokens and consumers. This
 * implementation covers Shindig's role as an OAuth Proxy for Gadgets wishing to use external OAuth resources.
 */
public class OAuthStoreImpl implements OAuthStore
{
    /**
     * Local instance of logger.
     */
    private Log logger = LogFactory.make();

    /**
     * Instance of the {@link ActionController} for this class.
     */
    @Inject
    private ActionController actionController;

    /**
     * Instance of the getConsumerInfo Service Action.
     */
    private final ServiceAction getConsumerInfoAction;

    /**
     * Instance of the setConsumerTokenInfo Service Action.
     */
    private final ServiceAction setConsumerTokenInfoAction;

    /**
     * Instance of the getConsumerTokenInfo Service Action.
     */
    private final ServiceAction getConsumerTokenInfoAction;

    /**
     * Instance of the removeConsumerToken Service Action.
     */
    private final ServiceAction removeConsumerTokenAction;

    /**
     * Constructor.
     * 
     * @param inGetConsumerInfoAction
     *            instance of {@link ServiceAction} for CreateOAuthRequestToken Service Action.
     * @param inSetConsumerTokenInfoAction
     *            instance of {@link ServiceAction} for OAuthAuthorize.
     * @param inGetConsumerTokenInfoAction
     *            instance of UpdateRequestToAccessToken {@link ServiceAction}.
     * @param inRemoveConsumerTokenAction
     *            instance of GetOAuthEntryByToken {@link ServiceAction}.
     */
    @Inject
    public OAuthStoreImpl(@Named("getConsumerInfo") final ServiceAction inGetConsumerInfoAction,
            @Named("setConsumerTokenInfo") final ServiceAction inSetConsumerTokenInfoAction,
            @Named("getConsumerTokenInfo") final ServiceAction inGetConsumerTokenInfoAction,
            @Named("removeConsumerToken") final ServiceAction inRemoveConsumerTokenAction)
    {
        getConsumerInfoAction = inGetConsumerInfoAction;
        setConsumerTokenInfoAction = inSetConsumerTokenInfoAction;
        getConsumerTokenInfoAction = inGetConsumerTokenInfoAction;
        removeConsumerTokenAction = inRemoveConsumerTokenAction;
    }

    /**
     * Setter.
     * 
     * @param inServiceActionController
     *            instance of the {@link ActionController}.
     */
    public void setServiceActionController(final ActionController inServiceActionController)
    {
        actionController = inServiceActionController;
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
        ConsumerInfo consumerInfo = null;
        try
        {
            GetConsumerInfoRequest request = new GetConsumerInfoRequest(securityToken, serviceName, provider);
            ServiceActionContext currentContext = new ServiceActionContext(request, null);
            ConsumerInfoResponse response = (ConsumerInfoResponse) actionController.execute(currentContext,
                    getConsumerInfoAction);
            consumerInfo = response.getConsumerInfo();
        }
        catch (Exception ex)
        {
            logger.error("Error occurred getting consumer info.", ex);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return consumerInfo;
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
        try
        {
            SetConsumerTokenInfoRequest request = new SetConsumerTokenInfoRequest(securityToken, consumerInfo,
                    serviceName, tokenName, tokenInfo);
            ServiceActionContext currentContext = new ServiceActionContext(request, null);
            actionController.execute(currentContext, setConsumerTokenInfoAction);
        }
        catch (ExecutionException ex)
        {
            logger.error("Error occurred setting consumer token info.", ex);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    /**
     * Retrieves the token from the store or null if the token was not found or has expired.
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
        TokenInfo tokenInfo = null;
        try
        {
            GetConsumerTokenInfoRequest request = new GetConsumerTokenInfoRequest(securityToken, consumerInfo,
                    serviceName, tokenName);
            ServiceActionContext currentContext = new ServiceActionContext(request, null);
            TokenInfoResponse response = (TokenInfoResponse) actionController.execute(currentContext,
                    getConsumerTokenInfoAction);
            if (response != null)
            {
                tokenInfo = response.getTokenInfo();
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred getting consumer token info.", ex);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return tokenInfo;
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
        try
        {
            RemoveConsumerTokenRequest request = new RemoveConsumerTokenRequest(securityToken, consumerInfo,
                    serviceName, tokenName);
            ServiceActionContext currentContext = new ServiceActionContext(request, null);
            actionController.execute(currentContext, removeConsumerTokenAction);
        }
        catch (ExecutionException ex)
        {
            logger.error("Error occurred removing consumer token.", ex);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
