/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.Date;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for GalleryTabTemplate.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class GalleryTabTemplateTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private GalleryTabTemplate sut;

    /**
     * GalleryItemCategory.
     */
    private GalleryItemCategory gic = context.mock(GalleryItemCategory.class);

    /**
     * TabTemplate.
     */
    private TabTemplate tabTemplate = context.mock(TabTemplate.class);

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {
        sut = new GalleryTabTemplate();
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        Date testDate = new Date();
        sut.setCategory(gic);
        sut.setCreated(testDate);
        sut.setDescription("desc");
        sut.setTabTemplate(tabTemplate);
        sut.setTitle("title");

        assertEquals(testDate, sut.getCreated());
        assertEquals(gic, sut.getCategory());
        assertEquals("desc", sut.getDescription());
        assertEquals(tabTemplate, sut.getTabTemplate());
        assertEquals("title", sut.getTitle());
    }
}
