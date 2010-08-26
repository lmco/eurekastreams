/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the RefreshCachedCompositeStreamExecution.
 * 
 */
public class RefreshCachedCompositeStreamExecutionTest
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
     * Test instance of the mapper.
     */
    private final CompositeStreamActivityIdsMapper testMapper = context.mock(CompositeStreamActivityIdsMapper.class);

    /**
     * Test instance of the request.
     */
    private final RefreshCachedCompositeStreamRequest testRequest = context
            .mock(RefreshCachedCompositeStreamRequest.class);

    /**
     * Test instance of the Cache.
     */
    private final Cache testCache = context.mock(Cache.class);

    /**
     * System under test.
     */
    private RefreshCachedCompositeStreamExecution sut;

    /**
     * Prep the system under test for the test suite.
     */
    @Before
    public void setUp()
    {
        sut = new RefreshCachedCompositeStreamExecution(testMapper,
        //
                CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM, testCache);
    }

    /**
     * Test the execution of the action.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(testRequest).getListToUpdate();

                oneOf(testCache).delete(with(any(String.class)));

                oneOf(testRequest).getListToUpdate();

                oneOf(testRequest).getListOwnerId();

                oneOf(testMapper).execute(with(any(Long.class)), with(any(Long.class)));
            }
        });

        sut.execute(new ActionContext()
        {
            private static final long serialVersionUID = 1496927733112107110L;

            @Override
            public Serializable getParams()
            {
                return testRequest;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

            }
        });

        context.assertIsSatisfied();
    }
}
