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

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.opensocial.UpdateAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for UpdateAppDataExecution.
 * 
 */
public class UpdateAppDataExecutionTest
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
     * Mocked instance of AppData.
     */
    private AppData testAppData = context.mock(AppData.class);

    /**
     * System Under Test.
     */
    private UpdateAppDataExecution sut = null;

    /**
     * Mocked mapper for testing.
     */
    private AppDataMapper jpaAppDataMapper = context.mock(AppDataMapper.class);

    /**
     * Test Application Id.
     */
    final Long testApplicationId = 3487L;

    /**
     * Test Person Id.
     */
    final String testPersonId = "2347321410-sadfdsafdsf-324-324342";

    /**
     * {@link UpdateAppDataRequest}.
     */
    final UpdateAppDataRequest updateRequest = context.mock(UpdateAppDataRequest.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private final PrincipalActionContext applicationContext = context.mock(PrincipalActionContext.class);

    /**
     * Helper method to build up HashMap values for testing.
     * 
     * @return HashMap String, String populated with test values.
     */
    private HashMap<String, String> getAppDataValueHashMap()
    {
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("name1", "value1");
        testData.put("name2", "value2");
        testData.put("name3", "value3");
        return testData;
    }

    /**
     * Get system under test with a mocked mapper.
     */
    @Before
    public void setUp()
    {
        sut = new UpdateAppDataExecution(jpaAppDataMapper);
    }

    /**
     * This is a basic test of the standard success scenario for the UpdateAppDataAction performAction method.
     * 
     * @throws Exception
     *             errors to be caught by caller.
     */
    @Test
    public void testExecute() throws Exception
    {
        final Map<String, String> testAppDataValues = getAppDataValueHashMap();

        context.checking(new Expectations()
        {
            {
                allowing(applicationContext).getParams();
                will(returnValue(updateRequest));

                allowing(updateRequest).getOpenSocialId();
                will(returnValue(testPersonId));

                allowing(updateRequest).getApplicationId();
                will(returnValue(testApplicationId));

                allowing(updateRequest).getAppDataValues();
                will(returnValue(testAppDataValues));

                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId, testPersonId);
                will(returnValue(testAppData));

                oneOf(testAppData).getValues();
                will(returnValue(testAppDataValues));

                oneOf(testAppData).setValues(testAppDataValues);

                oneOf(jpaAppDataMapper).flush();

                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId, testPersonId);
                will(returnValue(testAppData));
            }
        });

        // Make the call
        AppData actual = sut.execute(applicationContext);

        assertEquals(testAppData, actual);

        context.assertIsSatisfied();
    }

}
