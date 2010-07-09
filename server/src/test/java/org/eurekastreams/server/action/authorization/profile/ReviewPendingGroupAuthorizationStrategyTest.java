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
package org.eurekastreams.server.action.authorization.profile;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ReviewPendingGroupAuthorizationStrategy.
 */
public class ReviewPendingGroupAuthorizationStrategyTest
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
     * The user name.
     */
    private String userName = "pcollins";

    /**
     * mapper that looks up org coordinators from cache.
     */
    private GetRecursiveOrgCoordinators getRecursiveOrgCoordinatorsMock = context
            .mock(GetRecursiveOrgCoordinators.class);

    /**
     * Group Cache mapper.
     */
    private DomainGroupMapper getDomainGroupsMock = context.mock(DomainGroupMapper.class);

    /**
     * Subject under test.
     */
    private ReviewPendingGroupAuthorizationStrategy sut;

    /**
     * group mock shortName.
     */
    private String groupShortName = "testGroupName";

    /**
     * Request.
     */
    private final ReviewPendingGroupRequest request = new ReviewPendingGroupRequest(groupShortName, true);

    /**
     * groupMock.
     */
    private DomainGroup groupMock = context.mock(DomainGroup.class);

    /**
     * orgMock.
     */
    private Organization orgMock = context.mock(Organization.class);

    /**
     * User principal.
     */
    private Principal userPrincipal;

    /**
     * Setup method run before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ReviewPendingGroupAuthorizationStrategy(getRecursiveOrgCoordinatorsMock, getDomainGroupsMock);
        userPrincipal = new Principal()
        {
            /**
             * serial version uid.
             */
            private static final long serialVersionUID = -7392506331073026427L;

            @Override
            public String getOpenSocialId()
            {
                return null;
            }

            @Override
            public Long getId()
            {
                return null;
            }

            @Override
            public String getAccountId()
            {
                return userName;
            }
        };
    }

    /**
     * Verify that this succeeds.
     */
    @Test
    public void userHasAuthorization()
    {
        setupCommon(true);
        sut.authorize(getPrincipalActionContext());
    }

    /**
     * verify that the exception is thrown when this fails.
     */
    @Test(expected = AuthorizationException.class)
    public void userNoAuthorization()
    {
        setupCommon(false);
        sut.authorize(getPrincipalActionContext());
    }

    /**
     * Setup is the same except for results.
     *
     * @param success
     *            If the authorization should be successful.
     */
    @SuppressWarnings("deprecation")
    public void setupCommon(final boolean success)
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getDomainGroupsMock).findByShortName(groupShortName);
                will(returnValue(groupMock));

                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getId();
                will(returnValue(7L));

                oneOf(getRecursiveOrgCoordinatorsMock).isOrgCoordinatorRecursively(userName, 7L);
                will(returnValue(success));
            }
        });
    }

    /**
     * Get the principal action context.
     *
     * @return the principal action context.
     */
    private PrincipalActionContext getPrincipalActionContext()
    {
        return new PrincipalActionContext()
        {
            /**
             * Serial version uid.
             */
            private static final long serialVersionUID = 949085751758669842L;

            /**
             * State - none.
             *
             * @return the state
             */
            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            /**
             * Params - none.
             *
             * @return the params
             */
            @Override
            public Serializable getParams()
            {
                return request;
            }

            /**
             * User principal.
             *
             * @return the user principal
             */
            @Override
            public Principal getPrincipal()
            {
                return userPrincipal;
            }
        };
    }
}
