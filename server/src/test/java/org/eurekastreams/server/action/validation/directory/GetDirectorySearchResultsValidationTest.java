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
package org.eurekastreams.server.action.validation.directory;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test suite for the {@link GetDirectorySearchResultsValidation} class.
 *
 */
public class GetDirectorySearchResultsValidationTest
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
    private GetDirectorySearchResultsValidation sut = new GetDirectorySearchResultsValidation();

    /**
     * The mocked UserDetails.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * The search term to use.
     */
    private static final String SEARCH_TEXT = "heynow";

    /**
     * The starting index to use.
     */
    private static final int FROM = 0;

    /**
     * The ending index to use.
     */
    private static final int TO = 9;

    /**
     * The org short name.
     */
    private static final String SHORT_NAME = "orgshortname";

    /**
     * Test that org shortname is regexed.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithSpaceInOrgShortName()
    {
        GetDirectorySearchResultsRequest params = new GetDirectorySearchResultsRequest(SEARCH_TEXT, " " + SHORT_NAME,
                "", FROM, TO);

        ServiceActionContext currentContext = new ServiceActionContext(params, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Test that org shortname is regexed.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithParensInOrgShortName()
    {
        GetDirectorySearchResultsRequest params = new GetDirectorySearchResultsRequest(SEARCH_TEXT, ")foo", "", FROM,
                TO);

        ServiceActionContext currentContext = new ServiceActionContext(params, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Test that org shortname is regexed.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithQuotesInOrgShortName()
    {
        GetDirectorySearchResultsRequest params = new GetDirectorySearchResultsRequest(SEARCH_TEXT, "\"foo", "", FROM,
                TO);

        ServiceActionContext currentContext = new ServiceActionContext(params, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Check that good parameters pass validation.
     */
    @Test
    public void validateParamsWithGoodParams()
    {
        GetDirectorySearchResultsRequest params = new GetDirectorySearchResultsRequest(SEARCH_TEXT, SHORT_NAME, "",
                FROM, TO);

        ServiceActionContext currentContext = new ServiceActionContext(params, principalMock);

        sut.validate(currentContext);
    }

    /**
     * Check that good parameters pass validation.
     */
    @Test
    public void validateParamsWithEmptyShortName()
    {
        GetDirectorySearchResultsRequest params = new GetDirectorySearchResultsRequest(SEARCH_TEXT, "", "",
                FROM, TO);

        ServiceActionContext currentContext = new ServiceActionContext(params, principalMock);

        sut.validate(currentContext);
    }

}
