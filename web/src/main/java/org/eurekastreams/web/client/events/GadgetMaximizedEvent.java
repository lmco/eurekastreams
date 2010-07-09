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
 * Event gets fired when gadget is maximized.
 *
 */
public class GadgetMaximizedEvent
{
    /**
     * The gadget zone.
     */
    private GadgetPanel gadgetZone;

    /**
     * Default Constructor.
     * @param inGadgetZone the gadget zone being maximized.
     */
    public GadgetMaximizedEvent(final GadgetPanel inGadgetZone)
    {
        gadgetZone = inGadgetZone;
    }

    /**
     * Get the gadget zone being maximized.
     * @return the gadget zone.
     */
    public GadgetPanel getGadgetZone()
    {
        return gadgetZone;
    }

    /**
     * Gets Event.
     *
     * @return Event.
     */
    public static GadgetMaximizedEvent getEvent()
    {
        return new GadgetMaximizedEvent(null);
    }
}
