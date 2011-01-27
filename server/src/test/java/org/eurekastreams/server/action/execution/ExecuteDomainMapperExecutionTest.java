/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ExecuteDomainMapperExecution.
 *
 */
public class ExecuteDomainMapperExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get list of keys of entities to warm.
     */
    private final DomainMapper<Serializable, Serializable> domainMapper = context.mock(DomainMapper.class);

    /** Strategy to supply mapper parameters from the action context. */
    private final Transformer<ActionContext, Serializable> parameterSupplier = context.mock(Transformer.class);

    /**
     * {@link ActionContext}.
     */
    private final ActionContext actionContext = context.mock(ActionContext.class);

    /** Mapper result. */
    private final Serializable mapperResult = context.mock(Serializable.class, "mapperResult");

    /** Extracted parameter. */
    private final Serializable parameter = context.mock(Serializable.class, "parameter");

    /**
     * Test.
     */
    @Test
    public void test()
    {
        ExecuteDomainMapperExecution sut = new ExecuteDomainMapperExecution(parameterSupplier, domainMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(parameterSupplier).transform(with(same(actionContext)));
                will(returnValue(parameter));

                oneOf(domainMapper).execute(with(same(parameter)));
                will(returnValue(mapperResult));
            }
        });

        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertSame(result, mapperResult);
    }
}
