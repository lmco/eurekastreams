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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReportDTO;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test.
 *
 */
public class GetAllPopularHashTagsFromGroupsJoinedExecutionTest
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
     * System under test.
     */
    private GetAllPopularHashTagsFromGroupsJoinedExecution sut;

    /**
     * Mapper to get the popular hashtags for a stream.
     */
    private final DomainMapper<List<StreamPopularHashTagsRequest>,
    List<StreamPopularHashTagsReportDTO>> popularHashTagsMapper = context.mock(DomainMapper.class);


    /**
     * Groups mapper.
     */
    private final ExecutionStrategy<PrincipalActionContext> getGroups = context.mock(ExecutionStrategy.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal.
     */
    private Principal principal = context.mock(Principal.class);


    /**
     * Test.
     */
    @Test
    public void execute()
    {
        final GetCurrentUserStreamFiltersResponse groupResponse = new GetCurrentUserStreamFiltersResponse();
        final List<StreamFilter> filters = new ArrayList<StreamFilter>();
        GroupStreamDTO groupFilter = new GroupStreamDTO(1L, "", "shortName", null, 1L, false);
        filters.add(groupFilter);
        groupResponse.setStreamFilters(filters);

        final List<StreamPopularHashTagsReportDTO> responses = new ArrayList<StreamPopularHashTagsReportDTO>();
        final StreamPopularHashTagsReportDTO response1 = context.mock(StreamPopularHashTagsReportDTO.class, "r1");
        final StreamPopularHashTagsReportDTO response2 = context.mock(StreamPopularHashTagsReportDTO.class, "r2");
        responses.add(response1);
        responses.add(response2);

        sut = new GetAllPopularHashTagsFromGroupsJoinedExecution(popularHashTagsMapper, getGroups);

        context.checking(new Expectations()
        {
            {
                oneOf(getGroups).execute(actionContext);
                will(returnValue(groupResponse));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue("joeSmith"));

                oneOf(popularHashTagsMapper).execute(with(any(List.class)));
                will(returnValue(responses));

                allowing(response1).getPopularHashTags();
                will(returnValue(null));

                allowing(response2).getPopularHashTags();
                will(returnValue(new ArrayList<String>()));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
