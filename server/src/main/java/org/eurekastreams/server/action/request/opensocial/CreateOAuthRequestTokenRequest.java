/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.request.opensocial;

import java.io.Serializable;

/**
 * Request object that contains the parameters for calling the CreateOAuthRequestToken Execution class.
 * 
 */
public class CreateOAuthRequestTokenRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 62888712565575131L;

    /**
     * Instance of the Consumer Key for this request.
     */
    private String consumerKey;

    /**
     * Instance of the OAuthVersion for this request.
     */
    private String oauthVersion;

    /**
     * Instance of the signedCallbackUrl for this request.
     */
    private String signedCallbackUrl;

    /**
     * Constructor.
     * @param inConsumerKey - consumer key for this request.
     * @param inOAuthVersion - OAuth version for this request.
     * @param inSignedCallbackUrl - Signed callback url for this request.
     */
    public CreateOAuthRequestTokenRequest(final String inConsumerKey, final String inOAuthVersion,
            final String inSignedCallbackUrl)
    {
        consumerKey = inConsumerKey;
        oauthVersion = inOAuthVersion;
        signedCallbackUrl = inSignedCallbackUrl;
    }

    /**
     * @return the consumerKey
     */
    public String getConsumerKey()
    {
        return consumerKey;
    }

    /**
     * @param inConsumerKey the consumerKey to set
     */
    public void setConsumerKey(final String inConsumerKey)
    {
        consumerKey = inConsumerKey;
    }

    /**
     * @return the oauthVersion
     */
    public String getOauthVersion()
    {
        return oauthVersion;
    }

    /**
     * @param inOAuthVersion the oauthVersion to set
     */
    public void setOauthVersion(final String inOAuthVersion)
    {
        oauthVersion = inOAuthVersion;
    }

    /**
     * @return the signedCallbackUrl
     */
    public String getSignedCallbackUrl()
    {
        return signedCallbackUrl;
    }

    /**
     * @param inSignedCallbackUrl the signedCallbackUrl to set
     */
    public void setSignedCallbackUrl(final String inSignedCallbackUrl)
    {
        signedCallbackUrl = inSignedCallbackUrl;
    }
    
    
}
