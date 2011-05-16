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

package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test comment Filter.
 */
public class CanCommentFilterTest
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
     * System under test.
     */
    private CanCommentFilter sut;

    /**
     * PersonMapperMock.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper = context
            .mock(DomainMapper.class);

    /**
     * GroupMapper Mock.
     */
    private GetDomainGroupsByShortNames gMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get all Coordinators of a Group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinators = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * List of activities.
     */
    private List<ActivityDTO> activities;

    /**
     * Activity Mock.
     */
    private ActivityDTO activityMock = context.mock(ActivityDTO.class);

    /**
     * Stream Mock.
     */
    private StreamEntityDTO streamMock = context.mock(StreamEntityDTO.class);

    /**
     * PersonModelView Mock.
     */
    private PersonModelView destinationPersonMock = context.mock(PersonModelView.class, "activity destination person");

    /**
     * Domain group model view mock..
     */
    private DomainGroupModelView groupMock = context.mock(DomainGroupModelView.class);

    /**
     * Current user account.
     */
    private PersonModelView currentUserAccount = new PersonModelView();

    /**
     * Person destination account id.
     */
    private String destinationPersonAccountId = "destinationpersonaccountid";

    /**
     * Group destination short name.
     */
    private String destinationGroupShortName = "destinationgroupshortname";

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        activities = new ArrayList<ActivityDTO>(1);
        activities.add(activityMock);
        currentUserAccount.setAccountId("personAccount");

        sut = new CanCommentFilter(getPersonModelViewsByAccountIdsMapper, gMapperMock, groupCoordinators);
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterEmptyList()
    {
        final List<ActivityDTO> empty = new ArrayList<ActivityDTO>();

        sut.filter(empty, currentUserAccount);
    }

    /**
     * Test filter method.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterBadType()
    {
        final List<ActivityDTO> empty = new ArrayList<ActivityDTO>();

        final List<PersonModelView> personModelViews = new ArrayList<PersonModelView>(1);
        personModelViews.add(destinationPersonMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.APPLICATION));
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterPersonStreamCanComment()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(destinationPersonAccountId);

        final List<PersonModelView> personModelViews = new ArrayList<PersonModelView>(1);
        personModelViews.add(destinationPersonMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.PERSON));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(destinationPersonAccountId));

                oneOf(getPersonModelViewsByAccountIdsMapper).execute(destinationIds);
                will(returnValue(personModelViews));

                oneOf(destinationPersonMock).isCommentable();
                will(returnValue(true));

                allowing(destinationPersonMock).getAccountId();
                will(returnValue(destinationPersonAccountId));

                oneOf(activityMock).setCommentable(true);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterPersonStreamCanNotComment()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(destinationPersonAccountId);

        final List<PersonModelView> personModelViews = new ArrayList<PersonModelView>(1);
        personModelViews.add(destinationPersonMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.PERSON));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(destinationPersonAccountId));

                oneOf(getPersonModelViewsByAccountIdsMapper).execute(destinationIds);
                will(returnValue(personModelViews));

                oneOf(destinationPersonMock).isCommentable();
                will(returnValue(false));

                allowing(destinationPersonMock).getAccountId();
                will(returnValue(destinationPersonAccountId));

                oneOf(activityMock).setCommentable(false);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterPersonStreamCanNotCommentIsDestinationUser()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(currentUserAccount.getAccountId());

        final List<PersonModelView> personModelViews = new ArrayList<PersonModelView>(1);
        personModelViews.add(destinationPersonMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.PERSON));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(currentUserAccount.getAccountId()));

                oneOf(getPersonModelViewsByAccountIdsMapper).execute(with(any(List.class)));
                will(returnValue(personModelViews));

                oneOf(destinationPersonMock).isCommentable();
                will(returnValue(false));

                allowing(destinationPersonMock).getAccountId();
                will(returnValue(destinationPersonAccountId));

                oneOf(activityMock).setCommentable(true);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterGroupStreamCanComment()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(destinationGroupShortName);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.GROUP));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(destinationGroupShortName));

                oneOf(gMapperMock).execute(destinationIds);
                will(returnValue(domainGroupModelViews));

                oneOf(groupMock).getShortName();
                will(returnValue(destinationGroupShortName));

                oneOf(groupMock).isCommentable();
                will(returnValue(true));

                oneOf(activityMock).setCommentable(true);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterGroupStreamCanNotCommentNotCooridnator()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(destinationGroupShortName);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.GROUP));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(destinationGroupShortName));

                oneOf(gMapperMock).execute(destinationIds);
                will(returnValue(domainGroupModelViews));

                allowing(groupMock).getShortName();
                will(returnValue(destinationGroupShortName));

                oneOf(groupMock).isCommentable();
                will(returnValue(false));

                oneOf(groupMock).getEntityId();
                will(returnValue(1L));

                oneOf(groupCoordinators).hasGroupCoordinatorAccessRecursively(currentUserAccount.getAccountId(), 1L);
                will(returnValue(false));

                oneOf(activityMock).setCommentable(false);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public final void testFilterGroupStreamCanNotCommentIsCooridnator()
    {
        final List<String> destinationIds = new ArrayList<String>(1);
        destinationIds.add(destinationGroupShortName);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(streamMock));

                allowing(streamMock).getType();
                will(returnValue(EntityType.GROUP));

                allowing(streamMock).getUniqueIdentifier();
                will(returnValue(destinationGroupShortName));

                oneOf(gMapperMock).execute(destinationIds);
                will(returnValue(domainGroupModelViews));

                allowing(groupMock).getShortName();
                will(returnValue(destinationGroupShortName));

                oneOf(groupMock).isCommentable();
                will(returnValue(false));

                oneOf(groupMock).getEntityId();
                will(returnValue(1L));

                oneOf(groupCoordinators).hasGroupCoordinatorAccessRecursively(currentUserAccount.getAccountId(), 1L);
                will(returnValue(true));

                oneOf(activityMock).setCommentable(true);
            }
        });

        sut.filter(activities, currentUserAccount);

        context.assertIsSatisfied();
    }
}
