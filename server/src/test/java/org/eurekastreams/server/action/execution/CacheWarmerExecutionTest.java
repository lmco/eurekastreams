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
package org.eurekastreams.server.action.execution;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.cache.CacheWarmer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for CacheWarmerExecution.
 * 
 */
public class CacheWarmerExecutionTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link CacheWarmer}.
     */
    private CacheWarmer cacheWarmer = context.mock(CacheWarmer.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    @SuppressWarnings("unchecked")
    private TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * System under test.
     */
    private CacheWarmerExecution sut = new CacheWarmerExecution(cacheWarmer);

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContextMock).getUserActionRequests();
                will(returnValue(null));

                oneOf(cacheWarmer).execute(null);
            }
        });

        sut.execute(taskHandlerActionContextMock);
        context.assertIsSatisfied();
    }
}
