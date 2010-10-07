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
package org.eurekastreams.server.action.execution.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.DomainGroupCacheUpdaterRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddPrivateGroupIdToCachedCoordinatorAccessList;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.SaveDomainGroupCoordinatorsListToCache;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * This class is responsible for testing the functionality of the DomainGroupCacheUpdaterAsyncAction.
 * 
 */
public class DomainGroupCacheUpdaterAsyncExecutionTest
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
     * Mocked instance of the {@link SaveDomainGroupCoordinatorsListToCache} mapper.
     */
    private SaveDomainGroupCoordinatorsListToCache groupCoordinatorCacheMock = context
            .mock(SaveDomainGroupCoordinatorsListToCache.class);

    /**
     * Mocked instance of the {@link DomainGroupMapper}.
     */
    private DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);

    /**
     * Mocked instance of the {@link AddPrivateGroupIdToCachedCoordinatorAccessList} cache mapper.
     */
    private AddPrivateGroupIdToCachedCoordinatorAccessList privateGroupIdCachedCoordAccessListMock = context
            .mock(AddPrivateGroupIdToCachedCoordinatorAccessList.class);

    /**
     * Mocked instance of the cache client.
     */
    private Cache cacheMock = context.mock(Cache.class);

    /**
     * Mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked user details.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * Mocked domain group.
     */
    private DomainGroup groupMock = context.mock(DomainGroup.class);

    /**
     * System under test.
     */
    private DomainGroupCacheUpdaterAsyncExecution sut;

    /**
     * Mocked domain group.
     */
    private DomainGroupCacheUpdaterRequest request = context.mock(DomainGroupCacheUpdaterRequest.class);

    /**
     * Mocked authored by mapper.
     */
    private GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity getActivityIdsAuthordedByEntityDbMapper = context
            .mock(GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity.class);

    /**
     * group id.
     */
    private final Long groupId = 37271L;

    /**
     * group name.
     */
    private final String groupName = "groupname";

    /**
     * Activity IDs for the group.
     */
    private List<Long> activityIds = Arrays.asList(5L, 6L, 7L);
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new DomainGroupCacheUpdaterAsyncExecution(getActivityIdsAuthordedByEntityDbMapper,
                groupCoordinatorCacheMock, groupMapper, privateGroupIdCachedCoordAccessListMock, cacheMock);
    }

    /**
     * Test the update execution of perform action with a private group.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformActionWithPrivateGroupUpdate() throws Exception
    {
        final Long coord1 = 3827L;
        final Long coord2 = 3828L;
        final Long coord3 = 3829L;

        final List<Long> coordIds = new ArrayList<Long>(3);
        coordIds.add(coord1);
        coordIds.add(coord2);
        coordIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getDomainGroupId();
                will(returnValue(groupId));

                oneOf(groupMapper).findById(groupId);
                will(returnValue(groupMock));

                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupCoordinatorCacheMock).execute(groupMock);
                will(returnValue(coordIds));

                oneOf(request).getIsUpdate();
                will(returnValue(true));
                
                allowing(groupMock).getShortName();
                will(returnValue(groupName));

                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(groupName, EntityType.GROUP);
                will(returnValue(activityIds));
                
                exactly(activityIds.size()).of(cacheMock).delete(with(any(String.class)));
                
                oneOf(cacheMock).addToSet(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord1,
                        groupId);
                oneOf(cacheMock).addToSet(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord2,
                        groupId);
                oneOf(cacheMock).addToSet(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord3,
                        groupId);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the create execution of perform action with a private group the is not pending. If pending the cache update
     * will be skipped. The update should happen when group is approved.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformActionWithPrivateGroupCreateNotPending() throws Exception
    {
        final Long coord1 = 3827L;
        final Long coord2 = 3828L;
        final Long coord3 = 3829L;

        final List<Long> coordIds = new ArrayList<Long>(3);
        coordIds.add(coord1);
        coordIds.add(coord2);
        coordIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getDomainGroupId();
                will(returnValue(groupId));

                oneOf(groupMapper).findById(groupId);
                will(returnValue(groupMock));

                oneOf(groupCoordinatorCacheMock).execute(groupMock);
                will(returnValue(coordIds));

                oneOf(request).getIsUpdate();
                will(returnValue(false));

                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));
                
                allowing(groupMock).getShortName();
                will(returnValue(groupName));

                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(groupName, EntityType.GROUP);
                will(returnValue(activityIds));
                
                exactly(activityIds.size()).of(cacheMock).delete(with(any(String.class)));
                
                oneOf(groupMock).isPending();
                will(returnValue(false));

                oneOf(privateGroupIdCachedCoordAccessListMock).execute(groupId);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the create execution of perform action with a private group that is pending. If pending the cache update
     * will be skipped. The update should happen when group is approved.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformActionWithPrivateGroupCreatePending() throws Exception
    {
        final Long coord1 = 3827L;
        final Long coord2 = 3828L;
        final Long coord3 = 3829L;

        final List<Long> coordIds = new ArrayList<Long>(3);
        coordIds.add(coord1);
        coordIds.add(coord2);
        coordIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getDomainGroupId();
                will(returnValue(groupId));

                oneOf(groupMapper).findById(groupId);
                will(returnValue(groupMock));

                oneOf(groupCoordinatorCacheMock).execute(groupMock);
                will(returnValue(coordIds));

                oneOf(request).getIsUpdate();
                will(returnValue(false));
               
                allowing(groupMock).getShortName();
                will(returnValue(groupName));

                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(groupName, EntityType.GROUP);
                will(returnValue(activityIds));
                
                exactly(activityIds.size()).of(cacheMock).delete(with(any(String.class)));
                
                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupMock).isPending();
                will(returnValue(true));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the execution of perform action with a public group.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformActionWithPublicGroup() throws Exception
    {
        final Long coord1 = 3827L;
        final Long coord2 = 3828L;
        final Long coord3 = 3829L;

        final List<Long> coordIds = new ArrayList<Long>(3);
        coordIds.add(coord1);
        coordIds.add(coord2);
        coordIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getDomainGroupId();
                will(returnValue(groupId));

                oneOf(groupMapper).findById(groupId);
                will(returnValue(groupMock));
                
                allowing(groupMock).getShortName();
                will(returnValue(groupName));

                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(groupName, EntityType.GROUP);
                will(returnValue(activityIds));
                
                exactly(activityIds.size()).of(cacheMock).delete(with(any(String.class)));
                oneOf(groupCoordinatorCacheMock).execute(groupMock);
                will(returnValue(coordIds));

                oneOf(groupMock).isPublicGroup();
                will(returnValue(true));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
