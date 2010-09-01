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

import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;

/**
 * This class is a simple wrapper for the {@link ConsumerInfo} object that is not serializable.
 * Since the Action Framework ExecutionStrategy interface requires a serializable response, 
 * {@link ConsumerInfo} could not be returned directly, so it is wrapped in this response object.
 * This response is not actually serialized, but satisfies the interface requirements since {@link ConsumerInfo}
 * is an external object.
 *
 */
public class ConsumerInfoResponse implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -5627154487042614365L;

    /**
     * Local instance of the {@link ConsumerInfo}.
     */
    private final ConsumerInfo consumerInfo;
    
    /**
     * Constructor.
     * @param inConsumerInfo - instance of an {@link ConsumerInfo}.
     */
    public ConsumerInfoResponse(final ConsumerInfo inConsumerInfo)
    {
        consumerInfo = inConsumerInfo;
    }
    
    /**
     * Retrieve the {@link ConsumerInfo}.
     * @return instance of the {@link ConsumerInfo} provided.
     */
    public ConsumerInfo getConsumerInfo()
    {
        return consumerInfo;
    }  
}
