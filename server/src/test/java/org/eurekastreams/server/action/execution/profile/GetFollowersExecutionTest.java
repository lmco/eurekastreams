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

import java.util.ArrayList;
import java.util.Collection;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetCurrentUserFollowingStatusExecution.
 */
@SuppressWarnings("unchecked")
public class GetFollowersExecutionTest
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
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * GroupMapper used to retrieve a group if that's the target type.
     */
    private DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);

    /**
     * Mapper to populate OrganizationChildren's parentOrganization with skeleton orgs from cache.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper orgChildrenSkeletonParentOrgPopulatorCacheMapper = context
            .mock(PopulateOrgChildWithSkeletonParentOrgsCacheMapper.class);

    /**
     * Mocked request object.
     */
    private GetFollowersFollowingRequest getFollowersFollowingRequest = context
            .mock(GetFollowersFollowingRequest.class);

    /**
     * Subject under test.
     */
    private GetFollowersExecution sut;

    /**
     * Account Id for the user whose profile is being viewed.
     */
    private static final String TARGET_USER = "targetuser";

    /**
     * Account Id for the org whose profile is being viewed.
     */
    private static final String TARGET_GROUP = "targetgroup";

    /**
     * Start value.
     */
    private static final Integer START_VALUE = 40;

    /**
     * End value.
     */
    private static final Integer END_VALUE = 100;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetFollowersExecution(personMapper, groupMapper, orgChildrenSkeletonParentOrgPopulatorCacheMapper);
    }

    /**
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void executePersonFollowers() throws Exception
    {
        // for now, manually update until the back end is ready
        // this simulates a successful callback
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0; i < 8; i++)
        {
            Person p = new Person("secon" + i, "Stephen", "X", "Economopolus", "Steve");
            p.setTitle("Chief Strategist");
            list.add(p);
        }
        final PagedSet<Person> connections = new PagedSet<Person>(0, 7, 8, list);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersFollowingRequest));

                oneOf(getFollowersFollowingRequest).getEntityId();
                will(returnValue(TARGET_USER));

                oneOf(getFollowersFollowingRequest).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(getFollowersFollowingRequest).getStartIndex();
                will(returnValue(START_VALUE));

                oneOf(getFollowersFollowingRequest).getEndIndex();
                will(returnValue(END_VALUE));

                oneOf(personMapper).getFollowers(TARGET_USER, START_VALUE, END_VALUE);
                will(returnValue(connections));

                oneOf(orgChildrenSkeletonParentOrgPopulatorCacheMapper).populateParentOrgSkeletons(
                        with(any(Collection.class)));
            }
        });

        assertEquals((PagedSet<Person>) sut.execute(actionContext), connections);
        context.assertIsSatisfied();
    }

    /**
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void executeGroupFollowers() throws Exception
    {
        // for now, manually update until the back end is ready
        // this simulates a successful callback.
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0; i < 8; i++)
        {
            Person p = new Person("secon" + i, "Stephen", "X", "Economopolus", "Steve");
            p.setTitle("Chief Strategist");
            list.add(p);
        }
        final PagedSet<Person> connections = new PagedSet<Person>(0, 7, 8, list);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersFollowingRequest));

                oneOf(getFollowersFollowingRequest).getEntityId();
                will(returnValue(TARGET_GROUP));

                oneOf(getFollowersFollowingRequest).getEntityType();
                will(returnValue(EntityType.GROUP));

                oneOf(getFollowersFollowingRequest).getStartIndex();
                will(returnValue(START_VALUE));

                oneOf(getFollowersFollowingRequest).getEndIndex();
                will(returnValue(END_VALUE));

                oneOf(groupMapper).getFollowers(TARGET_GROUP, START_VALUE, END_VALUE);
                will(returnValue(connections));

                oneOf(orgChildrenSkeletonParentOrgPopulatorCacheMapper).populateParentOrgSkeletons(
                        with(any(Collection.class)));
            }
        });

        assertEquals((PagedSet<Person>) sut.execute(actionContext), connections);
        context.assertIsSatisfied();
    }

    /**
     * 
     * @throws Exception
     *             exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void executeWithBadTargetType() throws Exception
    {
        // for now, manually update until the back end is ready
        // this simulates a successful callback.
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0; i < 8; i++)
        {
            Person p = new Person("secon" + i, "Stephen", "X", "Economopolus", "Steve");
            p.setTitle("Chief Strategist");
            list.add(p);
        }
        final PagedSet<Person> connections = new PagedSet<Person>(0, 7, 8, list);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersFollowingRequest));

                oneOf(getFollowersFollowingRequest).getEntityId();
                will(returnValue(TARGET_USER));

                oneOf(getFollowersFollowingRequest).getEntityType();
                will(returnValue(EntityType.NOTSET));

                oneOf(getFollowersFollowingRequest).getStartIndex();
                will(returnValue(START_VALUE));

                oneOf(getFollowersFollowingRequest).getEndIndex();
                will(returnValue(END_VALUE));
            }
        });

        assertEquals((PagedSet<Person>) sut.execute(actionContext), connections);
        context.assertIsSatisfied();
    }

}
