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

/**
 * Request object for the SetGadgetMinimizedState action.
 *
 */
public class SetGadgetStateRequest implements Serializable
{
    /**
     * State of the gadget.
     *
     */
    public enum State
    {
        /**
         * Normal.
         */
        NORMAL,
        /**
         * Minimized.
         */
        MINIMIZED,
        /**
         * Maximized.
         */
        MAXIMIZED
    }
    /**
     * Serialization id for this request object.
     */
    private static final long serialVersionUID = 4977829551578369070L;

    /**
     * Local instance of the gadget id for this request.
     */
    private Long gadgetId;

    /**
     * Local instance of the minimized state for this request.
     */
    private boolean minimized;

    /**
     * Local instance of the maximized state for this request.
     */
    private boolean maximized;

    /**
     * Default constructor for EJB compliance.
     */
    private SetGadgetStateRequest()
    {
        //Default constructor for EJB compliance.
    }

    /**
     * Constructor for request object.
     * @param inGadgetId - value of gadget id for this request.
     * @param inState - value of state for this request.
     */
    public SetGadgetStateRequest(final Long inGadgetId, final State inState)
    {
        gadgetId = inGadgetId;
        minimized = inState == State.MINIMIZED;
        maximized = inState == State.MAXIMIZED;
    }

    /**
     * @return the gadgetId
     */
    public Long getGadgetId()
    {
        return gadgetId;
    }

    /**
     * @param inGadgetId the gadgetId to set
     */
    public void setGadgetId(final Long inGadgetId)
    {
        this.gadgetId = inGadgetId;
    }

    /**
     * @return the minimized
     */
    public boolean isMinimized()
    {
        return minimized;
    }

    /**
     * @param inMinimized the minimized to set
     */
    private void setMinimized(final boolean inMinimized)
    {
        this.minimized = inMinimized;
    }

    /**
     * @return the maximized
     */
    public boolean isMaximized()
    {
        return maximized;
    }

    /**
     * @param inMaximized the maximized to set
     */
    private void setMaximized(final boolean inMaximized)
    {
        this.maximized = inMaximized;
    }
}
