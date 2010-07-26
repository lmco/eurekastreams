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

import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.db.SetPersonLockedStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for SetPersonLockedStatusExecution.
 * 
 */
public class SetPersonLockedStatusExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link SetPersonLockedStatus}.
     */
    private SetPersonLockedStatus setLockedStatusDAO = context.mock(SetPersonLockedStatus.class);;

    /**
     * {@link AsyncActionContext}.
     */
    private AsyncActionContext actionContext = context.mock(AsyncActionContext.class);

    /**
     * {@link SetPersonLockedStatusRequest}.
     */
    private SetPersonLockedStatusRequest request = context.mock(SetPersonLockedStatusRequest.class);

    /**
     * System under test.
     */
    private SetPersonLockedStatusExecution sut = new SetPersonLockedStatusExecution(setLockedStatusDAO);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(setLockedStatusDAO).execute(request);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
