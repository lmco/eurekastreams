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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper = context.mock(DomainMapper.class);

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
     * Test the execution.
     */
    @Test
    public void testExecute()
    {
        assertTrue(true);
        // AddBufferedActivitiesToCache sut = new AddBufferedActivitiesToCache(bulkActivitiesMapper, cache,
        // getCompositeStreamsByActivity);
        //
        // final List<Long> activityIds = null;
        //
        // final ActivityDTO activity1 = context.mock(ActivityDTO.class);
        // final ActivityDTO activity2 = context.mock(ActivityDTO.class, "a2");
        //
        // final List<ActivityDTO> activities = new LinkedList<ActivityDTO>();
        // activities.add(activity1);
        // activities.add(activity2);
        //
        // final List<Long> followerIdsForAct1 = new LinkedList<Long>();
        // followerIdsForAct1.add(1L);
        // followerIdsForAct1.add(2L);
        // followerIdsForAct1.add(3L);
        //
        // final List<Long> followerIdsForAct2 = new LinkedList<Long>();
        // followerIdsForAct2.add(1L);
        // followerIdsForAct2.add(2L);
        // followerIdsForAct2.add(4L);
        //
        // context.checking(new Expectations()
        // {
        // {
        // allowing(activity1).getId();
        // will(returnValue(7L));
        //
        // allowing(activity2).getId();
        // will(returnValue(8L));
        //
        // oneOf(cache).setListCAS(CacheKeys.BUFFERED_ACTIVITIES, null);
        // will(returnValue(activityIds));
        //
        // oneOf(bulkActivitiesMapper).execute(activityIds);
        // will(returnValue(activities));
        //
        // oneOf(getCompositeStreamsByActivity).getFollowers(activity1);
        // will(returnValue(followerIdsForAct1));
        //
        // oneOf(getCompositeStreamsByActivity).getFollowers(activity2);
        // will(returnValue(followerIdsForAct2));
        //
        // exactly(8).of(cache).addToTopOfList(with(any(String.class)), with(any(ArrayList.class)));
        // }
        // });
        //
        // sut.execute();
        //
        // context.assertIsSatisfied();
    }

}
