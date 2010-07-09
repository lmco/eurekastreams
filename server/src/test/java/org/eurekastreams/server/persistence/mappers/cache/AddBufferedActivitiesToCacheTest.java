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
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Add buffered activities to the cache.
 *
 */
public class AddBufferedActivitiesToCacheTest
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
     * Bulk activities mapper mock.
     */
    private BulkActivitiesMapper bulkActivitiesMapper = context.mock(BulkActivitiesMapper.class);
    /**
     * Cache mock.
     */
    private MemcachedCache cache = context.mock(MemcachedCache.class);
    /**
     * Get composite stream ids by activity mock.
     */
    private GetCompositeStreamIdsByAssociatedActivity getCompositeStreamsByActivity = context
            .mock(GetCompositeStreamIdsByAssociatedActivity.class);

    /**
     * Mocked instance of the {@link GetCoreStreamViewIdCacheMapper}.
     */
    private GetCoreStreamViewIdCacheMapper getCoreStreamViewIdCacheMapperMock = context
            .mock(GetCoreStreamViewIdCacheMapper.class);

    /**
     * Test the execution.
     */
    @Test
    public void testExecute()
    {
        AddBufferedActivitiesToCache sut = new AddBufferedActivitiesToCache(bulkActivitiesMapper, cache,
                getCompositeStreamsByActivity, getCoreStreamViewIdCacheMapperMock);

        final Long everyoneList = 3L;

        final List<Long> activityIds = null;

        final ActivityDTO activity1 = context.mock(ActivityDTO.class);
        final ActivityDTO activity2 = context.mock(ActivityDTO.class, "a2");

        final List<ActivityDTO> activities = new LinkedList<ActivityDTO>();
        activities.add(activity1);
        activities.add(activity2);

        final StreamView view1 = context.mock(StreamView.class, "view1");
        final StreamView view2 = context.mock(StreamView.class, "view2");
        final StreamView view3 = context.mock(StreamView.class, "view3");
        final StreamView view4 = context.mock(StreamView.class, "view4");

        final List<StreamView> streamViewsForAct1 = new LinkedList<StreamView>();
        streamViewsForAct1.add(view1);
        streamViewsForAct1.add(view2);
        streamViewsForAct1.add(view3);

        final List<StreamView> streamViewsForAct2 = new LinkedList<StreamView>();
        streamViewsForAct2.add(view1);
        streamViewsForAct2.add(view2);
        streamViewsForAct2.add(view4);

        final List<Long> followerIdsForAct1 = new LinkedList<Long>();
        followerIdsForAct1.add(1L);
        followerIdsForAct1.add(2L);
        followerIdsForAct1.add(3L);

        final List<Long> followerIdsForAct2 = new LinkedList<Long>();
        followerIdsForAct2.add(1L);
        followerIdsForAct2.add(2L);
        followerIdsForAct2.add(4L);

        context.checking(new Expectations()
        {
            {
                allowing(activity1).getId();
                will(returnValue(7L));

                allowing(activity2).getId();
                will(returnValue(8L));

                allowing(view1).getId();
                will(returnValue(1L));

                allowing(view2).getId();
                will(returnValue(2L));

                allowing(view3).getId();
                will(returnValue(3L));

                allowing(view4).getId();
                will(returnValue(4L));

                oneOf(cache).setListCAS(CacheKeys.BUFFERED_ACTIVITIES, null);
                will(returnValue(activityIds));

                oneOf(bulkActivitiesMapper).execute(activityIds, null);
                will(returnValue(activities));

                oneOf(getCoreStreamViewIdCacheMapperMock).execute(Type.EVERYONE);
                will(returnValue(everyoneList));

                oneOf(getCompositeStreamsByActivity).getCompositeStreams(activity1);
                will(returnValue(streamViewsForAct1));
                oneOf(getCompositeStreamsByActivity).getFollowers(activity1);
                will(returnValue(followerIdsForAct1));

                oneOf(getCompositeStreamsByActivity).getCompositeStreams(activity2);
                will(returnValue(streamViewsForAct2));
                oneOf(getCompositeStreamsByActivity).getFollowers(activity2);
                will(returnValue(followerIdsForAct2));

                exactly(8).of(cache).addToTopOfList(with(any(String.class)), with(any(ArrayList.class)));
            }
        });

        sut.execute();

        context.assertIsSatisfied();
    }

}
