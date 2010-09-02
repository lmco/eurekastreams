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
package org.eurekastreams.server.action.authorization.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ViewActivityAuthorizationStrategy.
 */
public class ViewActivityAuthorizationStrategyTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link ActivitySecurityDTO} DAO.
     */
    private DomainMapper securityMapper = context.mock(DomainMapper.class);

    /**
     * Mapper to get the list of group ids that includes private groups the current user can see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper = context
            .mock(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);
    /**
     * {@link ServiceActionContext}.
     */
    private ServiceActionContext actionContext = context.mock(ServiceActionContext.class);

    /**
     * System under test.
     */
    private ViewActivityAuthorizationStrategy sut = new ViewActivityAuthorizationStrategy(securityMapper,
            getVisibleGroupsForUserMapper);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * {@link ActivitySecurityDTO}.
     */
    private ActivitySecurityDTO asd = context.mock(ActivitySecurityDTO.class);

    /**
     * Activity id used in tests.
     */
    private Long activityId = 5L;

    /**
     * Principal id used in tests.
     */
    private Long principalId = 6L;

    /**
     * Destination entity id used in tests.
     */
    private Long destinationEntityId = 7L;

    /**
     * Test.
     */
    @Test
    public void testStreamIsPublic()
    {
        final Collection<ActivitySecurityDTO> asdCollection = new ArrayList<ActivitySecurityDTO>();
        asdCollection.add(asd);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(securityMapper).execute(with(any(List.class)));
                will(returnValue(asdCollection));

                allowing(asd).isDestinationStreamPublic();
                will(returnValue(true));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testStreamIsNotPublicUserInGroup()
    {
        final Collection<ActivitySecurityDTO> asdCollection = new ArrayList<ActivitySecurityDTO>();
        asdCollection.add(asd);

        final Set<Long> visibleDestinationEntityIds = new HashSet<Long>();
        visibleDestinationEntityIds.add(destinationEntityId);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(securityMapper).execute(with(any(List.class)));
                will(returnValue(asdCollection));

                allowing(asd).isDestinationStreamPublic();
                will(returnValue(false));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(getVisibleGroupsForUserMapper).execute(principalId);
                will(returnValue(visibleDestinationEntityIds));

                allowing(asd).getDestinationEntityId();
                will(returnValue(destinationEntityId));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testStreamIsNotPublicUserNotInGroup()
    {
        final Collection<ActivitySecurityDTO> asdCollection = new ArrayList<ActivitySecurityDTO>();
        asdCollection.add(asd);

        final Set<Long> visibleDestinationEntityIds = new HashSet<Long>();
        visibleDestinationEntityIds.add(destinationEntityId);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(securityMapper).execute(with(any(List.class)));
                will(returnValue(asdCollection));

                allowing(asd).isDestinationStreamPublic();
                will(returnValue(false));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(getVisibleGroupsForUserMapper).execute(principalId);
                will(returnValue(visibleDestinationEntityIds));

                allowing(asd).getDestinationEntityId();
                will(returnValue(destinationEntityId + 1));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
