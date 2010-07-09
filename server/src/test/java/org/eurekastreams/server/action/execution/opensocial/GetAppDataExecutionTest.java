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
package org.eurekastreams.server.action.execution.opensocial;

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetAppDataExecution} class.
 *
 */
public class GetAppDataExecutionTest
{
    /**
     * System under test.
     */
    private GetAppDataExecution sut;

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
     * Mocked instance of the principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked mapper for testing.
     */
    private AppDataMapper jpaAppDataMapper = context.mock(AppDataMapper.class);

    /**
     * Test instance of the AppData object to use in tests below.
     */
    private AppData testAppData = context.mock(AppData.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetAppDataExecution(jpaAppDataMapper);
    }

    /**
     * Test the execution method of the strategy.
     */
    @Test
    public void testExecute()
    {
        final Long testApplicationId = 3487L;
        final String testOpenSocialId = "213-423142314-231421-342134";

        context.checking(new Expectations()
        {
            {
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(returnValue(testAppData));
            }
        });

        // Set up the call parameters
        GetAppDataRequest currentRequest = new GetAppDataRequest(testApplicationId, testOpenSocialId);

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        AppData actual = sut.execute(currentContext);

        assertEquals(testAppData, actual);

        context.assertIsSatisfied();
    }

    /**
     * Test the execution method of the strategy.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteWithException()
    {
        final Long testApplicationId = 3487L;
        final String testOpenSocialId = "213-423142314-231421-342134";

        context.checking(new Expectations()
        {
            {
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(throwException(new Exception()));
            }
        });

        // Set up the call parameters
        GetAppDataRequest currentRequest = new GetAppDataRequest(testApplicationId, testOpenSocialId);

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);

        // Make the call
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
