/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.email;

import java.util.Map;

/**
 * Data returned from authenticating the message.
 */
public class MessageAuthenticationResult
{
    /** Certified metadata from the message. */
    private final Map<String, Long> metadata;

    /** Verified sender of the message. */
    private final String senderAddress;

    /**
     * Constructor.
     * 
     * @param inMetadata
     *            Metadata from the message.
     * @param inSenderAddress
     *            Sender of the message
     */
    public MessageAuthenticationResult(final Map<String, Long> inMetadata, final String inSenderAddress)
    {
        metadata = inMetadata;
        senderAddress = inSenderAddress;
    }

    /**
     * @return the metadata
     */
    public Map<String, Long> getMetadata()
    {
        return metadata;
    }

    /**
     * @return the senderAddress
     */
    public String getSenderAddress()
    {
        return senderAddress;
    }
}
