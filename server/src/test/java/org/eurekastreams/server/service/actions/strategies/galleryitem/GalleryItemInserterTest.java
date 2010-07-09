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
package org.eurekastreams.server.service.actions.strategies.galleryitem;


import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Tests the create person strategy.
 */
public class GalleryItemInserterTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
  
    /**
     * The mock theme mapper to be used by the action.
     */
    private GalleryItemMapper galleryItemMapper = context.mock(GalleryItemMapper.class);
    
    /**
     * System under test.
     */
    private GalleryItemInserter sut;

    /**
     * GadgetDefinition Mock.
     */
    private GalleryItem galleryItemMock = context.mock(GalleryItem.class);

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new GalleryItemInserter(galleryItemMapper);
    }

    /**
     * Test the get method.
     */
    @Test
    public final void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemMapper).insert(galleryItemMock);
                
            }
        });

        sut.save(galleryItemMock);
        context.assertIsSatisfied();
    }
}
