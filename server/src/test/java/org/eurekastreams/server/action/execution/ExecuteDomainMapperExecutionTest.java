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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get list of keys of entities to warm.
     */
    private DomainMapper<Serializable, Serializable> domainMapper = context.mock(DomainMapper.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * List of longs.
     */
    private List<Long> results = new ArrayList<Long>(Arrays.asList(5L));

    /**
     * Test.
     */
    @Test
    public void test()
    {
        ExecuteDomainMapperExecution sut = new ExecuteDomainMapperExecution(domainMapper);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue("foo"));

                allowing(domainMapper).execute("foo");
                will(returnValue(results));
            }
        });

        Serializable result = sut.execute(actionContext);

        assertNotNull(result);
        assertTrue(((List<Long>) result).contains(5L));

        context.assertIsSatisfied();
    }

}
