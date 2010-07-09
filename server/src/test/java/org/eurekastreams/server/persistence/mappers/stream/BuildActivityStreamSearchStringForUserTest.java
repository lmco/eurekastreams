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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for BuildActivityStreamSearchStringForUser.
 */
public class BuildActivityStreamSearchStringForUserTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get a list of all group ids that aren't public that a user can
     * see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getGroupIdsMapper = context
            .mock(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);

    /**
     * System under test.
     */
    private BuildActivityStreamSearchStringForUser sut = new BuildActivityStreamSearchStringForUser(
            getGroupIdsMapper);

    /**
     * Person id to use for tests.
     */
    final Long personId = 38928L;

    /**
     * Test execute with no groups.
     */
    @Test
    public void testExecuteNoGroups()
    {
        final Set<Long> groupIds = new HashSet<Long>();

        context.checking(new Expectations()
        {
            {
                // no groups
                oneOf(getGroupIdsMapper).execute(personId);
                will(returnValue(groupIds));
            }
        });

        String query = sut.execute(personId);
        assertEquals("isPublic:t", query);

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteWithAGroup()
    {
        final Long groupId = 827837L;
        final Set<Long> groupIds = new HashSet<Long>();

        // add a group
        groupIds.add(groupId);

        context.checking(new Expectations()
        {
            {
                // no groups
                oneOf(getGroupIdsMapper).execute(personId);
                will(returnValue(groupIds));
            }
        });

        String query = sut.execute(personId);
        assertEquals("isPublic:t recipient:( g" + groupId + ")", query);

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteWithGroups()
    {
        final Long groupId1 = 1111L;
        final Long groupId2 = 2222L;
        final Set<Long> groupIds = new HashSet<Long>();

        // add some groups
        groupIds.add(groupId1);
        groupIds.add(groupId2);

        context.checking(new Expectations()
        {
            {
                // no groups
                oneOf(getGroupIdsMapper).execute(personId);
                will(returnValue(groupIds));
            }
        });

        String query = sut.execute(personId);

        // since we're iterating over a set, check all the possibilities
        final String output1 = "isPublic:t recipient:( g" + groupId1 + " g"
                + groupId2 + ")";
        final String output2 = "isPublic:t recipient:( g" + groupId2 + " g"
                + groupId1 + ")";
        assertTrue(query.equals(output1) || query.equals(output2));

        context.assertIsSatisfied();
    }
}
