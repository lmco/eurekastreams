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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for FollowerStatusPopulator.
 * 
 */
@SuppressWarnings("unchecked")
public class FollowerStatusPopulatorTest
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
     * Person id followed by Principal mapper.
     */
    private DomainMapper<Long, List<Long>> personIdsFollowedByPrincipalMapper = context.mock(DomainMapper.class,
            "personIdsFollowedByPrincipalMapper");

    /**
     * Group id followed by Principal mapper.
     */
    private DomainMapper<Long, List<Long>> groupIdsFollowedByPrincipalMapper = context.mock(DomainMapper.class,
            "groupIdsFollowedByPrincipalMapper");

    /**
     * System under test.
     */
    private FollowerStatusPopulator<FollowerStatusable> sut = new FollowerStatusPopulator<FollowerStatusable>(
            personIdsFollowedByPrincipalMapper, groupIdsFollowedByPrincipalMapper);

    /**
     * Current user id used for test.
     */
    private Long currentUserId = 5L;

    /**
     * {@link FollowerStatusable}.
     */
    private FollowerStatusable fs1 = context.mock(FollowerStatusable.class, "fs1");

    /**
     * {@link FollowerStatusable}.
     */
    private FollowerStatusable fs2 = context.mock(FollowerStatusable.class, "fs2");

    /**
     * {@link FollowerStatusable}.
     */
    private FollowerStatusable fs3 = context.mock(FollowerStatusable.class, "fs3");

    /**
     * Test.
     */
    @Test
    public void testFollowing()
    {
        final List<Long> idList = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));
        final List<FollowerStatusable> fsList = new ArrayList<FollowerStatusable>(Arrays.asList(fs1, fs2, fs3));
        final Long idInList = 1L;

        context.checking(new Expectations()
        {
            {
                oneOf(fs1).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(personIdsFollowedByPrincipalMapper).execute(currentUserId);
                will(returnValue(idList));

                oneOf(fs1).getEntityId();
                will(returnValue(idInList));

                oneOf(fs1).setFollowerStatus(FollowerStatus.FOLLOWING);

                oneOf(fs2).getEntityType();
                will(returnValue(EntityType.GROUP));

                oneOf(groupIdsFollowedByPrincipalMapper).execute(currentUserId);
                will(returnValue(idList));

                oneOf(fs2).getEntityId();
                will(returnValue(idInList));

                oneOf(fs2).setFollowerStatus(FollowerStatus.FOLLOWING);

                oneOf(fs3).getEntityType();
                will(returnValue(EntityType.NOTSET));

                oneOf(fs3).setFollowerStatus(FollowerStatus.NOTSPECIFIED);

            }
        });

        sut.execute(currentUserId, fsList, null);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNotFollowing()
    {
        final List<Long> idList = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));
        final List<FollowerStatusable> fsList = new ArrayList<FollowerStatusable>(Arrays.asList(fs1, fs2, fs3));
        final Long idNotInList = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(fs1).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(personIdsFollowedByPrincipalMapper).execute(currentUserId);
                will(returnValue(idList));

                oneOf(fs1).getEntityId();
                will(returnValue(idNotInList));

                oneOf(fs1).setFollowerStatus(FollowerStatus.NOTFOLLOWING);

                oneOf(fs2).getEntityType();
                will(returnValue(EntityType.GROUP));

                oneOf(groupIdsFollowedByPrincipalMapper).execute(currentUserId);
                will(returnValue(idList));

                oneOf(fs2).getEntityId();
                will(returnValue(idNotInList));

                oneOf(fs2).setFollowerStatus(FollowerStatus.NOTFOLLOWING);

                oneOf(fs3).getEntityType();
                will(returnValue(EntityType.NOTSET));

                oneOf(fs3).setFollowerStatus(FollowerStatus.DISABLED);

            }
        });

        sut.execute(currentUserId, fsList, FollowerStatus.DISABLED);

        context.assertIsSatisfied();
    }

    /**
     * Test following self doesn't show as such.
     */
    @Test
    public void testFollowingSelfDoesntShowFollowing()
    {
        final List<Long> idList = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L, currentUserId));
        final List<FollowerStatusable> fsList = new ArrayList<FollowerStatusable>(Arrays.asList(fs1));

        context.checking(new Expectations()
        {
            {
                oneOf(fs1).getEntityType();
                will(returnValue(EntityType.PERSON));

                oneOf(personIdsFollowedByPrincipalMapper).execute(currentUserId);
                will(returnValue(idList));

                oneOf(fs1).getEntityId();
                will(returnValue(currentUserId));

                oneOf(fs1).setFollowerStatus(FollowerStatus.NOTFOLLOWING);
            }
        });

        sut.execute(currentUserId, fsList, FollowerStatus.DISABLED);

        context.assertIsSatisfied();
    }
}
