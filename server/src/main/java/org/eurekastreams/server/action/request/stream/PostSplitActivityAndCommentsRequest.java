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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * Request for PostSplitActivityAndCommentsExecution; to post an activity and comments using just the text.
 */
public class PostSplitActivityAndCommentsRequest implements Serializable
{
    /** Type of entity whose stream to post to. */
    private EntityType entityType;

    /** ID of entity whose stream to post to. */
    private long entityId;

    /** The text for the activity body and comment bodies. */
    private String text;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            Type of entity whose stream to post to.
     * @param inEntityId
     *            ID of entity whose stream to post to.
     * @param inText
     *            The text for the activity body and comment bodies.
     */
    public PostSplitActivityAndCommentsRequest(final EntityType inEntityType, final long inEntityId,
            final String inText)
    {
        entityType = inEntityType;
        entityId = inEntityId;
        text = inText;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @param inEntityType
     *            the entityType to set
     */
    public void setEntityType(final EntityType inEntityType)
    {
        entityType = inEntityType;
    }

    /**
     * @return the entityId
     */
    public long getEntityId()
    {
        return entityId;
    }

    /**
     * @param inEntityId
     *            the entityId to set
     */
    public void setEntityId(final long inEntityId)
    {
        entityId = inEntityId;
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param inText
     *            the text to set
     */
    public void setText(final String inText)
    {
        text = inText;
    }
}
