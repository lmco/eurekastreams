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

import junit.framework.Assert;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Tab.
 */
public class TabTemplateTest
{

    /** fixture. */
    private String message;
    /** fixture. */
    private String name = "name";
    /** fixture. */
    private Layout layout = Layout.ONECOLUMN;
    /** fixture. */
    private List<Gadget> gadgets = new ArrayList<Gadget>();
    /** fixture. */
    private TabTemplate sut = new TabTemplate(name, layout);

    /**
     * Setup for tests.
     */
    @Before
    public void setup()
    {
        message = null;
        sut = new TabTemplate(name, layout);
    }

    /**
     * test.
     */
    @Test
    public void testConstructor()
    {
        message = "constructor parameters should be stored and accessible via setters";

        assertEquals(message, name, sut.getTabName());
        Assert.assertEquals(message, layout, sut.getTabLayout());
    }

    /**
     * test.
     */
    @Test
    public void testAccessors()
    {
        message = "accessor parameters should be stored and accessible via getters/setters";

        // assert preconditions
        assertEquals(message, name, sut.getTabName());
        Assert.assertEquals(message, layout, sut.getTabLayout());

        String gadgetUrl = "http://www.example.com";
        GadgetDefinition def =
                new GadgetDefinition(gadgetUrl, UUID.randomUUID().toString(), new GalleryItemCategory("somecategory"));
        Gadget gadget = new Gadget(def, 1, 0, new Person(), "");
        gadgets.clear();
        gadgets.add(gadget);

        // call accessors
        String newName = "another name";
        Layout newLayout = Layout.ONECOLUMN;
        sut.setTabLayout(newLayout);
        sut.setTabName(newName);
        sut.setGadgets(gadgets);

        // assert postconditions
        assertEquals(message, newName, sut.getTabName());
        Assert.assertEquals(message, layout, sut.getTabLayout());
        assertEquals(message, gadgets, sut.getGadgets());
    }

    /**
     * test.
     */
    @Test
    public void testNameValidation()
    {
        message = "name should follow validation rules";

        String newName = "";
        sut.setTabName(newName);

        ClassValidator<TabTemplate> validator = new ClassValidator<TabTemplate>(TabTemplate.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);

        assertEquals(message, 1, invalidValues.length);
        assertEquals(message, TabTemplate.MAX_TAB_NAME_MESSAGE, invalidValues[0].getMessage());
    }

}
