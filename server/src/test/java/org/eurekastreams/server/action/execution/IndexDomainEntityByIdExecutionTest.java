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

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for IndexDomainEntityByIdExecution.
 * 
 */
public class IndexDomainEntityByIdExecutionTest
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
     * Entity mapper.
     */
    private FindByIdMapper<DomainEntity> mapper = context.mock(FindByIdMapper.class);

    /**
     * {@link IndexEntity} mapper.
     */
    private IndexEntity<DomainEntity> indexer = context.mock(IndexEntity.class);

    /**
     * {@link DomainEntity}.
     */
    private DomainEntity entity = context.mock(DomainEntity.class);

    /**
     * ActonContext.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private IndexDomainEntityByIdExecution sut = new IndexDomainEntityByIdExecution(mapper, indexer, "foo");

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
                will(returnValue(5L));

                allowing(mapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(entity));

                allowing(indexer).execute(entity);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullActivity()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(5L));

                allowing(mapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(null));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

}
