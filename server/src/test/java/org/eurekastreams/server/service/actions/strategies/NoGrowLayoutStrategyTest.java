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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.Person;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of the NoGrowLayoutStrategy.
 */
public class NoGrowLayoutStrategyTest
{
    /**
     * Subject under test.
     */
    private NoGrowLayoutStrategy sut = null;

    /**
     * Sample gadgets to play with.
     */
    private List<Gadget> gadgets = null;

    /**
     * Sample gadgets to play with.
     */
    private List<Gadget> gadgetsClone = null;

    /**
     * The number of zones to grow to. Must be greater than the number of zones
     * used in setup().
     */
    private static final int AVAILABLE_ZONES = 4;

    /**
     * Create the sut and some gadgets to test it with.
     */
    @Before
    public final void setup()
    {
        sut = new NoGrowLayoutStrategy();

        gadgets = makeGadgets();

        gadgetsClone = makeGadgets();
    }

    /**
     * Build a collection of gadgets to play with.
     *
     * @return the new gadgets
     */
    private List<Gadget> makeGadgets()
    {
        List<Gadget> gadgetList = new ArrayList<Gadget>();
        String url = "http://www.example.com/gadgets";
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 1, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 1, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 2, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 2, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition(url, UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 2, 1, new Person(), ""));

        return gadgetList;
    }

    /**
     * Make sure the NoGrow strategy returns an unaltered list.
     */
    @Test
    public final void testGrow()
    {
        sut.grow(AVAILABLE_ZONES, gadgets);

        assertEquals("Gadgets changed size.", gadgetsClone.size(), gadgets.size());
        for (int i = 0; i < gadgetsClone.size(); i++)
        {
            assertEquals("Gadget zone changed.",
                    gadgetsClone.get(i).getZoneNumber(),
                    gadgets.get(i).getZoneNumber());
            assertEquals("Gadget index changed.",
                    gadgetsClone.get(i).getZoneIndex(),
                    gadgets.get(i).getZoneIndex());
        }
    }
}
