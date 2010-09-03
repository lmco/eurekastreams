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
package org.eurekastreams.server.persistence.mappers.requests.opensocial;

/**
 * Request object for mapper calls needing an OAuthConsumer.
 */
public class OAuthConsumerRequest
{
    /**
     * The name of the oauth service provider that the consumer is configured to access.
     */
    private String serviceProviderName;

    /**
     * The url of the gadget that is making the oauth request.
     */
    private String gadgetUrl;

    /**
     * Constructor for the OAuthConsumerRequest object.
     * 
     * @param inServiceProviderName
     *            - string containing the oauth service provider name.
     * @param inGadgetUrl
     *            - string containing the gadget url.
     */
    public OAuthConsumerRequest(final String inServiceProviderName, final String inGadgetUrl)
    {
        serviceProviderName = inServiceProviderName;
        gadgetUrl = inGadgetUrl;
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
     * @return the serviceProviderName
     */
    public String getServiceProviderName()
    {
        return serviceProviderName;
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
     * @return the gadgetUrl
     */
    public String getGadgetUrl()
    {
        return gadgetUrl;
    }
}
