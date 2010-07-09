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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.persistence.mappers.stream.UserCompositeStreamIdsMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetCurrentUserCompositeStreamsExecution} class.
 *
 */
public class GetCurrentUserCompositeStreamsExecutionTest
{
    /**
     * System under test.
     */
    private GetCurrentUserCompositeStreamsExecution sut;

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
     * ID mapper mock.
     */
    private UserCompositeStreamIdsMapper idMapper = context.mock(UserCompositeStreamIdsMapper.class);

    /**
     * bulk mapper mock.
     */
    private BulkCompositeStreamsMapper bulkMapper = context.mock(BulkCompositeStreamsMapper.class);

    /**
     * people mapper mock.
     */
    private GetPeopleByIds peopleMapper = context.mock(GetPeopleByIds.class);

    /**
     * Mocked {@link Principal} class.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new GetCurrentUserCompositeStreamsExecution(idMapper, bulkMapper, peopleMapper);
    }

    /**
     * Perform action test.
     *
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void performActionTest() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(principalMock).getId();

                ArrayList<Long> compositeStreamIds = new ArrayList<Long>();
                compositeStreamIds.add(8L);

                ArrayList<StreamView> compositeStreams = new ArrayList<StreamView>();
                StreamView compositeStream = new StreamView();
                compositeStream.setName("streamname");
                compositeStreams.add(compositeStream);

                ArrayList<PersonModelView> people = new ArrayList<PersonModelView>();
                people.add(new PersonModelView());

                oneOf(idMapper).execute(with(any(Long.class)));
                will(returnValue(compositeStreamIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(compositeStreams));

                oneOf(peopleMapper).execute(with(any(ArrayList.class)));
                will(returnValue(people));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(null, principalMock);

        GetCurrentUserStreamFiltersResponse results = (GetCurrentUserStreamFiltersResponse) sut.execute(currentContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getStreamFilters().size());
    }
}
