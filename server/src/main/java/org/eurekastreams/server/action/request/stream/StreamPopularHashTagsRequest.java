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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;

/**
 * Request to represent the stream to get popular hashtags for.
 */
public class StreamPopularHashTagsRequest implements Serializable
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5986595093579218944L;

    /**
     * The type of entity representing the stream.
     */
    private ScopeType streamEntityScopeType;

    /**
     * The unique key/short name of the stream's entity.
     */
    private String streamEntityUniqueKey;

    /**
     * Default constructor.
     */
    public StreamPopularHashTagsRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inStreamEntityScopeType
     *            the stream entity scope type
     * @param inStreamEntityUniqueKey
     *            the stream entity unique key
     */
    public StreamPopularHashTagsRequest(final ScopeType inStreamEntityScopeType, final String inStreamEntityUniqueKey)
    {
        streamEntityScopeType = inStreamEntityScopeType;
        streamEntityUniqueKey = inStreamEntityUniqueKey;
    }

    /**
     * Get the stream entity scope type.
     *
     * @return the streamEntityScopeType
     */
    public ScopeType getStreamEntityScopeType()
    {
        return streamEntityScopeType;
    }

    /**
     * Set the stream entity scope type.
     *
     * @param inStreamEntityScopeType
     *            the streamEntityScopeType to set
     */
    public void setStreamEntityScopeType(final ScopeType inStreamEntityScopeType)
    {
        streamEntityScopeType = inStreamEntityScopeType;
    }

    /**
     * Get the stream entity unique key.
     *
     * @return the streamEntityUniqueKey
     */
    public String getStreamEntityUniqueKey()
    {
        return streamEntityUniqueKey;
    }

    /**
     * Set the stream entity unique key.
     *
     * @param inStreamEntityUniqueKey
     *            the streamEntityUniqueKey to set
     */
    public void setStreamEntityUniqueKey(final String inStreamEntityUniqueKey)
    {
        streamEntityUniqueKey = inStreamEntityUniqueKey;
    }
}
