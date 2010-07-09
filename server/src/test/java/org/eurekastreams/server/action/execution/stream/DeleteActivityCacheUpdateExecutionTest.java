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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivityCacheUpdate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteActivityCacheUpdateExecution class.
 * 
 */
public class DeleteActivityCacheUpdateExecutionTest
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
     * Mock DeleteActivityCacheUpdate DAO.
     */
    private DeleteActivityCacheUpdate deleteActivityCacheUpdateDAO = context.mock(DeleteActivityCacheUpdate.class);

    /**
     * Mock DeleteActivityCacheUpdateRequest.
     */
    private DeleteActivityCacheUpdateRequest request = context.mock(DeleteActivityCacheUpdateRequest.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private DeleteActivityCacheUpdateExecution sut;

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new DeleteActivityCacheUpdateExecution(deleteActivityCacheUpdateDAO);
    }

    /**
     * Testing the post action.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void testPerformAction() throws Exception
    {

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(deleteActivityCacheUpdateDAO).execute(request);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

}
