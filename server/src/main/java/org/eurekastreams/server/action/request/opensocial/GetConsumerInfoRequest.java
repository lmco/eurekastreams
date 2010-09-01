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

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;

/**
 * Request object that contains the parameters for calling the GetConsumerInfoExecution class.
 * 
 */
public class GetConsumerInfoRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -4559302769655606393L;

    /**
     * Instance of the {@link SecurityToken} for this request.
     */
    private SecurityToken securityToken;

    /**
     * Instance of the service name for this request.
     */
    private String serviceName;

    /**
     * Instance of the {@link OAuthServiceProvider} for this request.
     */
    private OAuthServiceProvider provider;

    /**
     * Constructor.
     * 
     * @param inSecurityToken
     *            security token for this request.
     * @param inServiceName
     *            service name for this request.
     * @param inProvider
     *            provider for this request.
     */
    public GetConsumerInfoRequest(final SecurityToken inSecurityToken, final String inServiceName,
            final OAuthServiceProvider inProvider)
    {
        setSecurityToken(inSecurityToken);
        setServiceName(inServiceName);
        setProvider(inProvider);
    }

    /**
     * @param inSecurityToken
     *            the securityToken to set
     */
    public void setSecurityToken(final SecurityToken inSecurityToken)
    {
        securityToken = inSecurityToken;
    }

    /**
     * @return the securityToken
     */
    public SecurityToken getSecurityToken()
    {
        return securityToken;
    }

    /**
     * @param inServiceName
     *            the serviceName to set
     */
    public void setServiceName(final String inServiceName)
    {
        serviceName = inServiceName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @param inProvider
     *            the provider to set
     */
    public void setProvider(final OAuthServiceProvider inProvider)
    {
        provider = inProvider;
    }

    /**
     * @return the provider
     */
    public OAuthServiceProvider getProvider()
    {
        return provider;
    }
}
