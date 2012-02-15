/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

/**
 * Request for reordering gadgets.
 *
 */
public class ReorderGadgetRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 3747819663781089959L;

    /**
     * The parameter index of the target Tab id.
     */
    private long currentTabId;

    /**
     * The parameter index for the ID of the gadget being moved.
     */
    private long gadgetId;

    /**
     * The parameter index for the target zone number that we're moving the gadget into.
     */
    private int targetZoneNumber;

    /**
     * The parameter index for the target zone index that we're moving the gadget into.
     */
    private int targetZoneIndex;

    /**
     * Constructor for serialization only.
     */
    @SuppressWarnings("unused")
    private ReorderGadgetRequest()
    {
        // no-op
    }

    /**
     * Construcor.
     *
     * @param inCurrentTabId
     *            The parameter index of the target Tab id.
     * @param inGadgetId
     *            The parameter index for the ID of the gadget being moved.
     * @param inTargetZoneNumber
     *            The parameter index for the target zone number that we're moving the gadget into.
     * @param inTargetZoneIndexNumber
     *            The parameter index for the target zone index that we're moving the gadget into.
     */
    public ReorderGadgetRequest(final long inCurrentTabId, final long inGadgetId, final int inTargetZoneNumber,
            final int inTargetZoneIndexNumber)
    {
        currentTabId = inCurrentTabId;
        gadgetId = inGadgetId;
        targetZoneNumber = inTargetZoneNumber;
        targetZoneIndex = inTargetZoneIndexNumber;
    }

    /**
     * @return the currentTabId
     */
    public long getCurrentTabId()
    {
        return currentTabId;
    }

    /**
     * @param inCurrentTabId
     *            the currentTabId to set
     */
    public void setCurrentTabId(final long inCurrentTabId)
    {
        currentTabId = inCurrentTabId;
    }

    /**
     * @return the gadgetId
     */
    public long getGadgetId()
    {
        return gadgetId;
    }

    /**
     * @param inGadgetId
     *            the gadgetId to set
     */
    public void setGadgetId(final long inGadgetId)
    {
        gadgetId = inGadgetId;
    }

    /**
     * @return the targetZoneNumber
     */
    public int getTargetZoneNumber()
    {
        return targetZoneNumber;
    }

    /**
     * @param inTargetZoneNumber
     *            the targetZoneNumber to set
     */
    public void setTargetZoneNumber(final int inTargetZoneNumber)
    {
        targetZoneNumber = inTargetZoneNumber;
    }

    /**
     * @return the targetZoneIndex
     */
    public int getTargetZoneIndex()
    {
        return targetZoneIndex;
    }

    /**
     * @param inTargetZoneIndex
     *            the targetZoneIndex to set
     */
    public void setTargetZoneIndex(final int inTargetZoneIndex)
    {
        targetZoneIndex = inTargetZoneIndex;
    }
}
