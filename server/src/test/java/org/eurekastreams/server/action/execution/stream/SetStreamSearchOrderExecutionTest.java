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
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetStreamSearchOrderExecution} class.
 *
 */
public class SetStreamSearchOrderExecutionTest
{
    /**
     * System under test.
     */
    private SetStreamSearchOrderExecution sut;

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
     * Principal mock.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetStreamSearchOrderExecution(personMapper);
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

        final StreamSearch search1 = context.mock(StreamSearch.class, "sv1");
        final StreamSearch search2 = context.mock(StreamSearch.class, "sv2");
        final List<StreamSearch> searchList = new LinkedList<StreamSearch>();
        searchList.add(search1);
        searchList.add(search2);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();

                oneOf(personMapper).findByAccountId(with(any(String.class)));
                will(returnValue(person));

                oneOf(person).getStreamSearches();
                will(returnValue(searchList));

                allowing(search1).getId();
                will(returnValue(0L));
                allowing(search2).getId();
                will(returnValue(1L));

                oneOf(person).setStreamSearchHiddenLineIndex(1);
                oneOf(personMapper).flush();
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
