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

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetGalleryItemCategoriesExecution}.
 *
 */
public class GetGalleryItemCategoriesExecutionTest
{
    /**
     * System under test.
     */
    private GetGalleryItemCategoriesExecution sut;

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
     * Mocked {@link Principal} object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked tab mapper object for test.
     */
    private GalleryItemCategoryMapper mapper = context.mock(GalleryItemCategoryMapper.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setup()
    {
        sut = new GetGalleryItemCategoriesExecution(mapper, GalleryItemType.GADGET);
    }

    /**
     * Test the perform action method.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @SuppressWarnings("unchecked")
        @Test
    public final void testExecute() throws Exception
    {

        String message = "mapper should return correct list of gadget definitions";

        final ArrayList<GalleryItemCategory> list = new ArrayList<GalleryItemCategory>();
        for (int i = 0; i < 8; i++)
        {
            GalleryItemCategory gadgetCategory = new GalleryItemCategory("somecategory");
            list.add(gadgetCategory);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findGalleryItemCategories(GalleryItemType.GADGET);
                will(returnValue(list));
            }
        });

        // Set up the call parameters
        ServiceActionContext currentContext = new ServiceActionContext(null, principalMock);

        // Make the call
        LinkedList results = (LinkedList) sut.execute(currentContext);

        for (Object obj : results)
        {
            assertEquals(message, "somecategory", obj.toString());
        }

        assertEquals(list.size(), results.size());

        context.assertIsSatisfied();
    }

}
