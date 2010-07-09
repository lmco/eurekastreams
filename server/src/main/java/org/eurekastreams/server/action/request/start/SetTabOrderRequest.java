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
package org.eurekastreams.server.action.request.start;

import java.io.Serializable;

import org.eurekastreams.server.domain.TabGroupType;

/**
 * Request object for the SetTabOrder action strategies.
 *
 */
public class SetTabOrderRequest implements Serializable
{
    /**
     * Serialization id for this request.
     */
    private static final long serialVersionUID = 755436562039933911L;

    /**
     * Local instance of the {@link TabGroupType} for this request.
     */
    private TabGroupType tabType;

    /**
     * Local instance of the tabId for this request.
     */
    private Long tabId;

    /**
     * Local instance of the new index to move the tab to.
     */
    private Integer newIndex;

    /**
     * Empty constructor for EJB compliance.
     */
    private SetTabOrderRequest()
    {
        // empty constructor for EJB compliance.
    }

    /**
     * Basic constructor for request.
     *
     * @param inTabGroupType
     *            - instance of the {@link TabGroupType} for this request.
     * @param inTabId
     *            - tab id to reorder for this request.
     * @param inNewIndex
     *            - index where to place the tab for this request.
     */
    public SetTabOrderRequest(final TabGroupType inTabGroupType, final Long inTabId, final Integer inNewIndex)
    {
        tabType = inTabGroupType;
        tabId = inTabId;
        newIndex = inNewIndex;
    }

    /**
     * @return the tabType
     */
    public TabGroupType getTabType()
    {
        return tabType;
    }

    /**
     * @param inTabType
     *            the tabType to set
     */
    public void setTabType(final TabGroupType inTabType)
    {
        this.tabType = tabType;
    }

    /**
     * @return the tabId
     */
    public Long getTabId()
    {
        return tabId;
    }

    /**
     * @param inTabId
     *            the tabId to set
     */
    public void setTabId(final Long inTabId)
    {
        this.tabId = tabId;
    }

    /**
     * @return the newIndex
     */
    public Integer getNewIndex()
    {
        return newIndex;
    }

    /**
     * @param inNewIndex
     *            the newIndex to set
     */
    public void setNewIndex(final Integer inNewIndex)
    {
        this.newIndex = newIndex;
    }
}
