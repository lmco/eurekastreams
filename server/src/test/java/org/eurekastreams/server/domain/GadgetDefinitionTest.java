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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Gadget.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class GadgetDefinitionTest
{
    /**
     * Very basic test to ensure that the gadget constructor stores parameters correctly.
     */
    @Test
    public void sutinitionConstructor()
    {
        String message = "accessor parameters should be stored and accessible via getters/setters";

        /** fixture. */
        String gadgetUrl = "http://www.example.com";
        GadgetDefinition sut = new GadgetDefinition(gadgetUrl, UUID.randomUUID().toString(), new GalleryItemCategory(
                "somecategory"));
        assertEquals("gadget url does not match url passed into the constructor.", gadgetUrl, sut.getUrl());

        List<Gadget> gadgets = new ArrayList<Gadget>();
        GadgetDefinition def = new GadgetDefinition(gadgetUrl, UUID.randomUUID().toString(), new GalleryItemCategory(
                "somecategory"));
        Gadget gadget = new Gadget(def, 1, 0, new Person(), "");
        gadgets.clear();
        gadgets.add(gadget);

        sut.setGadgets(gadgets);
        sut.setNumberOfUsers(gadgets.size());

        sut.getUrl();
        sut.getUUID();
        sut.getCategory();
        sut.getNumberOfUsers();

        assertEquals(message, gadgets, sut.getGadgets());
    }

    /**
     * Tests the transient properties.
     */
    @Test
    public void transientPropertiesTest()
    {
        GadgetDefinition sut = new GadgetDefinition();

        final String author = "some author";
        final String title = "some title";
        final String desc = "some description";

        sut.setGadgetAuthor(author);
        sut.setGadgetTitle(title);
        sut.setGadgetDescription(desc);

        assertEquals(author, sut.getGadgetAuthor());
        assertEquals(title, sut.getGadgetTitle());
        assertEquals(desc, sut.getGadgetDescription());

    }

    /**
     * Test the static web root affects the url.
     */
    @Test
    public void testWebRoot()
    {
        String existingWebRoot = GadgetDefinition.getWebRootUrl();
        GadgetDefinition gd = new GadgetDefinition();

        GadgetDefinition.setWebRootUrl(null);
        gd.setUrl("foo");
        assertEquals("foo", gd.getUrl());

        GadgetDefinition.setWebRootUrl("http://foo.com");
        assertEquals("http://foo.com/foo", gd.getUrl());

        gd.setUrl("http://bar.com/foo");
        assertEquals("http://bar.com/foo", gd.getUrl());

        GadgetDefinition.setWebRootUrl(existingWebRoot);
    }
}
