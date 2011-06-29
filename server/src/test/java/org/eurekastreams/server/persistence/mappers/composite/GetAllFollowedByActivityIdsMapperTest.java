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
package org.eurekastreams.server.persistence.mappers.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetAllFollowedByActivityIdsRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetAllFollowedByActivityIdsMapper.
 * 
 */
public class GetAllFollowedByActivityIdsMapperTest
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
     * Mapper to get activity ids for all people a user is following.
     */
    private DomainMapper<Long, List<Long>> getFollowedPeopleActivityIdsMapper = context.mock(DomainMapper.class,
            "getFollowedPeopleActivityIdsMapper");

    /**
     * Mapper to get activity ids for all groups a user is following.
     */
    private DomainMapper<List<Long>, List<List<Long>>> getFollowedGroupActivityIdsMapper = context.mock(
            DomainMapper.class, "getFollowedGroupActivityIdsMapper");

    /**
     * Request object mock.
     */
    private GetAllFollowedByActivityIdsRequest request = context.mock(GetAllFollowedByActivityIdsRequest.class);

    /**
     * user id.
     */
    private Long userId = 5L;

    /**
     * Group stream ids.
     */
    private ArrayList<Long> groupStreamIds = new ArrayList<Long>(Arrays.asList(1L, 2L));

    /**
     * System under test.
     */
    private GetAllFollowedByActivityIdsMapper sut = new GetAllFollowedByActivityIdsMapper(
            getFollowedPeopleActivityIdsMapper, getFollowedGroupActivityIdsMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(request).getUserId();
                will(returnValue(userId));

                oneOf(request).getGroupStreamIds();
                will(returnValue(groupStreamIds));

                oneOf(getFollowedPeopleActivityIdsMapper).execute(userId);
                will(returnValue(new ArrayList<Long>()));

                oneOf(getFollowedGroupActivityIdsMapper).execute(groupStreamIds);
                will(returnValue(new ArrayList<List<Long>>()));
            }
        });

        sut.execute(request);
        context.assertIsSatisfied();
    }
}
