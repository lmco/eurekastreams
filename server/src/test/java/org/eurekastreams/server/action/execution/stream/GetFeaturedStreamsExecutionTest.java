/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.strategies.FeaturedStreamDTOTransientDataPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetFeaturedStreamsExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class GetFeaturedStreamsExecutionTest
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
     * {@link FeaturedStreamDTOTransientDataPopulator}.
     */
    private FeaturedStreamDTOTransientDataPopulator transientDataPopulator = context
            .mock(FeaturedStreamDTOTransientDataPopulator.class);

    /**
     * Mapper to retrieve featured stream DTOs.
     */
    private DomainMapper<MapperRequest, List<FeaturedStreamDTO>> featuredStreamDTOMapper = context.mock(
            DomainMapper.class, "featuredStreamDTOMapper");

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principle.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * User ID.
     */
    private final Long userId = 5L;

    /**
     * System under test.
     */
    private GetFeaturedStreamsExecution sut = new GetFeaturedStreamsExecution(featuredStreamDTOMapper,
            transientDataPopulator);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final ArrayList<FeaturedStreamDTO> results = new ArrayList<FeaturedStreamDTO>();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(userId));

                oneOf(featuredStreamDTOMapper).execute(null);
                will(returnValue(results));

                oneOf(transientDataPopulator).execute(userId, results);
                will(returnValue(results));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();

    }

}
