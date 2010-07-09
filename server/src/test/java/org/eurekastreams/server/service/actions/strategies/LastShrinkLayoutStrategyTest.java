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
 * Test the LastShrinkLayoutStrategy.
 */
public class LastShrinkLayoutStrategyTest
{
    /**
     * Subject under test.
     */
    private LastShrinkLayoutStrategy sut = null;

    /**
     * Sample gadgets to play with.
     */
    private List<Gadget> gadgets = null;

    /**
     * The number of zones to grow to. Must be greater than the number of zones
     * used in setup().
     */
    private static final int AVAILABLE_ZONES = 2;

    /**
     * Create the SUT and some gadgets to test it with.
     */
    @Before
    public final void setup()
    {
        sut = new LastShrinkLayoutStrategy();

        gadgets = makeGadgets();
    }

    /**
     * Build a collection of gadgets to play with.
     *
     * @return the new gadgets
     */
    private List<Gadget> makeGadgets()
    {
        List<Gadget> gadgetList = new ArrayList<Gadget>();
        gadgetList.add(new Gadget(new GadgetDefinition("gadget0", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget1", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 1, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget2", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget3", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 1, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget4", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 2, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget5", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 2, 0, new Person(), ""));
        gadgetList.add(new Gadget(new GadgetDefinition("gadget6", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 2, 1, new Person(), ""));

        return gadgetList;
    }

    /**
     * Make sure all gadgets from zone 2 move to zone 1.
     */
    @Test
    public final void testShrink()
    {
        int gadgetCount = gadgets.size();
        sut.shrink(AVAILABLE_ZONES, gadgets);

        assertEquals("Gadgets changed size.", gadgetCount, gadgets.size());

        verifyGadget(gadgets.get(0), new GadgetDefinition("gadget0", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 0);
        verifyGadget(gadgets.get(1), new GadgetDefinition("gadget1", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 0, 1);
        verifyGadget(gadgets.get(2), new GadgetDefinition("gadget2", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 0);
        verifyGadget(gadgets.get(3), new GadgetDefinition("gadget3", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 1);
        verifyGadget(gadgets.get(4), new GadgetDefinition("gadget4", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 2);
        verifyGadget(gadgets.get(5), new GadgetDefinition("gadget5", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 3);
        verifyGadget(gadgets.get(6), new GadgetDefinition("gadget6", UUID.randomUUID().toString(),
                 new GalleryItemCategory("somecategory")), 1, 4);

    }

    /**
     * Check that this gadget ended up with the right zoneNumber and zoneIndex.
     *
     * @param gadget
     *            the gadget being tested
     * @param definition
     *            correct definition
     * @param zoneNumber
     *            correct zoneNumber
     * @param zoneIndex
     *            correct zoneIndex
     */
    private void verifyGadget(final Gadget gadget,
            final GadgetDefinition definition, final int zoneNumber,
            final int zoneIndex)
    {
        assertEquals("Wrong Gadget defintion.",
                definition.getUrl(), gadget.getGadgetDefinition().getUrl());
        assertEquals("Gadget zone number is wrong for " + gadget.getGadgetDefinition().getUrl(),
                zoneNumber, gadget.getZoneNumber());
        assertEquals("Gadget zone index is wrong for " + gadget.getGadgetDefinition().getUrl(),
                zoneIndex, gadget.getZoneIndex());
    }
}
