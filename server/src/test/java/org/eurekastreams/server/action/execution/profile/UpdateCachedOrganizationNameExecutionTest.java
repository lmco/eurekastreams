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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.profile.UpdateOrganizationNameRequest;
import org.eurekastreams.server.persistence.UpdateCachedOrganizationName;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for UpdateCachedOrganizationNameExecution class.
 * 
 */
public class UpdateCachedOrganizationNameExecutionTest
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
     * The mock mapper to be used by the action.
     */
    private UpdateCachedOrganizationName mapper = context.mock(UpdateCachedOrganizationName.class);

    /**
     * The mock user information from the session.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link UpdateOrganizationNameRequest}.
     */
    private UpdateOrganizationNameRequest request = context.mock(UpdateOrganizationNameRequest.class);

    /**
     * Subject under test.
     */
    private UpdateCachedOrganizationNameExecution sut = null;

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new UpdateCachedOrganizationNameExecution(mapper);
    }

    /**
     * Testing the post action.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void testExecute() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getOrganizationId();
                will(returnValue(1L));

                allowing(request).getNewOrganizationName();
                will(returnValue("newName"));

                oneOf(mapper).execute(1L, "newName");
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }

}
