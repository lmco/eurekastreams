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
package org.eurekastreams.server.service.actions.strategies;

import java.util.List;

import org.eurekastreams.server.domain.Gadget;

/**
 * Shrink the layout by moving any gadgets from excess zones to the last
 * remaining zone.
 */
public class LastShrinkLayoutStrategy implements ShrinkLayoutStrategy
{

    /**
     * Move the gadgets that are in zones that no longer exist. This
     * implementation assumes that the gadgets are in order by ZoneNumber and
     * then by ZoneIndex.
     * 
     * @param availableZones
     *            the number of zones now available
     * @param gadgets
     *            the gadgets to rearrange
     */
    public final void shrink(final int availableZones,
            final List<Gadget> gadgets)
    {
        int lastZone = availableZones - 1;
        int lastIndex = 0;

        for (Gadget gadget : gadgets)
        {
            if (gadget.getZoneNumber() > lastZone)
            {
                gadget.setZoneNumber(lastZone);
                gadget.setZoneIndex(++lastIndex);
            } 
            else
            {
                lastIndex = gadget.getZoneIndex();
            }
        }
    }

}
