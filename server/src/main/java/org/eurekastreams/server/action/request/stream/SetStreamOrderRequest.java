/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

/**
 * Request for reordering streams (Group, Search, and View) on the Activity Page.
 *
 */
public class SetStreamOrderRequest implements Serializable
{
    /**
     * Serialization id.
     */

    private static final long serialVersionUID = 222865138114168086L;
    /**
     * Filter id.
     */

    private Long streamId;
    /**
     * New index.
     */

    private Integer newIndex;
    /**
     * Index of the line that hides views.
     */

    private Integer hiddenLineIndex;

    /**
     * Default constructor.
     *
     * @param inStreamId
     *            filter id.
     * @param inNewIndex
     *            new index.
     * @param inHiddenLineIndex
     *            hidden line index.
     */
    public SetStreamOrderRequest(final Long inStreamId,
            final Integer inNewIndex, final Integer inHiddenLineIndex)
    {
        streamId = inStreamId;
        newIndex = inNewIndex;
        hiddenLineIndex = inHiddenLineIndex;
    }

    /**
     * Used for Serialization.
     */
    private SetStreamOrderRequest()
    {
    }

    /**
     * Gets stream id.
     *
     * @return stream id.
     */
    public Long getStreamId()
    {
        return streamId;
    }

    /**
     * Sets stream id.
     *
     * @param inStreamId
     *            stream id.
     */
    public void setStreamId(final Long inStreamId)
    {
        streamId = inStreamId;
    }

    /**
     * Gets new index.
     *
     * @return new index.
     */
    public Integer getNewIndex()
    {
        return newIndex;
    }

    /**
     * Sets new index.
     *
     * @param inNewIndex
     *            new index.
     */
    public void setNewIndex(final Integer inNewIndex)
    {
        newIndex = inNewIndex;
    }

    /**
     * Gets hidden line index.
     *
     * @return hidden line index.
     */
    public Integer getHiddenLineIndex()
    {
        return hiddenLineIndex;
    }

    /**
     * Sets hidden line index.
     *
     * @param inHiddenLineIndex
     *            hidden line index.
     */
    public void setHiddenLineIndex(final Integer inHiddenLineIndex)
    {
        hiddenLineIndex = inHiddenLineIndex;
    }
}
