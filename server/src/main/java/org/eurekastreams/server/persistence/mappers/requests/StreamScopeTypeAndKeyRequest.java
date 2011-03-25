/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;

/**
 * Request for a stream scope including type and key.
 */
public class StreamScopeTypeAndKeyRequest implements Serializable
{
    /**
     * serial version id.
     */
    private static final long serialVersionUID = -4846680662880850445L;

    /**
     * The stream scope's type.
     */
    private ScopeType scopeType;

    /**
     * The stream unique key.
     */
    private String key;

    /**
     * Constructor.
     */
    public StreamScopeTypeAndKeyRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inScopeType
     *            The stream scope's type.
     * @param inKey
     *            The stream unique key.
     */
    public StreamScopeTypeAndKeyRequest(final ScopeType inScopeType, final String inKey)
    {
        scopeType = inScopeType;
        key = inKey;
    }

    /**
     * @return the scopeType
     */
    public ScopeType getScopeType()
    {
        return scopeType;
    }

    /**
     * @param inScopeType
     *            the scopeType to set
     */
    public void setScopeType(final ScopeType inScopeType)
    {
        scopeType = inScopeType;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param inKey
     *            the key to set
     */
    public void setKey(final String inKey)
    {
        key = inKey;
    }
}
