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

import java.util.Arrays;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.persistence.mappers.cache.RemoveIdsFromLists;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteIdsFromListsExecution class.
 * 
 */
public class DeleteIdsFromListsExecutionTest
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
     * ActionContext mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link RemoveIdsFromLists} mock.
     */
    private RemoveIdsFromLists removeMapper = context.mock(RemoveIdsFromLists.class);

    /**
     * The system under test.
     */
    private DeleteIdsFromListsExecution sut;

    /**
     * Setup sut.
     */
    @Before
    public void setUp()
    {
        sut = new DeleteIdsFromListsExecution(removeMapper);
    }

    /**
     * Test execute method.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                DeleteIdsFromListsRequest request = new DeleteIdsFromListsRequest(Arrays.asList(
                        "someKey", "anotherKey"), Arrays.asList(1L, 2L));
                oneOf(actionContext).getParams();
                will(returnValue(request));

                allowing(removeMapper).execute(with(any(DeleteIdsFromListsRequest.class)));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
