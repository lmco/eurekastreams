/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.SaveOrganizationCoordinatorIdsToCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for OrganizationCacheUpdaterExecution.
 */
public class OrganizationCacheUpdaterExecutionTest
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
     * Mocked mapper to get org coordinator ids from the database.
     */
    private final SaveOrganizationCoordinatorIdsToCache orgCoordCacheUpdater = context
            .mock(SaveOrganizationCoordinatorIdsToCache.class);

    /**
     * {@link OrganizationCacheUpdaterRequest}.
     */
    private final OrganizationCacheUpdaterRequest request = context.mock(OrganizationCacheUpdaterRequest.class);

    /**
     * Mocked mapper to retrieve the private group ids a user has the ability to view activities for through an
     * org/group coordinator role.
     */
    private final DomainMapper<Long, Set<Long>> privateGroupIdsCacheRefreshMapperMock = context.mock(
            DomainMapper.class, "privateGroupIdsCacheRefreshMapperMock");

    /**
     * {@link ActionContext}.
     */
    private final ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    private final OrganizationCacheUpdaterExecution sut = new OrganizationCacheUpdaterExecution(
            privateGroupIdsCacheRefreshMapperMock, orgCoordCacheUpdater);

    /**
     * Test execute.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformAction() throws Exception
    {
        final Long groupId = 5L;
        final Long coord1 = 3827L;
        final Long coord2 = 3828L;
        final Long coord3 = 3829L;

        final Set<Long> coordinatorIds = new HashSet<Long>();
        coordinatorIds.add(coord1);
        coordinatorIds.add(coord2);
        coordinatorIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                // when we ask the request for coordinators, it returns 3
                oneOf(request).getCoordinatorIds();
                will(returnValue(coordinatorIds));

                // assert each of the coordinators' search strings are rebuilt
                oneOf(privateGroupIdsCacheRefreshMapperMock).execute(coord1);
                oneOf(privateGroupIdsCacheRefreshMapperMock).execute(coord2);
                oneOf(privateGroupIdsCacheRefreshMapperMock).execute(coord3);

                oneOf(orgCoordCacheUpdater).execute(request);

                allowing(request).getClearRecursiveOrgChildernUpTree();
                will(returnValue(true));

                allowing(request).getOrganizationId();
                will(returnValue(groupId));

            }
        });

        // perform sut
        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
