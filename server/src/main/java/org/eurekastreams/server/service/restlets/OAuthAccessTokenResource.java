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
package org.eurekastreams.server.service.restlets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.OAuth.Parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.OAuthConstants;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST endpoint class for generating OAuth access tokens.
 */
public class OAuthAccessTokenResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(OAuthAccessTokenResource.class);
    
    /**
     * The oauth data store to communicate with.
     */
    private OAuthDataStore dataStore;

    /**
     * The validator for verifying properly constructed OAuth requests.
     */
    public static final OAuthValidator VALIDATOR = new SimpleOAuthValidator();

    /**
     * Setter.
     * 
     * @param inDataStore
     *            the dataStore to set
     */
    public void setDataStore(final OAuthDataStore inDataStore)
    {
        dataStore = inDataStore;
    }

    /**
     * Initialize.
     * 
     * @param request
     *             the restlet request.     
     */
    @Override
    protected void initParams(final Request request)
    {
    }

    /**
     * Handle requests.
     * 
     * @param variant
     *            the variant to be retrieved.
     * @throws ResourceException
     *             thrown if a representation cannot be provided
     * @return a representation of the resource
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        log.trace("Request made to OAuthAccessTokenResource");
        try
        {
            OAuthMessage requestMessage = new RestletRequestMessage(this.getRequest());

            OAuthEntry entry = getValidatedEntry(requestMessage);
            if (entry == null)
            {
                throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
            }

            if (entry.callbackToken != null)
            {
                // We're using the fixed protocol
                String clientCallbackToken = requestMessage.getParameter(OAuthConstants.OAUTH_VERIFIER);
                if (!entry.callbackToken.equals(clientCallbackToken))
                {
                    dataStore.disableToken(entry);
                    throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "This token is not authorized");
                }
            }
            else if (!entry.authorized)
            {
                // Old protocol. Catch consumers trying to convert a token to one that's not authorized
                dataStore.disableToken(entry);
                throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "This token is not authorized");
            }

            // turn request token into access token
            OAuthEntry accessEntry = dataStore.convertToAccessToken(entry);

            List<Parameter> params = OAuth.newList(OAuth.OAUTH_TOKEN, accessEntry.token, OAuth.OAUTH_TOKEN_SECRET,
                    accessEntry.tokenSecret, "user_id", entry.userId);

            Representation rep = new StringRepresentation(OAuth.formEncode(params), MediaType.TEXT_PLAIN);
            return rep;
        }
        catch (IOException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, "");
        }
        catch (URISyntaxException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, "");
        }
        catch (OAuthException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
    }

    /**
     * Converts an OAuthMessage into a more meaningful, validated OAuthEntry.
     * @param requestMessage
     *          The oauth message.
     * @return a validated, populated OAuthEntry.
     * @throws IOException
     *          thrown if request could not be accessed.
     * @throws OAuthException
     *          thrown if a problem was found validating the message.
     * @throws URISyntaxException
     *          thrown if malformed URI was sent in the message.
     */
    private OAuthEntry getValidatedEntry(final OAuthMessage requestMessage) throws IOException,
            OAuthException, URISyntaxException
    {
        OAuthEntry entry = dataStore.getEntry(requestMessage.getToken());
        if (entry == null)
        {
            throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
        }

        if (entry.type != OAuthEntry.Type.REQUEST)
        {
            throw new OAuthProblemException(OAuth.Problems.TOKEN_USED);
        }

        if (entry.isExpired())
        {
            throw new OAuthProblemException(OAuth.Problems.TOKEN_EXPIRED);
        }

        // find consumer key, compare with supplied value, if present.

        if (requestMessage.getConsumerKey() == null)
        {
            OAuthProblemException e = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
            e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_CONSUMER_KEY);
            throw e;
        }

        String consumerKey = entry.consumerKey;
        if (!consumerKey.equals(requestMessage.getConsumerKey()))
        {
            throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_REFUSED);
        }

        OAuthConsumer consumer = dataStore.getConsumer(consumerKey);

        if (consumer == null)
        {
            throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
        }

        OAuthAccessor accessor = new OAuthAccessor(consumer);

        accessor.requestToken = entry.token;
        accessor.tokenSecret = entry.tokenSecret;

        VALIDATOR.validateMessage(requestMessage, accessor);

        return entry;
    }
}
