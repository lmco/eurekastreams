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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedPersonModelViewCacheMapper;
import org.eurekastreams.server.persistence.mappers.db.ReorderStreamsDbMapper;
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
    private DomainMapper<Long, List<PersonStream>> getOrderedPersonStreamListForPersonByIdMapper = context.mock(
            DomainMapper.class, "getOrderedPersonStreamListForPersonByIdMapper");

    /**
     * Mocked instance of the Principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Reorder Mapper.
     */
    private ReorderStreamsDbMapper reorderMapper = context.mock(ReorderStreamsDbMapper.class);

    /**
     * mapper to remove a personmodelview from cache by person id.
     */
    private RemoveCachedPersonModelViewCacheMapper removeCachedPersonModelViewByIdCacheMapper = context
            .mock(RemoveCachedPersonModelViewCacheMapper.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetStreamOrderExecution(getOrderedPersonStreamListForPersonByIdMapper, reorderMapper,
                removeCachedPersonModelViewByIdCacheMapper);
    }

    /**
     * Test.
     */
    @Test
    public void performAction()
    {
        SetStreamOrderRequest request = new SetStreamOrderRequest(3L, 1, 1);

        final Long personId = 12L;
        final List<PersonStream> streams = new ArrayList<PersonStream>();

        final PersonStream ps1 = context.mock(PersonStream.class, "ps1");
        final PersonStream ps2 = context.mock(PersonStream.class, "ps2");
        streams.add(ps1);
        streams.add(ps2);

        final List<PersonStream> expectedOrder = new ArrayList<PersonStream>();
        expectedOrder.add(ps2);
        expectedOrder.add(ps1);

        context.checking(new Expectations()
        {
            {
                allowing(principalMock).getId();
                will(returnValue(personId));

                oneOf(getOrderedPersonStreamListForPersonByIdMapper).execute(personId);
                will(returnValue(streams));

                allowing(ps1).getStreamId();
                will(returnValue(3L));

                allowing(ps2).getStreamId();
                will(returnValue(4L));

                oneOf(reorderMapper).execute(personId, expectedOrder, 1);

                oneOf(removeCachedPersonModelViewByIdCacheMapper).execute(personId);
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
