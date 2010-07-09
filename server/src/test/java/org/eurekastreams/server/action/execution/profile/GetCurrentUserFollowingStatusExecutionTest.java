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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetCurrentUserFollowingStatusExecution.
 */
public class GetCurrentUserFollowingStatusExecutionTest
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
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * GroupMapper used to retrieve a group if that's the target type.
     */
    private DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);

    /**
     * Mocked request object.
     */
    private GetCurrentUserFollowingStatusRequest getCurrentUserFollowingStatusRequest = context
            .mock(GetCurrentUserFollowingStatusRequest.class);

    /**
     * Mapper that looks-to/loads cache with people modelviews by open social id.
     */
    private GetPeopleByOpenSocialIds getPeopleByOpenSocialIdsMapper = context.mock(GetPeopleByOpenSocialIds.class);

    /**
     * OpenSocial Id regex.
     */
    private String openSocialRegEx = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";

    /**
     * Subject under test.
     */
    private GetCurrentUserFollowingStatusExecution sut;

    /**
     * Current user person object stored in ExtendedUserDetails.
     */
    // private Person userPerson = context.mock(Person.class);
    /**
     * Account Id for the current user.
     */
    private static final String CURRENT_USER = "currentuser";

    /**
     * Open social Id for the target user.
     */
    private static final String TARGET_USER_OPENSOCIAL_ID = "2d359911-0977-418a-9490-57e8252b1a56";

    /**
     * Account Id for the user whose profile is being viewed.
     */
    private static final String TARGET_USER = "targetuser";

    /**
     * Account Id for the org whose profile is being viewed.
     */
    private static final String TARGET_GROUP = "targetgroup";

    /**
     * Mapper to get people by open social ids.
     */
    // private GetPeopleByOpenSocialIds getPeopleByOpenSocialIdsMapper;
    /**
     * Current user model view mock.
     */
    private PersonModelView targetUser = context.mock(PersonModelView.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetCurrentUserFollowingStatusExecution(personMapper, groupMapper, getPeopleByOpenSocialIdsMapper,
                openSocialRegEx);
    }

    /**
     * Test followed entity id is an OpenSocial Id and entity type is a person. Entity id is not current user and
     * followed is true.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testOpenSocialEntityIdIsPersonNotSameIsFollowing() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_USER_OPENSOCIAL_ID));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(getPeopleByOpenSocialIdsMapper).fetchUniqueResult(TARGET_USER_OPENSOCIAL_ID);
                will(returnValue(targetUser));

                oneOf(targetUser).getAccountId();
                will(returnValue(TARGET_USER));

                oneOf(personMapper).isFollowing(CURRENT_USER, TARGET_USER);
                will(returnValue(true));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.FOLLOWING);
        context.assertIsSatisfied();
    }

    /**
     * Test followed entity id is an account id and entity type is a person. Entity id is not current user and followed
     * is true.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testAccountIdEntityIdIsPersonNotSameIsFollowing() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(personMapper).isFollowing(CURRENT_USER, TARGET_USER);
                will(returnValue(true));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.FOLLOWING);
        context.assertIsSatisfied();
    }

    /**
     * Test followed entity id is an short name and entity type is a group. Entity id is not current user and followed
     * is true.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testShortNameEntityIdIsGroupNotSameIsFollowing() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_GROUP));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.GROUP));

                oneOf(groupMapper).isFollowing(CURRENT_USER, TARGET_GROUP);
                will(returnValue(true));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.FOLLOWING);
        context.assertIsSatisfied();
    }

    /**
     * Test followed entity id is an OpenSocial Id and entity type is a person. Entity id is current user.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testOpenSocialEntityIdIsPersonSame() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_USER_OPENSOCIAL_ID));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(getPeopleByOpenSocialIdsMapper).fetchUniqueResult(TARGET_USER_OPENSOCIAL_ID);
                will(returnValue(targetUser));

                oneOf(targetUser).getAccountId();
                will(returnValue(CURRENT_USER));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.DISABLED);
        context.assertIsSatisfied();
    }

    /**
     * Test followed entity id is an OpenSocial Id and entity type is a person. Entity id is not current user and not
     * followed.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testOpenSocialEntityIdIsPersonNotSameNotFollowed() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_USER_OPENSOCIAL_ID));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(getPeopleByOpenSocialIdsMapper).fetchUniqueResult(TARGET_USER_OPENSOCIAL_ID);
                will(returnValue(targetUser));

                oneOf(targetUser).getAccountId();
                will(returnValue(TARGET_USER));

                oneOf(personMapper).isFollowing(CURRENT_USER, TARGET_USER);
                will(returnValue(false));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.NOTFOLLOWING);
        context.assertIsSatisfied();
    }

    /**
     * Test invalid entity type.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void testInvalidEntityTypeReturnsDisabled() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getCurrentUserFollowingStatusRequest));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                oneOf(getCurrentUserFollowingStatusRequest).getFollowedEntityId();
                will(returnValue(TARGET_USER_OPENSOCIAL_ID));

                oneOf(getCurrentUserFollowingStatusRequest).getEntityType();
                will(returnValue(EntityType.NOTSET));
            }
        });

        assertEquals((Follower.FollowerStatus) sut.execute(actionContext), Follower.FollowerStatus.DISABLED);
        context.assertIsSatisfied();
    }

}
