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
 * REST endpoint class for generating OAuth request tokens.
 */
public class OAuthRequestTokenResource extends SmpResource
{
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
     *            the restlet request.
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
        OAuthMessage requestMessage = new RestletRequestMessage(this.getRequest());

        try
        {
            String consumerKey = requestMessage.getConsumerKey();
            if (consumerKey == null)
            {
                throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED,
                        OAuth.Problems.OAUTH_PARAMETERS_ABSENT + OAuth.OAUTH_CONSUMER_KEY);
            }
            OAuthConsumer consumer = dataStore.getConsumer(consumerKey);

            if (consumer == null)
            {
                throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED,
                        OAuth.Problems.CONSUMER_KEY_UNKNOWN);
            }

            OAuthAccessor accessor = new OAuthAccessor(consumer);
            VALIDATOR.validateMessage(requestMessage, accessor);

            String callback = requestMessage.getParameter(OAuth.OAUTH_CALLBACK);
            /*
             * if (enableSignedCallbacks) { callback = requestMessage.getParameter(OAuth.OAUTH_CALLBACK);; } if
             * (callback == null && !enableOAuth10) { OAuthProblemException e = new
             * OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
             * e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_CALLBACK); throw e; }
             */

            // generate request_token and secret
            OAuthEntry entry = dataStore.generateRequestToken(consumerKey, requestMessage
                    .getParameter(OAuth.OAUTH_VERSION), callback);

            List<Parameter> responseParams = OAuth.newList(OAuth.OAUTH_TOKEN, entry.getToken(), 
                    OAuth.OAUTH_TOKEN_SECRET, entry.getTokenSecret());
            if (callback != null)
            {
                responseParams.add(new Parameter(OAuth.OAUTH_CALLBACK_CONFIRMED, "true"));
            }

            Representation rep = new StringRepresentation(OAuth.formEncode(responseParams), MediaType.TEXT_PLAIN);
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
        catch (OAuthProblemException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getProblem());
        }
        catch (OAuthException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_EXPECTATION_FAILED, e.getMessage());
        }
    }
}
