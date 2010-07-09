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
package org.eurekastreams.server.domain.stream.plugins;

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Plugins.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class PluginDefinitionTest
{
    /**
     * Subject under test.
     */
    PluginDefinition sut; 
    
    /**
     * Very basic test to ensure that the gadget constructor stores parameters 
     * correctly.
     */
    @Test
    public void sutinitionConstructor()
    {
        String gadgetUrl = "http://www.example.com";
        GalleryItemCategory gic = new GalleryItemCategory("face");
        Person person = new Person();
        person.setBiography("hello test");
        
        sut = new PluginDefinition();

        sut.setUrl(gadgetUrl);
        sut.setCategory(gic);
        sut.setShowInGallery(false);
        sut.setNumberOfUsers(5);
        sut.setOwner(person);
        
        assertEquals(gadgetUrl, sut.getUrl());
        assertEquals(gic, sut.getCategory());
        assertEquals(Boolean.FALSE, sut.getShowInGallery());
        assertEquals(5, sut.getNumberOfUsers());
        assertEquals("hello test", sut.getOwner().getBiography());
        
    }
    
    /**
     * Tests the transient properties.
     */
    @Test
    public void transientPropertiesTest()
    {
        sut = new PluginDefinition();
        
        final Long updateF = 3L;
        
        sut.setUpdateFrequency(updateF);
        
        assertEquals(updateF, sut.getUpdateFrequency());
    }

}
