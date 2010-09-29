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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test suite for the {@link InitializeCacheExecution} class.
 * 
 */
public class InitializeCacheExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Test.
     */
    @Test
    public void testListKeys()
    {
        InitializeCacheExecution sut = new InitializeCacheExecution(cache, new ArrayList<String>(Arrays.asList("key",
                "key1")));

        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                allowing(cache).clear();

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(actionContext);

        assertEquals(2, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullKey()
    {
        List<String> keys = new ArrayList<String>();
        keys.add(null);
        InitializeCacheExecution sut = new InitializeCacheExecution(null, keys);

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testEmptyKeys()
    {
        List<String> keys = new ArrayList<String>();
        keys.add("");
        InitializeCacheExecution sut = new InitializeCacheExecution(null, keys);

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
