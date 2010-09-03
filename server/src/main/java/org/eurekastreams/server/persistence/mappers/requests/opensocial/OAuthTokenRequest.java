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

import org.eurekastreams.server.domain.OAuthConsumer;

/**
 * Request object for mapper calls needing an OAuthToken.
 */
public class OAuthTokenRequest
{
    /**
     * The oauth consumer associated with this token.
     */
    private OAuthConsumer consumer;

    /**
     * The gadget viewer id for this token.
     */
    private String viewerId;

    /**
     * The gadget owner id for this token.
     */
    private String ownerId;

    /**
     * Constructor for the OAuthTokenRequest object.
     * 
     * @param inConsumer
     *            - OAuth consumer for this request.
     * @param inViewerId
     *            - string containing the viewer id.
     * @param inOwnerId
     *            - string containing the owner id.
     */
    public OAuthTokenRequest(final OAuthConsumer inConsumer, final String inViewerId, final String inOwnerId)
    {
        consumer = inConsumer;
        viewerId = inViewerId;
        ownerId = inOwnerId;
    }

    /**
     * @param inViewerId
     *            the viewerId to set
     */
    public void setViewerId(final String inViewerId)
    {
        viewerId = inViewerId;
    }

    /**
     * @return the viewerId
     */
    public String getViewerId()
    {
        return viewerId;
    }

    /**
     * @param inOwnerId
     *            the ownerId to set
     */
    public void setGadgetUrl(final String inOwnerId)
    {
        ownerId = inOwnerId;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId()
    {
        return ownerId;
    }

    /**
     * @param inConsumer
     *            the consumer to set
     */
    public void setConsumer(final OAuthConsumer inConsumer)
    {
        consumer = inConsumer;
    }

    /**
     * @return the consumer
     */
    public OAuthConsumer getConsumer()
    {
        return consumer;
    }
}
