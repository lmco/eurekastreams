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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Gadget.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class GadgetTest
{
    /**
     * Basic test to ensure the constructor is correctly creating the object.
     */
    @Test
    public void testGadget()
    {
        String gadgetDefinitionUrl = "http://www.example.com";

        GadgetDefinition testGadgetDefinition =
                new GadgetDefinition(gadgetDefinitionUrl, UUID.randomUUID().toString(), new GalleryItemCategory(
                        "somecategory"));
        Gadget testGadget = new Gadget(testGadgetDefinition, 1, 0, new Person(), "");
        assertEquals("Gadget Zone Number is not the same as the one passed in.", 1, testGadget.getZoneNumber());
        assertEquals("Gadget Zone Index is not the same as the one passed in.", 0, testGadget.getZoneIndex());
        assertEquals("GadgetDefinition url is not the same s the one passed in.", gadgetDefinitionUrl, testGadget
                .getGadgetDefinition().getUrl());
    }

    /**
     * Test the gadget constructor with gadget param.
     */
    @Test
    public void testGadgetConstructor()
    {
        String gadgetDefinitionUrl = "http://www.example.com";
        final int zoneIndex = 33;
        final int zoneNumber = 44;
        String uuid = UUID.randomUUID().toString();

        GadgetDefinition testGadgetDefinition =
                new GadgetDefinition(gadgetDefinitionUrl, uuid, new GalleryItemCategory("somecategory"));
        Gadget existingGadget = new Gadget(testGadgetDefinition, zoneNumber, zoneIndex, new Person(), "");
        existingGadget.setMinimized(true);

        Gadget newGadget = new Gadget(existingGadget);

        assertEquals(uuid, newGadget.getGadgetDefinition().getUUID());
        assertEquals(zoneIndex, newGadget.getZoneIndex());
        assertEquals(zoneNumber, newGadget.getZoneNumber());
        assertEquals(true, newGadget.isMinimized());

        // now try with not minimized
        existingGadget.setMinimized(false);
        newGadget = new Gadget(existingGadget);
        assertEquals(false, newGadget.isMinimized());

    }

    /**
     * Basic test to ensure the setZoneIndex works properly.
     */
    @Test
    public void testSetZoneIndex()
    {
        final int zoneIndex = 5281;
        GadgetDefinition testGadDef =
                new GadgetDefinition("http://foo.com", UUID.randomUUID().toString(), new GalleryItemCategory(
                        "somecategory"));
        Gadget gadget = new Gadget(testGadDef, 1, 0, new Person(), "");

        // now change the zone index and zone numbers
        gadget.setZoneIndex(zoneIndex);
        assertEquals("getZoneIndex() doesn't return the same value as the previous setZoneIndex(5281)", zoneIndex,
                gadget.getZoneIndex());
    }

    /**
     * Basic test to ensure the setGadgetUserPref works properly.
     */
    @Test
    public void testSetGadgetUserPref()
    {
        final String userPref = "{\"userPref1\":\"value1\",\"userPref2\":\"value2\"}";
        GadgetDefinition testGadDef =
                new GadgetDefinition("http://foo.com", UUID.randomUUID().toString(), new GalleryItemCategory(
                        "somecategory"));
        Gadget gadget = new Gadget(testGadDef, 1, 0, new Person(), "");

        // now change the zone index and zone numbers
        gadget.setGadgetUserPref(userPref);
        assertEquals("getGadgetUserPref() doesn't return the same value as the previous setGadgetUserPref(5281)",
                userPref, gadget.getGadgetUserPref());
    }

    /**
     * Basic test to ensure the setZoneNumber works properly.
     */
    @Test
    public void testSetZoneNumber()
    {
        final int zoneNumber = 7901;
        GadgetDefinition testGadDef =
                new GadgetDefinition("http://foo.com", UUID.randomUUID().toString(), new GalleryItemCategory(
                        "somecategory"));
        Gadget gadget = new Gadget(testGadDef, 1, 0, new Person(), "");

        // now change the zone index and zone numbers
        gadget.setZoneNumber(zoneNumber);
        assertEquals("getZoneNumber() doesn't return the same value as the previous setZoneNumber(7901)", zoneNumber,
                gadget.getZoneNumber());
    }

    /**
     * Basic test to ensure the setMinimized works properly.
     */
    @Test
    public void testMinimizedProperty()
    {
        GadgetDefinition testGadDef =
                new GadgetDefinition("http://foo.com", UUID.randomUUID().toString(), new GalleryItemCategory(
                        "somecategory"));
        Gadget gadget = new Gadget(testGadDef, 1, 0, new Person(), "");

        gadget.setMinimized(true);
        assertEquals("isMinimized() doesn't return the same value as the previous setMinimized()", true, gadget
                .isMinimized());
    }
}
