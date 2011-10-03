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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * Request for a token for a stream.
 */
public class GetTokenForStreamRequest implements Serializable
{
    /** Entity type of the stream. */
    private EntityType streamEntityType;

    /** ID of stream's entity (person/group). */
    private long streamEntityId;

    /**
     * Constructor for serialization.
     */
    @SuppressWarnings("unused")
    private GetTokenForStreamRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inStreamEntityType
     *            Entity type of the stream.
     * @param inStreamEntityId
     *            ID of stream's entity (person/group).
     */
    public GetTokenForStreamRequest(final EntityType inStreamEntityType, final long inStreamEntityId)
    {
        streamEntityType = inStreamEntityType;
        streamEntityId = inStreamEntityId;
    }

    /**
     * @return the streamEntityType
     */
    public EntityType getStreamEntityType()
    {
        return streamEntityType;
    }

    /**
     * @return the streamEntityId
     */
    public long getStreamEntityId()
    {
        return streamEntityId;
    }

    /**
     * @param inStreamEntityType
     *            the streamEntityType to set
     */
    public void setStreamEntityType(final EntityType inStreamEntityType)
    {
        streamEntityType = inStreamEntityType;
    }

    /**
     * @param inStreamEntityId
     *            the streamEntityId to set
     */
    public void setStreamEntityId(final long inStreamEntityId)
    {
        streamEntityId = inStreamEntityId;
    }
}
