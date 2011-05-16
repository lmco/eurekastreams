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

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StreamPopularHashTagsAuthorization.
 */
public class StreamPopularHashTagsAuthorizationTest
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
     * Mapper to get domain groups by short name.
     */
    private final GetDomainGroupsByShortNames groupsMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to check for group access.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser groupAccessMapper = context
            .mock(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);

    /**
     * System under test.
     */
    private StreamPopularHashTagsAuthorization sut = new StreamPopularHashTagsAuthorization(groupsMapper,
            groupAccessMapper);

    /**
     * Action context.
     */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * User's Principal.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * Group.
     */
    private final DomainGroupModelView group = context.mock(DomainGroupModelView.class);

    /**
     * User's id.
     */
    private final Long userId = 3828L;

    /**
     * Test authorize for a person stream.
     */
    @Test
    public void testAuthorizeForPersonStream()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new StreamPopularHashTagsRequest(ScopeType.PERSON, "accountid")));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize for a person stream.
     */
    @Test
    public void testAuthorizeForPublicGroupStream()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(group).isPublic();
                will(returnValue(true));

                oneOf(actionContext).getParams();
                will(returnValue(new StreamPopularHashTagsRequest(ScopeType.GROUP, "shortname")));

                oneOf(groupsMapper).fetchUniqueResult("shortname");
                will(returnValue(group));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize for a person stream.
     */
    @Test
    public void testAuthorizeForPrivateGroupWithAccess()
    {
        final Long groupId = 828L;

        final HashSet<Long> accessiblePrivateGroupIds = new HashSet<Long>();
        accessiblePrivateGroupIds.add(groupId);

        context.checking(new Expectations()
        {
            {
                oneOf(group).isPublic();
                will(returnValue(false));

                oneOf(group).getId();
                will(returnValue(groupId));

                oneOf(actionContext).getParams();
                will(returnValue(new StreamPopularHashTagsRequest(ScopeType.GROUP, "shortname")));

                oneOf(groupsMapper).fetchUniqueResult("shortname");
                will(returnValue(group));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(userId));

                oneOf(groupAccessMapper).execute(userId);
                will(returnValue(accessiblePrivateGroupIds));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize for a person stream.
     */
    @Test
    public void testAuthorizeForPrivateGroupWithNoAccess()
    {
        final Long groupId = 828L;

        final HashSet<Long> accessiblePrivateGroupIds = new HashSet<Long>();
        accessiblePrivateGroupIds.add(1L);
        accessiblePrivateGroupIds.add(2L);
        accessiblePrivateGroupIds.add(3L);
        accessiblePrivateGroupIds.add(4L);

        context.checking(new Expectations()
        {
            {
                oneOf(group).isPublic();
                will(returnValue(false));

                oneOf(group).getId();
                will(returnValue(groupId));

                oneOf(actionContext).getParams();
                will(returnValue(new StreamPopularHashTagsRequest(ScopeType.GROUP, "shortname")));

                oneOf(groupsMapper).fetchUniqueResult("shortname");
                will(returnValue(group));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(userId));

                oneOf(groupAccessMapper).execute(userId);
                will(returnValue(accessiblePrivateGroupIds));

                // for logging
                allowing(principal).getAccountId();
                will(returnValue("accountid"));

                allowing(group).getShortName();
                will(returnValue("shortname"));
            }
        });

        boolean exceptionFired = false;
        try
        {
            sut.authorize(actionContext);
        }
        catch (AuthorizationException ex)
        {
            exceptionFired = true;
        }
        assertTrue(exceptionFired);
        context.assertIsSatisfied();
    }
}
