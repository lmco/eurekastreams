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
package org.eurekastreams.server.action.response.opensocial;

import java.io.Serializable;

import org.apache.shindig.social.core.oauth.OAuthSecurityToken;

/**
 * This class is a simple wrapper for the {@link OAuthSecurityToken} object that is not serializable.
 * Since the Action Framework ExecutionStrategy interface requires a serializable response, 
 * {@link OAuthSecurityToken} could not be returned directly, so it is wrapped in this response object.
 * This response is not actually serialized, but satisfies the interface requirements since {@link OAuthSecurityToken}
 * is an external object.
 *
 */
public class SecurityTokenResponse implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 4936445959437635003L;

    /**
     * Local instance of the {@link OAuthSecurityToken}.
     */
    private final OAuthSecurityToken securityToken;
    
    /**
     * Constructor.
     * @param inSecurityToken - instance of an {@link OAuthSecurityToken}.
     */
    public SecurityTokenResponse(final OAuthSecurityToken inSecurityToken)
    {
        securityToken = inSecurityToken;
    }
    
    /**
     * Retrieve the {@link OAuthSecurityToken}.
     * @return instance of the {@link OAuthSecurityToken} provided.
     */
    public OAuthSecurityToken getSecurityToken()
    {
        return securityToken;
    }  
}
