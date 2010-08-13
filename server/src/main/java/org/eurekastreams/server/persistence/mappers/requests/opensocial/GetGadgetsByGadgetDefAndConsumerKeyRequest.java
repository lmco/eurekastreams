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

import java.io.Serializable;

/**
 * This request object contains an OAuth Consumer Key along with a User Id.
 *
 */
public class GetGadgetsByGadgetDefAndConsumerKeyRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -2035444386134392679L;
    
    /**
     * String based consumer key.
     */
    private final String consumerKey;
    
    /**
     * Long person id.
     */
    private final Long personId;
    
    /**
     * Constructor.
     * @param inConsumerKey - string based consumer key.
     * @param inPersonId - long user id.
     */
    public GetGadgetsByGadgetDefAndConsumerKeyRequest(final String inConsumerKey, final Long inPersonId)
    {
        consumerKey = inConsumerKey;
        personId = inPersonId;
    }

    /**
     * @return the consumerKey
     */
    public String getConsumerKey()
    {
        return consumerKey;
    }

    /**
     * @return the personId
     */
    public Long getPersonId()
    {
        return personId;
    }

}
