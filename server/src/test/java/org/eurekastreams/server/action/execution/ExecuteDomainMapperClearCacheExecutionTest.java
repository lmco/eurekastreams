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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Set;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests ExecuteDomainMapperClearCacheExecution.
 */
public class ExecuteDomainMapperClearCacheExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests executing.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final Transformer<ActionContext, Serializable> parameterSupplier = context.mock(Transformer.class,
                "parameterSupplier");
        final DomainMapper<Serializable, Serializable> domainMapper = context.mock(DomainMapper.class, "domainMapper");
        final Cache cache = context.mock(Cache.class);
        final Transformer<ActionContext, Serializable> cacheKeyParameterSupplier = context.mock(Transformer.class,
                "cacheKeyParameterSupplier");
        final Serializable params = context.mock(Serializable.class, "params");
        final Serializable extractedParams = context.mock(Serializable.class, "extractedParams");
        final Serializable result = context.mock(Serializable.class, "result");
        final String cacheKeyPrefix = "PREFIX";
        final String cacheKeySuffix = "SUFFIX";
        final String cacheKey = cacheKeyPrefix + cacheKeySuffix;

        TaskHandlerExecutionStrategy<PrincipalActionContext> sut = new ExecuteDomainMapperClearCacheExecution(
                parameterSupplier, domainMapper, cache, cacheKeyPrefix, cacheKeyParameterSupplier);

        final TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(params, "user", 9L);

        context.checking(new Expectations()
        {
            {
                oneOf(parameterSupplier).transform(actionContext.getActionContext());
                will(returnValue(extractedParams));

                oneOf(domainMapper).execute(extractedParams);
                will(returnValue(result));

                oneOf(cacheKeyParameterSupplier).transform(actionContext.getActionContext());
                will(returnValue(cacheKeySuffix));

                oneOf(cache).delete(cacheKey);
            }
        });

        Serializable results = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertSame(result, results);
        assertEquals(1, actionContext.getUserActionRequests().size());
        UserActionRequest request = actionContext.getUserActionRequests().get(0);
        assertEquals("deleteCacheKeysAction", request.getActionKey());
        assertEquals(1, ((Set<String>) request.getParams()).size());
        assertTrue(((Set<String>) request.getParams()).contains(cacheKey));
    }
}
