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

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.ReorderStreams;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetStreamViewOrderExecution} class.
 * 
 */
public class SetStreamOrderExecutionTest
{
    /**
     * System under test.
     */
    private SetStreamOrderExecution sut;

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
     * Person mapper.
     */
    private FindByIdMapper<Person> personMapper = context.mock(FindByIdMapper.class);

    /**
     * Mocked instance of the Principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Reorder Mapper.
     */
    private ReorderStreams reorderMapper = context.mock(ReorderStreams.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetStreamOrderExecution(personMapper, reorderMapper);
    }

    /**
     * Test.
     */
    @Test
    public void performAction()
    {
        SetStreamOrderRequest request = new SetStreamOrderRequest(0L, 1, 1);

        final Person person = context.mock(Person.class);

        final Long personId = 12L;
        
        final Stream stream1 = context.mock(Stream.class, "s1");
        final Stream stream2 = context.mock(Stream.class, "s2");
        final List<Stream> streamList = new LinkedList<Stream>();
        streamList.add(stream1);
        streamList.add(stream2);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();

                oneOf(personMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));
                
                oneOf(person).getId();
                will(returnValue(personId));
                
                oneOf(person).getStreams();
                will(returnValue(streamList));

                allowing(stream1).getId();
                will(returnValue(0L));
                
                oneOf(reorderMapper).execute(personId, streamList, 1);
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
