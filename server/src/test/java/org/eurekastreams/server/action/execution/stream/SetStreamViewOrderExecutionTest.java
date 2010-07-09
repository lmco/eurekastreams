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
import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.PersonMapper;
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
public class SetStreamViewOrderExecutionTest
{
    /**
     * System under test.
     */
    private SetStreamViewOrderExecution sut;

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
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Mocked instance of the Principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetStreamViewOrderExecution(personMapper);
    }

    /**
     * Test.
     */
    @Test
    public void performAction()
    {
        SetStreamFilterOrderRequest request = new SetStreamFilterOrderRequest(0L,
                1, 1);

        final Person person = context.mock(Person.class);

        final StreamView view1 = context.mock(StreamView.class, "sv1");
        final StreamView view2 = context.mock(StreamView.class, "sv2");
        final List<StreamView> viewList = new LinkedList<StreamView>();
        viewList.add(view1);
        viewList.add(view2);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();

                oneOf(personMapper).findByAccountId(with(any(String.class)));
                will(returnValue(person));

                oneOf(person).getStreamViewDefinitions();
                will(returnValue(viewList));

                allowing(view1).getId();
                will(returnValue(0L));
                allowing(view2).getId();
                will(returnValue(1L));

                oneOf(person).setStreamViewHiddenLineIndex(1);
                oneOf(personMapper).flush();
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
