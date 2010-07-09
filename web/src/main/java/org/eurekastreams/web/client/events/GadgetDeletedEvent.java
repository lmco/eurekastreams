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
package org.eurekastreams.web.client.events;

import org.eurekastreams.web.client.ui.pages.start.GadgetPanel;

/**
 * This class represents the event of a user clicking on the Close button of Gadget.
 *
 */
public class GadgetDeletedEvent
{
    /**
     * The gadget zone.
     */
    private GadgetPanel gadgetZone;

    /**
     * Flag that tracks whether the deletion was successful.
     */
    private Boolean isSuccessful;

    /**
     * Default Constructor.
     * @param inGadgetZone the gadget zone being closed.
     * @param inIsSuccessful flag indicating whether or not the deleted event was successful.
     */
    public GadgetDeletedEvent(final GadgetPanel inGadgetZone, final Boolean inIsSuccessful)
    {
        gadgetZone = inGadgetZone;
        isSuccessful = inIsSuccessful;
    }

    /**
     * Get the gadget zone being closed.
     * @return the gadget zone.
     */
    public GadgetPanel getGadgetZone()
    {
        return gadgetZone;
    }

    /**
     * Get the status of the deletion event.
     * @return the status of the deletion event.
     */
    public Boolean getIsSuccessful()
    {
        return isSuccessful;
    }
}
