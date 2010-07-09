/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.fail;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for stream search fetcher.
 *
 */
public class CompositeStreamSearchActivityFetcherTest
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
     * Action mock.
     */
    private ServiceAction getAction = context.mock(ServiceAction.class);

    /**
     * Mocked instance of the {@link ServiceActionController}.
     */
    private ServiceActionController serviceActionControllerMock = context.mock(ServiceActionController.class);

    /**
     * Mocked instance of the {@link OpenSocialPrincipalPopulator} for this test suite.
     */
    private OpenSocialPrincipalPopulator openSocialPrincipalPopulatorMock = context
            .mock(OpenSocialPrincipalPopulator.class);

    /**
     * Find by id mock.
     */
    private FindByIdMapper<StreamSearch> findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Search mock.
     */
    private StreamSearch search = context.mock(StreamSearch.class);

    /**
     * View mock.
     */
    private StreamView view = context.mock(StreamView.class);

    /**
     * Open social id.
     */
    private String openSocialId = "id";

    /**
     * System under test.
     */
    private CompositeStreamSearchActivityFetcher sut = new CompositeStreamSearchActivityFetcher(getAction,
            openSocialPrincipalPopulatorMock, serviceActionControllerMock, findByIdMapper);

    /**
     * Test for the get activities method.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void getActivities() throws Exception
    {
        final PagedSet<ActivityDTO> results = new PagedSet<ActivityDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(search));

                oneOf(search).getKeywordsAsString();
                will(returnValue("keyword"));

                oneOf(search).getStreamView();
                will(returnValue(view));

                oneOf(view).getId();

                oneOf(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(results));

            }
        });

        sut.getActivities(0L, openSocialId, 5);
        context.assertIsSatisfied();
    }

    /**
     * Test for the get activities method.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void getActivitiesWithException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(search));

                oneOf(search).getKeywordsAsString();
                will(returnValue("keywords"));

                oneOf(search).getStreamView();
                will(returnValue(view));

                oneOf(view).getId();

                oneOf(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new Exception()));

            }
        });

        try
        {
            sut.getActivities(0L, openSocialId, 5);
            fail("Should have thrown Exception");
        }
        catch (Exception exception)
        {
            // Need this just for checkstyle.
            int x = 0;
        }

        context.assertIsSatisfied();
    }
}
