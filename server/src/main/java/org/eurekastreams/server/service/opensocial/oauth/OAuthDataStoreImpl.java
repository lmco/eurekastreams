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
package org.eurekastreams.server.service.opensocial.oauth;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.eurekastreams.server.action.request.opensocial.CreateOAuthRequestTokenRequest;
import org.eurekastreams.server.action.response.opensocial.SecurityTokenResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * {@link OAuthDataStore} implementation that is used during both 2 and 3-legged OAuth authorizations from Shindig.
 * These methods are called
 */
public class OAuthDataStoreImpl implements OAuthDataStore
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
     * Instance of the {@link PrincipalPopulatorTransWrapper} for the OpenSocialPrincipalPopulator.
     */
    @Inject
    private PrincipalPopulatorTransWrapper principalPopulator;

    /**
     * Instance of the CreateOauthRequestToken Service Action.
     */
    private final ServiceAction createOAuthRequestTokenAction;

    /**
     * Instance of the AuthorizeOAuthToken Service Action.
     */
    private final ServiceAction authorizeOAuthTokenAction;

    /**
     * Instance of the UpdateRequestToAccessToken Service Action.
     */
    private final ServiceAction updateRequestToAccessTokenAction;

    /**
     * Instance of the GetOAuthEntryByToken Service Action.
     */
    private final ServiceAction getOAuthEntryByTokenAction;

    /**
     * Instance of the DisableOAuthToken Service Action.
     */
    private final ServiceAction disableOAuthTokenAction;

    /**
     * Instance of the RemoveOAuthToken Service Action.
     */
    private final ServiceAction removeOAuthTokenAction;

    /**
     * Instance of the GetOauthConsumerByConsumerKeyAction Service Action.
     */
    private final ServiceAction getOAuthConsumerByConsumerKeyAction;

    /**
     * Instance of the GetSecurityToken For Conumer Request Service Action.
     */
    private final ServiceAction getSecurityTokenForConsumerRequestAction;

    /**
     * Constructor.
     * 
     * @param inCreateOAuthRequestTokenAction
     *            - instance of {@link ServiceAction} for CreateOAuthRequestToken Service Action.
     * @param inAuthorizeOAuthTokenAction
     *            - instance of {@link ServiceAction} for OAuthAuthorize.
     * @param inUpdateRequestToAccessTokenAction
     *            - instance of UpdateRequestToAccessToken {@link ServiceAction}.
     * @param inGetOAuthEntryByTokenAction
     *            - instance of GetOAuthEntryByToken {@link ServiceAction}.
     * @param inDisableOAuthTokenAction
     *            - instance of DisableOAuthToken {@link ServiceAction}.
     * @param inRemoveOAuthTokenAction
     *            - instance of RemoveOAuthToken {@link ServiceAction}.
     * @param inGetOAuthConsumerByConsumerKeyAction
     *            - instance of GetOAuthConsumerByConsumerKey {@link ServiceAction}.
     * @param inGetSecurityTokenForConsumerRequestAction
     *            - instance of GetSecurityTokenForConsumerRequest {@link ServiceAction}.
     */
    @Inject
    public OAuthDataStoreImpl(@Named("createOAuthRequestToken") final ServiceAction inCreateOAuthRequestTokenAction,
            @Named("authorizeOAuthToken") final ServiceAction inAuthorizeOAuthTokenAction,
            @Named("updateRequestToAccessToken") final ServiceAction inUpdateRequestToAccessTokenAction,
            @Named("getOAuthEntryByToken") final ServiceAction inGetOAuthEntryByTokenAction,
            @Named("disableOAuthToken") final ServiceAction inDisableOAuthTokenAction,
            @Named("removeOAuthToken") final ServiceAction inRemoveOAuthTokenAction,
            @Named("getOAuthConsumerByConsumerKey") final ServiceAction inGetOAuthConsumerByConsumerKeyAction,
            @Named("getSecurityTokenForConsumerRequest") final ServiceAction inGetSecurityTokenForConsumerRequestAction)
    {
        createOAuthRequestTokenAction = inCreateOAuthRequestTokenAction;
        authorizeOAuthTokenAction = inAuthorizeOAuthTokenAction;
        updateRequestToAccessTokenAction = inUpdateRequestToAccessTokenAction;
        getOAuthEntryByTokenAction = inGetOAuthEntryByTokenAction;
        disableOAuthTokenAction = inDisableOAuthTokenAction;
        removeOAuthTokenAction = inRemoveOAuthTokenAction;
        getOAuthConsumerByConsumerKeyAction = inGetOAuthConsumerByConsumerKeyAction;
        getSecurityTokenForConsumerRequestAction = inGetSecurityTokenForConsumerRequestAction;
    }

    /**
     * Setter.
     * 
     * @param inServiceActionController
     *            - instance of the {@link ActionController}.
     */
    public void setServiceActionController(final ActionController inServiceActionController)
    {
        actionController = inServiceActionController;
    }

    /**
     * Setter.
     * 
     * @param inPrincipalPopulatorTransWrapper
     *            - instance of {@link PrincipalPopulatorTransWrapper}.
     */
    public void setPrincipalPopulatorTransWrapper(final PrincipalPopulatorTransWrapper inPrincipalPopulatorTransWrapper)
    {
        principalPopulator = inPrincipalPopulatorTransWrapper;
    }

    /**
     * Creates a request token for a new OAuth request.
     * 
     * @param consumerKey
     *            the consumer key for this request.
     * @param oauthVersion
     *            the version of the oauth protocol used in this request.
     * @param signedCallbackUrl
     *            the callback url (needed for OAuth 1.0 A).
     * @return the entry containing the new token.
     * @throws OAuthProblemException
     *             thrown when token could not be persisted.
     */
    public OAuthEntry generateRequestToken(final String consumerKey, final String oauthVersion,
            final String signedCallbackUrl) throws OAuthProblemException
    {
        try
        {
            CreateOAuthRequestTokenRequest currentRequest = new CreateOAuthRequestTokenRequest(consumerKey,
                    oauthVersion, signedCallbackUrl);
            ServiceActionContext currentContext = new ServiceActionContext(currentRequest, null);
            return (OAuthEntry) actionController.execute(currentContext, createOAuthRequestTokenAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred persisting request token information", ex);
            throw new OAuthProblemException("Unable to persist request token information");
        }
    }

    /**
     * Authorize the access token.
     * 
     * @param entry
     *            the entry to authorize.
     * @param userId
     *            the user making the request.
     * @throws OAuthProblemException
     *             thrown when the token could not be persisted.
     */
    public void authorizeToken(final OAuthEntry entry, final String userId) throws OAuthProblemException
    {
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(entry.getToken(), principalPopulator
                    .getPrincipal(entry.getUserId(), ""));
            actionController.execute(currentContext, authorizeOAuthTokenAction);
        }
        catch (ExecutionException ex)
        {
            logger.error("Error occurred authorizing token", ex);
            throw new OAuthProblemException("Unable to authorize token");
        }
    }

    /**
     * Exchange a request token for an access token.
     * 
     * @param entry
     *            the entry to authorize.
     * @return the entry with the access token.
     * @throws OAuthProblemException
     *             thrown when the token could not be persisted.
     */
    public OAuthEntry convertToAccessToken(final OAuthEntry entry) throws OAuthProblemException
    {
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(entry, principalPopulator.getPrincipal(entry
                    .getUserId(), ""));
            return (OAuthEntry) actionController.execute(currentContext, updateRequestToAccessTokenAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred converting request token to access token", ex);
            throw new OAuthProblemException("Unable to convert request token to access token");
        }
    }

    /**
     * Return the consumer for a given key.
     * 
     * @param consumerKey
     *            the key.
     * @return the found consumer.
     * @throws OAuthProblemException
     *             thrown when there was a problem retrieving the consumer information.
     */
    public OAuthConsumer getConsumer(final String consumerKey) throws OAuthProblemException
    {
        OAuthConsumer consumer = null;

        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(consumerKey, null);
            consumer = (OAuthConsumer) actionController.execute(currentContext, getOAuthConsumerByConsumerKeyAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving consumer with provided key.", ex);
            throw new OAuthProblemException("Unable to retrieve consumer with provided information.");
        }
        return consumer;
    }

    /**
     * Get an entry for a specific oauth token.
     * 
     * @param oauthToken
     *            the token.
     * @return the associated entry.
     */
    public OAuthEntry getEntry(final String oauthToken)
    {
        OAuthEntry entry = null;
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(oauthToken, null);
            entry = (OAuthEntry) actionController.execute(currentContext, getOAuthEntryByTokenAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving OAuthEntry based on the token.", ex);
        }

        return entry;
    }

    /**
     * Marks a token as disabled.
     * 
     * @param entry
     *            the OAuthEntry to disable.
     */
    public void disableToken(final OAuthEntry entry)
    {
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(entry.getToken(), null);
            actionController.execute(currentContext, disableOAuthTokenAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred disabling token.", ex);
        }
    }

    /**
     * Deletes a token.
     * 
     * @param entry
     *            the OAuthEntry to remove.
     */
    public void removeToken(final OAuthEntry entry)
    {
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(entry.getToken(), null);
            actionController.execute(currentContext, removeOAuthTokenAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred removing token.", ex);
        }
    }

    /**
     * Return the proper security token for a 2 legged oauth request that has been validated for the given consumerKey.
     * App specific checks like making sure the requested user has the app installed are handled by the authorization
     * strategy of the ServiceAction being executed.
     * 
     * @param consumerKey
     *            the consumer making the oauth request.
     * @param userId
     *            the userId for this request.
     * @return the found securitytoken.
     * @throws OAuthProblemException
     *             thrown when the consumer key was not found.
     */
    public SecurityToken getSecurityTokenForConsumerRequest(final String consumerKey, final String userId)
            throws OAuthProblemException
    {
        SecurityTokenResponse response = null;
        try
        {
            // Currently, this supports two legged oauth with "reverse call home" where the server hosting an
            // application
            // wishes to request information about a user that has their application installed on their start page.
            ServiceActionContext currentContext = new ServiceActionContext(consumerKey, principalPopulator
                    .getPrincipal(userId, ""));
            response = (SecurityTokenResponse) actionController.execute(currentContext,
                    getSecurityTokenForConsumerRequestAction);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving security token for Consumer Request.", ex);
            throw new OAuthProblemException("Error occurred retrieving security token for Consumer Request.");
        }
        return response != null ? response.getSecurityToken() : null;
    }
}
