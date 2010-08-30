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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test.
 *
 */
public class GetPeopleWhoLikedActivityExecutionTest
{
    /**
     * System under test.
     */
    private GetPeopleWhoLikedActivityExecution sut;

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
     * Mapper.
     */
    private DecoratedPartialResponseDomainMapper<Long, ArrayList<Long>> mapper =
        context.mock(DecoratedPartialResponseDomainMapper.class);

    /**
     * People mapper.
     */
    private GetPeopleByIds peopleMapper = context.mock(GetPeopleByIds.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Test execute.
     */
    @Test
    public final void execute()
    {
        final List<Long> returned = new ArrayList<Long>();
        sut = new GetPeopleWhoLikedActivityExecution(mapper, peopleMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(1L));

                oneOf(mapper).execute(1L);
                will(returnValue(returned));

                oneOf(peopleMapper).execute(returned);
            }
        });

        sut.execute(actionContext);
    }
}
