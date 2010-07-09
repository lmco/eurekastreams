/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.gallery;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteGalleryItemExecution class.
 * 
 */
public class DeleteGalleryItemExecutionTest
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
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * The subject under test.
     */
    @SuppressWarnings("unchecked")
    private DeleteGalleryItemExecution sut;

    /**
     * The mock domainEntity mapper to be used by the action.
     */
    @SuppressWarnings("unchecked")
    private GalleryItemMapper galleryItemMapperMock = context.mock(GalleryItemMapper.class);

    /**
     * Mocked user information in the session.
     */
    private GalleryItem galleryItem = context.mock(GalleryItem.class);

    /**
     * An arbitrary domainEntityanization id to use for testing.
     */
    private static final Long GALLERY_ITEM_ID = 37L;

    /**
     * Set up the SUT.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        sut = new DeleteGalleryItemExecution(galleryItemMapperMock);
    }

    /**
     * Build an domainEntityanization based on the input form being fully filled out with valid data.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithSuccess() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GALLERY_ITEM_ID));

                oneOf(galleryItemMapperMock).findById(GALLERY_ITEM_ID);
                will(returnValue(galleryItem));

                oneOf(galleryItemMapperMock).delete(galleryItem);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

}
