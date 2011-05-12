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
package org.eurekastreams.server.persistence.strategies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for ActivityDeletePropertyStrategy class.
 * 
 */
public class ActivityDeletePropertyStrategyTest
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
     * Mapper to get a PersonModelView from their account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context
            .mock(DomainMapper.class);

    /**
     * DAO for looking up group by short name.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper for getting system administrator ids.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class,
            "systemAdminIdsMapper");

    /**
     * Destination stream StreamEntityDTO.
     */
    private StreamEntityDTO activityDestinationStream = context.mock(StreamEntityDTO.class, "destination");

    /**
     * Actor StreamEntityDTO.
     */
    private StreamEntityDTO activityActorStream = context.mock(StreamEntityDTO.class, "actor");

    /**
     * ActivityDTO mock.
     */
    private ActivityDTO activity = context.mock(ActivityDTO.class);

    /**
     * user id.
     */
    private final long userPersonId = 99L;

    /**
     * group id.
     */
    private final long groupId = 38982L;

    /**
     * user acctounId.
     */
    private String userAcctId = "smithers";

    /**
     * unique key for destination personal stream.
     */
    private String activityDestinationStreamAcctId = "mrburns";

    /**
     * PersonModelView representing destination stream.
     */
    private PersonModelView activityDestinationPersonModelView = context.mock(PersonModelView.class);

    /**
     * Group short name.
     */
    private String groupShortName = "groupShortName";

    /**
     * Mapper to check if the user has coordinator access to a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupAccessMapper = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * System under test.
     */
    private ActivityDeletePropertyStrategy sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new ActivityDeletePropertyStrategy(groupByShortNameDAO, groupAccessMapper, systemAdminIdsMapper);
    }

    /**
     * Test execute with user is null.
     */
    @Test
    public void testExecuteUserIsNull()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activity).setDeletable(false);
            }
        });

        sut.execute(null, null, activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with user as activity author.
     */
    @Test
    public void testExecuteUserIsActivityAuthorOnly()
    {
        context.checking(new Expectations()
        {
            {
                allowing(activity).getActor();
                will(returnValue(activityActorStream));

                oneOf(activityActorStream).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(activityActorStream).getUniqueIdentifier();
                will(returnValue(userAcctId));

                oneOf(activity).setDeletable(true);
            }
        });

        sut.execute(userAcctId, userPersonId, activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with user as stream owner.
     */
    @Test
    public void testExecuteUserIsStreamOwner()
    {
        context.checking(new Expectations()
        {
            {
                // this will get us past author check
                allowing(activity).getActor();
                will(returnValue(activityActorStream));

                oneOf(activityActorStream).getType();
                will(returnValue(EntityType.NOTSET));
                // end author check skip

                allowing(activity).getDestinationStream();
                will(returnValue(activityDestinationStream));

                oneOf(activityDestinationStream).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(activityDestinationStream).getUniqueIdentifier();
                will(returnValue(userAcctId));

                oneOf(activity).setDeletable(true);
            }
        });

        sut.execute(userAcctId, userPersonId, activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with user as destination stream group Coordinator.
     */
    @Test
    public void testExecuteUserActivityGroupCoordinatorRole()
    {
        context.checking(new Expectations()
        {
            {
                // this will get us past author check
                allowing(activity).getActor();
                will(returnValue(activityActorStream));

                oneOf(activityActorStream).getType();
                will(returnValue(EntityType.NOTSET));
                // end author check skip

                // this will get us past stream owner check
                allowing(activity).getDestinationStream();
                will(returnValue(activityDestinationStream));

                oneOf(activityDestinationStream).getType();
                will(returnValue(EntityType.NOTSET));
                // end stream owner check skip

                oneOf(activityDestinationStream).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(activityDestinationStream).getUniqueIdentifier();
                will(returnValue(groupShortName));

                oneOf(groupByShortNameDAO).fetchId(groupShortName);
                will(returnValue(groupId));

                oneOf(groupAccessMapper).hasGroupCoordinatorAccessRecursively(userPersonId, groupId);
                will(returnValue(true));

                oneOf(activity).setDeletable(true);
            }
        });

        sut.execute(userAcctId, userPersonId, activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with user as destination stream group Coordinator.
     */
    @Test
    public void testExecuteUserActivityPersonalStreamSystemAdmin()
    {
        final List<Long> systemAdminIds = new ArrayList<Long>();
        systemAdminIds.add(userPersonId);

        context.checking(new Expectations()
        {
            {
                // this will get us past author check
                allowing(activity).getActor();
                will(returnValue(activityActorStream));

                oneOf(activityActorStream).getType();
                will(returnValue(EntityType.NOTSET));
                // end author check skip

                // this will get us past stream owner check
                allowing(activity).getDestinationStream();
                will(returnValue(activityDestinationStream));

                oneOf(activityDestinationStream).getType();
                will(returnValue(EntityType.NOTSET));
                // end stream owner check skip

                // skip group coordinator check.
                oneOf(activityDestinationStream).getType();
                will(returnValue(EntityType.NOTSET));

                oneOf(systemAdminIdsMapper).execute(null);
                will(returnValue(systemAdminIds));

                oneOf(activity).setDeletable(true);
            }
        });

        sut.execute(userAcctId, userPersonId, activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with user as non of the above.
     */
    @Test
    public void testExecuteUserIsNobody()
    {
        final List<Long> systemAdminIds = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                // this will get us past author check
                allowing(activity).getActor();
                will(returnValue(activityActorStream));

                oneOf(activityActorStream).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(activityActorStream).getUniqueIdentifier();
                will(returnValue("notSmithers"));
                // end author check skip

                // this will get us past stream owner check
                allowing(activity).getDestinationStream();
                will(returnValue(activityDestinationStream));

                allowing(activityDestinationStream).getType();
                will(returnValue(EntityType.GROUP));

                allowing(activityDestinationStream).getUniqueIdentifier();
                will(returnValue(groupShortName));
                // end stream owner check skip

                oneOf(groupByShortNameDAO).fetchId(groupShortName);
                will(returnValue(groupId));

                oneOf(groupAccessMapper).hasGroupCoordinatorAccessRecursively(userPersonId, groupId);
                will(returnValue(false));

                oneOf(systemAdminIdsMapper).execute(null);
                will(returnValue(systemAdminIds));

                oneOf(activity).setDeletable(false);
            }
        });

        sut.execute(userAcctId, userPersonId, activity);
        context.assertIsSatisfied();
    }
}
