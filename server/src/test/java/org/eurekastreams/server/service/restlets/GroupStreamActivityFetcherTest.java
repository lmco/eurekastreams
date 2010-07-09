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
package org.eurekastreams.server.service.restlets;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the group stream activity fetcher.
 */
public class GroupStreamActivityFetcherTest
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
     * Action.
     */
    private final ServiceAction action = context.mock(ServiceAction.class);

    /**
     * Service Action Controller.
     */
    private final ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * Principal populator.
     */
    private final PrincipalPopulator principalPopulator = context.mock(PrincipalPopulator.class);

    /**
     * Mock group mapper.
     */
    private final GetDomainGroupsByIds groupMapper = context.mock(GetDomainGroupsByIds.class);

    /**
     * PagedSet of activity dtos.
     */
    private final PagedSet<ActivityDTO> results = new PagedSet<ActivityDTO>();

    /**
     * Open Social Id.
     */
    private final String openSocialId = "id";

    /**
     * System under test.
     */
    private GroupStreamActivityFetcher sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GroupStreamActivityFetcher(action, serviceActionController, principalPopulator, groupMapper);
    }

    /**
     * Test for the get activities method.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void getActivities() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).execute(with(any(List.class)));
                will(returnValue(Arrays.asList(new DomainGroupModelView())));

                allowing(principalPopulator).getPrincipal(openSocialId);

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(results));
            }
        });

        sut.getActivities(0L, openSocialId, 5);
        context.assertIsSatisfied();
    }
}
