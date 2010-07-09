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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetCompositeStreamIdsByAssociatedActivity} class.
 *
 */
public class GetCompositeStreamIdsByAssociatedActivityTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetCompositeStreamIdsByAssociatedActivity sut;

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
     * Mocked instance of the cache object.
     */
    private final Cache cacheMock = context.mock(Cache.class);

    /**
     * Mapper to get composite streams.
     */
    private BulkCompositeStreamsMapper bulkCompositeStreamsMapperMock = context.mock(BulkCompositeStreamsMapper.class);

    /**
     * Mapper to get followers of a person.
     */
    private GetFollowerIds personFollowersMapperMock = context.mock(GetFollowerIds.class);

    /**
     * Mapper to get followers of a group.
     */
    private GetGroupFollowerIds groupFollowersMapperMock = context.mock(GetGroupFollowerIds.class);

    /**
     * Mapper to get people by account ids.
     */
    private GetPeopleByAccountIds bulkPeopleByAccountIdMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Mapper to get hierarchical parent org ids.
     */
    private final GetRecursiveParentOrgIds parentOrgIdsMapperMock = context.mock(GetRecursiveParentOrgIds.class);

    /**
     * Mapper to get organizations by ids.
     */
    private final GetOrganizationsByIds orgsMapperMock = context.mock(GetOrganizationsByIds.class);

    /**
     * Organization by id DAO.
     */
    private GetOrganizationsByShortNames organizationsByShortNameDAOMock = context
            .mock(GetOrganizationsByShortNames.class);

    /**
     * Mapper to get groups by short name.
     */
    private GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapperMock = context
            .mock(GetDomainGroupsByShortNames.class);

    /**
     * TEst group destination stream id.
     */
    private static final Long TEST_GROUP_DESTINATION_STREAM = 878L;

    /**
     * Test person id for fordp.
     */
    private static final Long TEST_PERSON_ID = 42L;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new GetCompositeStreamIdsByAssociatedActivity(personFollowersMapperMock, groupFollowersMapperMock,
                bulkCompositeStreamsMapperMock, bulkPeopleByAccountIdMapperMock, bulkDomainGroupsByShortNameMapperMock,
                parentOrgIdsMapperMock, orgsMapperMock, organizationsByShortNameDAOMock);
        sut.setCache(cacheMock);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test the successful execution with an activity destined for a person's stream.
     */
    @Test
    public void testGetCompositeStreamsForDestinationPerson()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(1L);
        testDestinationStream.setUniqueIdentifier("testaccount");
        testDestinationStream.setType(EntityType.PERSON);

        testActivity.setDestinationStream(testDestinationStream);

        PersonModelView testPersonModelView = new PersonModelView();
        testPersonModelView.setParentOrganizationShortName("orgShortName");

        final List<PersonModelView> testPersonModelViewList = new ArrayList<PersonModelView>();
        testPersonModelViewList.add(testPersonModelView);

        OrganizationModelView testParentOrganizationModelView = new OrganizationModelView();
        testParentOrganizationModelView.setEntityId(1L);

        final List<OrganizationModelView> testParentOrganizationsModelViewList = new ArrayList<OrganizationModelView>();
        testParentOrganizationsModelViewList.add(testParentOrganizationModelView);

        final List<Long> testParentOrgIds = new ArrayList<Long>();
        testParentOrgIds.add(1L);

        OrganizationModelView testParentOrg = new OrganizationModelView();
        testParentOrg.setCompositeStreamId(2L);
        final List<OrganizationModelView> testParentOrgs = new ArrayList<OrganizationModelView>();
        testParentOrgs.add(testParentOrg);

        final List<StreamFilter> compositeStreamViewsForParentOrgHierarchy = new ArrayList<StreamFilter>();
        compositeStreamViewsForParentOrgHierarchy.add(new StreamView());

        context.checking(new Expectations()
        {
            {
                oneOf(bulkPeopleByAccountIdMapperMock).execute(with(any(List.class)));
                will(returnValue(testPersonModelViewList));

                oneOf(organizationsByShortNameDAOMock).execute(with(any(List.class)));
                will(returnValue(testParentOrganizationsModelViewList));

                oneOf(parentOrgIdsMapperMock).execute(with(any(Long.class)));
                will(returnValue(testParentOrgIds));

                oneOf(orgsMapperMock).execute(testParentOrgIds);
                will(returnValue(testParentOrgs));

                oneOf(bulkCompositeStreamsMapperMock).execute(with(any(List.class)));
                will(returnValue(compositeStreamViewsForParentOrgHierarchy));
            }
        });

        List<StreamView> results = sut.getCompositeStreams(testActivity);
        Assert.assertEquals(2, results.size());
        context.assertIsSatisfied();
    }

    /**
     * Test the successful execution with an activity destined for a group's stream.
     */
    @Test
    public void testGetCompositeStreamsForDestinationDomainGroup()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(TEST_GROUP_DESTINATION_STREAM);
        testDestinationStream.setUniqueIdentifier("testgroup");
        testDestinationStream.setType(EntityType.GROUP);

        testActivity.setDestinationStream(testDestinationStream);

        DomainGroupModelView testGroupModelView = new DomainGroupModelView();
        testGroupModelView.setParentOrganizationShortName("orgShortName");

        final List<DomainGroupModelView> testDomainGroupModelViewList = new ArrayList<DomainGroupModelView>();
        testDomainGroupModelViewList.add(testGroupModelView);

        OrganizationModelView testParentOrganizationModelView = new OrganizationModelView();
        testParentOrganizationModelView.setEntityId(1L);

        final List<OrganizationModelView> testParentOrganizationsModelViewList = new ArrayList<OrganizationModelView>();
        testParentOrganizationsModelViewList.add(testParentOrganizationModelView);

        final List<Long> testParentOrgIds = new ArrayList<Long>();
        testParentOrgIds.add(1L);

        OrganizationModelView testParentOrg = new OrganizationModelView();
        testParentOrg.setCompositeStreamId(2L);
        final List<OrganizationModelView> testParentOrgs = new ArrayList<OrganizationModelView>();
        testParentOrgs.add(testParentOrg);

        final List<StreamFilter> compositeStreamViewsForParentOrgHierarchy = new ArrayList<StreamFilter>();
        compositeStreamViewsForParentOrgHierarchy.add(new StreamView());

        context.checking(new Expectations()
        {
            {
                oneOf(bulkDomainGroupsByShortNameMapperMock).execute(with(any(List.class)));
                will(returnValue(testDomainGroupModelViewList));

                oneOf(organizationsByShortNameDAOMock).execute(with(any(List.class)));
                will(returnValue(testParentOrganizationsModelViewList));

                oneOf(parentOrgIdsMapperMock).execute(with(any(Long.class)));
                will(returnValue(testParentOrgIds));

                oneOf(orgsMapperMock).execute(testParentOrgIds);
                will(returnValue(testParentOrgs));

                oneOf(bulkCompositeStreamsMapperMock).execute(with(any(List.class)));
                will(returnValue(compositeStreamViewsForParentOrgHierarchy));
            }
        });

        List<StreamView> results = sut.getCompositeStreams(testActivity);
        Assert.assertEquals(2, results.size());
        context.assertIsSatisfied();
    }

    /**
     * Test the scenario when an unsupported destination stream type is supplied.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCompositeStreamsForUnsupportedDestination()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(TEST_GROUP_DESTINATION_STREAM);
        testDestinationStream.setType(EntityType.NOTSET);
        testActivity.setDestinationStream(testDestinationStream);

        sut.getCompositeStreams(testActivity);
    }

    /**
     * Test retrieving followers with entity type of Person for the Destination Stream.
     */
    @Test
    public void testGetFollowersWithPersonDestinationStream()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(1L);
        testDestinationStream.setUniqueIdentifier("fordp");
        testDestinationStream.setType(EntityType.PERSON);
        testActivity.setDestinationStream(testDestinationStream);

        PersonModelView testPersonModelView = new PersonModelView();
        testPersonModelView.setParentOrganizationShortName("orgShortName");
        testPersonModelView.setEntityId(TEST_PERSON_ID);

        final List<PersonModelView> testPersonModelViewList = new ArrayList<PersonModelView>();
        testPersonModelViewList.add(testPersonModelView);

        final List<Long> followerIds = new ArrayList<Long>();
        followerIds.add(TEST_PERSON_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(bulkPeopleByAccountIdMapperMock).execute(with(any(List.class)));
                will(returnValue(testPersonModelViewList));

                oneOf(personFollowersMapperMock).execute(with(any(Long.class)));
                will(returnValue(followerIds));
            }
        });

        List<Long> results = sut.getFollowers(testActivity);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(TEST_PERSON_ID, results.get(0));
        context.assertIsSatisfied();
    }

    /**
     * Test retrieving followers with entity type of Group for the Destination Stream.
     */
    @Test
    public void testGetFollowersWithGroupDestinationStream()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(1L);
        testDestinationStream.setUniqueIdentifier("group1");
        testDestinationStream.setType(EntityType.GROUP);
        testActivity.setDestinationStream(testDestinationStream);

        DomainGroupModelView testGroupModelView = new DomainGroupModelView();
        testGroupModelView.setParentOrganizationShortName("orgShortName");
        testGroupModelView.setEntityId(5L);

        final List<DomainGroupModelView> testGroupModelViewList = new ArrayList<DomainGroupModelView>();
        testGroupModelViewList.add(testGroupModelView);

        final List<Long> followerIds = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(bulkDomainGroupsByShortNameMapperMock).execute(with(any(List.class)));
                will(returnValue(testGroupModelViewList));

                oneOf(groupFollowersMapperMock).execute(with(5L));
                will(returnValue(followerIds));
            }
        });

        List<Long> results = sut.getFollowers(testActivity);
        Assert.assertEquals(0, results.size());
        context.assertIsSatisfied();
    }

    /**
     * Test retrieving followers with an unsupported entity type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFollowersUnsupportedDestinationStream()
    {
        ActivityDTO testActivity = new ActivityDTO();
        testActivity.setId(1L);

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setId(1L);
        testDestinationStream.setUniqueIdentifier("group1");
        testDestinationStream.setType(EntityType.NOTSET);
        testActivity.setDestinationStream(testDestinationStream);

        sut.getFollowers(testActivity);
    }
}
