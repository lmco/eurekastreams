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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetDomainGroupModelViewByShortNameExectution.
 *
 */
@SuppressWarnings("unchecked")
public class GetDomainGroupModelViewByShortNameExecutionTest
{
    /** Test data. */
    private static final long USER_ID = 999L;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper used to look up the group.
     */
    private final GetDomainGroupsByShortNames groupByShortNameMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get all person ids that have group coordinator access for a given group.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorIdsDAO = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mapper to get followers for a group.
     */
    private final DomainMapper<Long, List<Long>> groupFollowerIdsMapper = context.mock(DomainMapper.class);

    /**
     * DomainGroupModelView.
     */
    private final DomainGroupModelView dgmv = context.mock(DomainGroupModelView.class);

    /**
     * Get ids for direct group coordinators.
     */
    private final DomainMapper<Long, List<Long>> groupCoordinatorIdsByGroupIdMapper = context.mock(DomainMapper.class,
            "groupCoordinatorIdsByGroupIdMapper");

    /**
     * Get PersonModelViews by id.
     */
    private final DomainMapper<List<Long>, List<PersonModelView>> personModelViewsByIdMapper = context.mock(
            DomainMapper.class, "personModelViewsByIdMapper");

    /**
     * Mapper for getting group entity.
     */
    private final DomainMapper<FindByIdRequest, DomainGroup> groupEntityMapper = context.mock(DomainMapper.class,
            "groupEntityMapper");

    /** Fixture: Mapper for getting an activity. */
    private final DomainMapper<Long, ActivityDTO> activityMapper = context.mock(DomainMapper.class, "activityMapper");

    /**
     * DomainGroup entity mock.
     */
    private final DomainGroup dg = context.mock(DomainGroup.class);

    /**
     * Group short name.
     */
    private final String shortname = "shortName";

    /**
     * Group Id.
     */
    private final Long groupId = 5L;

    /**
     * System under test.
     */
    private GetDomainGroupModelViewByShortNameExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetDomainGroupModelViewByShortNameExecution(groupByShortNameMapper, groupCoordinatorIdsDAO,
                groupFollowerIdsMapper, groupCoordinatorIdsByGroupIdMapper, personModelViewsByIdMapper,
                groupEntityMapper, activityMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(groupByShortNameMapper).fetchUniqueResult(shortname);
                will(returnValue(dgmv));
            }
        });
    }

    // TODO: Minimal pass testing. Beef this up a bit for non public and restricted groups.

    /**
     * Expectations for when the group is found.
     */
    private void groupFoundExpectations()
    {
        final List<Long> coordIds = Collections.singletonList(1L);
        final List<PersonModelView> coords = Collections.singletonList(new PersonModelView());

        context.checking(new Expectations()
        {
            {
                allowing(dgmv).getId();
                will(returnValue(groupId));

                oneOf(dgmv).setBannerEntityId(groupId);

                oneOf(groupCoordinatorIdsByGroupIdMapper).execute(groupId);
                will(returnValue(coordIds));

                oneOf(personModelViewsByIdMapper).execute(coordIds);
                will(returnValue(coords));

                oneOf(dgmv).setCoordinators(coords);

                oneOf(groupEntityMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(dg));

                oneOf(dg).getCapabilities();
                will(returnValue(new ArrayList<BackgroundItem>()));

                oneOf(dgmv).setCapabilities(new ArrayList<String>());
            }
        });
    }

    /**
     * Test.
     */
    @Test
    public void testPublicNoStickyActivity()
    {
        groupFoundExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(dgmv).getStickyActivityId();
                will(returnValue(null));

                oneOf(dgmv).isPublic();
                will(returnValue(true));

                oneOf(dgmv).setRestricted(false);
            }
        });

        PrincipalActionContext pac = TestContextCreator.createPrincipalActionContext(shortname, null, USER_ID);

        sut.execute(pac);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testPublicStickyActivity()
    {
        final Long activityId = 888L;
        final ActivityDTO activity = context.mock(ActivityDTO.class);
        groupFoundExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(dgmv).getStickyActivityId();
                will(returnValue(activityId));

                oneOf(dgmv).isPublic();
                will(returnValue(true));

                oneOf(dgmv).setRestricted(false);

                oneOf(activityMapper).execute(activityId);
                will(returnValue(activity));

                oneOf(dgmv).setStickyActivity(activity);
            }
        });

        PrincipalActionContext pac = TestContextCreator.createPrincipalActionContext(shortname, null, USER_ID);

        sut.execute(pac);
        context.assertIsSatisfied();
    }
}
