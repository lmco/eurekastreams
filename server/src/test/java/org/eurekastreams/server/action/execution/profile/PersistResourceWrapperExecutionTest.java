/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PersistResourceWrapperExecution.
 */
@SuppressWarnings("unchecked")
public class PersistResourceWrapperExecutionTest
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
     * Mocked instance of {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * Transformer to convert persistResource result to result sent to client.
     */
    private Transformer transformer = context.mock(Transformer.class);

    /**
     * Persist resource action.
     */
    private TaskHandlerExecutionStrategy<PrincipalActionContext> persistResourceExecution = context
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * System under test.
     */
    private PersistResourceWrapperExecution sut = new PersistResourceWrapperExecution(persistResourceExecution,
            transformer);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(persistResourceExecution).execute(taskHandlerActionContext);
                will(returnValue(null));

                oneOf(transformer).transform(null);
                will(returnValue(null));
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

}
