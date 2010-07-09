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
package org.eurekastreams.server.service.actions.strategies.galleryitem;



import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;
import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Tests the create person strategy.
 */
public class GalleryItemUpdaterTest
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
    private GalleryItemUpdater sut;

    /**
     * GadgetDefinition Mock.
     */
    private GalleryItem galleryItemToUpdateMock = context.mock(GalleryItem.class, "itemToUpdate");
    
    /**
     * GadgetDefinition Mock.
     */
    private GalleryItem galleryItemExistingMock = context.mock(GalleryItem.class, "existingItem");

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new GalleryItemUpdater(galleryItemMapper);
    }

    /**
     * Test the execute method.
     */
    @Test
    public final void testExecute()
    {
        final String url = "http://www.tehGoogle.com";
        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemToUpdateMock).getUrl();
                will(returnValue(url));
                
                oneOf(galleryItemMapper).findByUrl(url);
                will(returnValue(null));
                
                oneOf(galleryItemMapper).flush();
                
            }
        });

        sut.save(galleryItemToUpdateMock);
        context.assertIsSatisfied();
    }
    
    /**
     * Test the execute method. Different galleryItem has
     * same url and different uuid so expect ValidationException.
     */
    @Test(expected = ValidationException.class)
    public final void testExecuteOtherUsingUrl()
    {
        final String url = "http://www.tehGoogle.com";
        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemToUpdateMock).getUrl();
                will(returnValue(url));
                
                oneOf(galleryItemMapper).findByUrl(url);
                will(returnValue(galleryItemExistingMock));
                
                oneOf(galleryItemToUpdateMock).getUUID();
                will(returnValue("uuid"));
                
                oneOf(galleryItemExistingMock).getUUID();
                will(returnValue("notSameUuid"));                               
            }
        });

        sut.save(galleryItemToUpdateMock);
        context.assertIsSatisfied();
    }
    
    /**
     * Test the execute method. GalleryItem has same url, 
     * but it's the one we are updating, so it should save
     * normally.
     */
    public final void testExecuteCurrentUsingUrl()
    {
        final String url = "http://www.tehGoogle.com";
        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemToUpdateMock).getUrl();
                will(returnValue(url));
                
                oneOf(galleryItemMapper).findByUrl(url);
                will(returnValue(galleryItemExistingMock));
                
                oneOf(galleryItemToUpdateMock).getUUID();
                will(returnValue("uuid"));
                
                oneOf(galleryItemExistingMock).getUUID();
                will(returnValue("uuid")); 
                
                oneOf(galleryItemMapper).flush();
            }
        });

        sut.save(galleryItemToUpdateMock);
        context.assertIsSatisfied();
    }
}
