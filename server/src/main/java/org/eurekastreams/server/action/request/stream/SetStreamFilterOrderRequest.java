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
public class SetStreamFilterOrderRequest implements Serializable
{
    /**
     * Serialization id.
     */

    private static final long serialVersionUID = 222865138114168086L;
    /**
     * Filter id.
     */

    private Long filterId;
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
     * @param inFilterId
     *            filter id.
     * @param inNewIndex
     *            new index.
     * @param inHiddenLineIndex
     *            hidden line index.
     */
    public SetStreamFilterOrderRequest(final Long inFilterId,
            final Integer inNewIndex, final Integer inHiddenLineIndex)
    {
        filterId = inFilterId;
        newIndex = inNewIndex;
        hiddenLineIndex = inHiddenLineIndex;
    }

    /**
     * Used for Serialization.
     */
    private SetStreamFilterOrderRequest()
    {
    }

    /**
     * Gets filter id.
     *
     * @return filter id.
     */
    public Long getFilterId()
    {
        return filterId;
    }

    /**
     * Sets filter id.
     *
     * @param inFilterId
     *            filter id.
     */
    public void setFilterId(final Long inFilterId)
    {
        filterId = inFilterId;
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
