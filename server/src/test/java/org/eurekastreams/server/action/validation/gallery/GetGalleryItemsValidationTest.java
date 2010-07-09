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
package org.eurekastreams.server.action.validation.gallery;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetGalleryItemsValidation} class.
 *
 */
public class GetGalleryItemsValidationTest
{
    /**
     * Recent sort criteria.
     */
    private static final String RECENT_SORT_CRITERIA = "recent";

    /**
     * Popularity sort criteria.
     */
    private static final String POPULARITY_SORT_CRITERIA = "popularity";

    /**
     * System under test.
     */
    private GetGalleryItemsValidation sut;

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
     * Setup the test suite.
     */
    @Before
    public void setup()
    {
        sut = new GetGalleryItemsValidation();
    }

    /**
     * Test that the sort criteria was correctly entered.
     */
    @Test
    public void testSuccessfulValidationPopularSortCriteria()
    {
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest(POPULARITY_SORT_CRITERIA, "", 0, 1);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Test that the sort criteria was correctly entered.
     */
    @Test
    public void testSuccessfulValidationRecentSortCriteria()
    {
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest(RECENT_SORT_CRITERIA, "", 0, 1);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Test that the sort criteria was incorrectly entered.
     */
    @Test(expected = ValidationException.class)
    public void testFailureValidationBadSortCriteria()
    {
        GetGalleryItemsRequest currentRequest = new GetGalleryItemsRequest("incompatible sort criteria", "", 0, 1);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        sut.validate(currentContext);
    }
}
