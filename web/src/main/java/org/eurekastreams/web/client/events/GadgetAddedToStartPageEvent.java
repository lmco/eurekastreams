/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.Gadget;

/**
 * Gets fired when a gagdet is added to the start page.
 * 
 */
public final class GadgetAddedToStartPageEvent
{
    /**
     * The gadget.
     */
    private Gadget gadget = null;
    
    /**
     * Gets the event.
     * @return the event.
     */
    public static GadgetAddedToStartPageEvent getEvent()
    {
        return new GadgetAddedToStartPageEvent(null);
    }

    /**
     * Constructor.
     * @param inGadget the added gadget.
     */
    public GadgetAddedToStartPageEvent(final Gadget inGadget)
    {
        gadget = inGadget;
    }
    
    /**
     * Get the gadget.
     * @return the gadget.
     */
    public Gadget getGadget()
    {
        return gadget;
    }
}
