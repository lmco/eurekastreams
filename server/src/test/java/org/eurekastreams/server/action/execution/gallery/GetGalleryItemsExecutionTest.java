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

import java.util.UUID;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetGalleryItemsExecution} class.
 *
 */
public class GetGalleryItemsExecutionTest
{
    /**
     * System under test.
     */
    @SuppressWarnings("unchecked")
    private GetGalleryItemsExecution sut;

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
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked tab mapper object for test.
     */
    private GadgetDefinitionMapper mapper = context.mock(GadgetDefinitionMapper.class);

    /**
     * Prepare the test suite.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        sut = new GetGalleryItemsExecution(mapper);
    }

    /**
     * Test the perform action method for sorting by popularity for a specific category.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSortByPopularityForSpecificCategory() throws Exception
    {

        String message = "mapper should return correct list of gadget definitions";

        // for now, manually update until the back end is ready
        // this simulates a successful callback
        final PagedSet<GadgetDefinition> pagedSet = new PagedSet<GadgetDefinition>();
        for (int i = 0; i < 8; i++)
        {
            GadgetDefinition gadgetDef = new GadgetDefinition("http://www.example.com", UUID.randomUUID().toString(),
                    new GalleryItemCategory("somecategory"));
            pagedSet.getPagedSet().add(gadgetDef);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findForCategorySortedByPopularity("News", 0, 7);
                will(returnValue(pagedSet));
            }
        });

        // Set up the call parameters
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest("popularity", "News", 0, 7);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        PagedSet<GadgetDefinition> results = sut.execute(currentContext);

        assertEquals(message, pagedSet, results);

        context.assertIsSatisfied();
    }

    /**
     * Test the perform action method for sorting by popularity for all categories.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSortByPopularityForAllCategories() throws Exception
    {

        String message = "mapper should return correct list of gadget definitions";

        // for now, manually update until the back end is ready
        // this simulates a successful callback
        final PagedSet<GadgetDefinition> pagedSet = new PagedSet<GadgetDefinition>();
        for (int i = 0; i < 8; i++)
        {
            GadgetDefinition gadgetDef = new GadgetDefinition("http://www.example.com", UUID.randomUUID().toString(),
                    new GalleryItemCategory("somecategory"));
            pagedSet.getPagedSet().add(gadgetDef);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findSortedByPopularity(0, 7);
                will(returnValue(pagedSet));
            }
        });

        // Set up the call parameters
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest("popularity", "", 0, 7);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        PagedSet<GadgetDefinition> results = sut.execute(currentContext);

        assertEquals(message, pagedSet, results);

        context.assertIsSatisfied();
    }

    /**
     * Test the perform action method for sorting by recent for a specific category.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSortByRecentForSpecificCategory() throws Exception
    {

        String message = "mapper should return correct list of gadget definitions";

        // for now, manually update until the back end is ready
        // this simulates a successful callback
        final PagedSet<GadgetDefinition> pagedSet = new PagedSet<GadgetDefinition>();
        for (int i = 0; i < 8; i++)
        {
            GadgetDefinition gadgetDef = new GadgetDefinition("http://www.example.com", UUID.randomUUID().toString(),
                    new GalleryItemCategory("somecategory"));
            pagedSet.getPagedSet().add(gadgetDef);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findForCategorySortedByRecent("News", 0, 7);
                will(returnValue(pagedSet));
            }
        });

        // Set up the call parameters
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest("recent", "News", 0, 7);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        PagedSet<GadgetDefinition> results = sut.execute(currentContext);

        assertEquals(message, pagedSet, results);

        context.assertIsSatisfied();
    }

    /**
     * Test the perform action method for sorting by recent for all categories.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSortByRecent() throws Exception
    {

        String message = "mapper should return correct list of gadget definitions";

        // for now, manually update until the back end is ready
        // this simulates a successful callback
        final PagedSet<GadgetDefinition> pagedSet = new PagedSet<GadgetDefinition>();
        for (int i = 0; i < 8; i++)
        {
            GadgetDefinition gadgetDef = new GadgetDefinition("http://www.example.com", UUID.randomUUID().toString(),
                    new GalleryItemCategory("somecategory"));
            pagedSet.getPagedSet().add(gadgetDef);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findSortedByRecent(0, 7);
                will(returnValue(pagedSet));
            }
        });

        // Set up the call parameters
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest("recent", "", 0, 7);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        PagedSet<GadgetDefinition> results = sut.execute(currentContext);

        assertEquals(message, pagedSet, results);

        context.assertIsSatisfied();
    }
}
