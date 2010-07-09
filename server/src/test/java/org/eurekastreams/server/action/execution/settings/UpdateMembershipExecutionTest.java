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
package org.eurekastreams.server.action.execution.settings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link UpdateMembershipExecution} class.
 *
 */
public class UpdateMembershipExecutionTest
{
    /**
     * System under test.
     */
    private UpdateMembershipExecution sut;

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
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new UpdateMembershipExecution();
    }

    /**
     * Test that retrieving the requests, retrieves a single request.
     */
    @Test
    public void testRetrieveRequests()
    {
        ServiceActionContext currentContext = new ServiceActionContext(null, null);
        TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerContext =
            new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerContext);
        List<UserActionRequest> requests = currentTaskHandlerContext.getUserActionRequests();
        assertEquals(1, requests.size());
        assertEquals("refreshMembershipAction", requests.get(0).getActionKey());

    }
}
