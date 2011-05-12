/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.db.GetAllPrivateGroupIdsDbMapper;
import org.eurekastreams.server.persistence.mappers.db.GetPrivateGroupIdsUnderOrganizations;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class tests the GetPrivateGroupsByUserId mapper.
 * 
 */
public class GetPrivateGroupsByUserIdTest extends MapperTest
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
     * Mapper to retrieve the private group ids that the supplied user is a follower or coordinator for.
     */
    @Autowired
    private GetPrivateGroupIdsCoordinatedByPerson privateGroupIdsMapper;

    /**
     * Mapper to get all private groups.
     */
    private final GetAllPrivateGroupIdsDbMapper getAllPrivateGroupIdsMapper = new GetAllPrivateGroupIdsDbMapper();

    /**
     * Mapper to retrieve the private group ids under an org.
     */
    @Autowired
    private GetPrivateGroupIdsUnderOrganizations orgPrivateGroupIdsMapper;

    /**
     * Test user id that is a coordinator of the test group.
     */
    private static final Long TEST_GROUP_COORDINATOR_USER_ID = 98L;

    /**
     * Test user id that is a coordinator of the parent org of a private group.
     */
    private static final Long TEST_PARENT_ORG_COORDINATOR_USER_ID = 99L;

    /**
     * Test user id that is a coordinator of the parent org of a private group.
     */
    private static final Long TEST_ORG_COORDINATOR_USER_ID = 42L;

    /**
     * Test private group id.
     */
    private static final Long TEST_PRIVATE_GROUP_ID = 1L;

    /**
     * Mapper to get the admin ids.
     */
    private DomainMapper<Serializable, List<Long>> adminIdsMapper = context.mock(DomainMapper.class, "AdminIdsMapper");

    /**
     * System under test.
     */
    private GetPrivateGroupsByUserId sut;

    /**
     * Method to prep the test suite. Need to update an existing group to make it private here before the test begins.
     */
    @Before
    public void setup()
    {
        getAllPrivateGroupIdsMapper.setEntityManager(getEntityManager());
        sut = new GetPrivateGroupsByUserId(privateGroupIdsMapper, getAllPrivateGroupIdsMapper, adminIdsMapper);
        sut.setEntityManager(getEntityManager());

        Query updateGroupToPrivate = getEntityManager().createQuery(
                "UPDATE DomainGroup g SET publicgroup = false WHERE g.id =:groupId").setParameter("groupId",
                TEST_PRIVATE_GROUP_ID);
        updateGroupToPrivate.executeUpdate();
    }

    /**
     * Method to test the execution when I have a coordinator of a private group.
     */
    @Test
    public void testExecuteWithDirectGroupCoordinator()
    {
        final List<Long> adminIds = new ArrayList<Long>();
        context.checking(new Expectations()
        {
            {
                oneOf(adminIdsMapper).execute(null);
                will(returnValue(adminIds));
            }
        });

        Set<Long> results = sut.execute(TEST_GROUP_COORDINATOR_USER_ID);

        assertNotNull(results);
        assertTrue(results.contains(TEST_PRIVATE_GROUP_ID));
    }

    /**
     * Method to test the execution when I have a system admin.
     */
    @Test
    public void testExecuteWithSystemAdmin()
    {
        final List<Long> adminIds = new ArrayList<Long>();
        adminIds.add(TEST_PARENT_ORG_COORDINATOR_USER_ID);
        context.checking(new Expectations()
        {
            {
                oneOf(adminIdsMapper).execute(null);
                will(returnValue(adminIds));
            }
        });

        Set<Long> results = sut.execute(TEST_PARENT_ORG_COORDINATOR_USER_ID);

        assertNotNull(results);
        assertTrue(results.contains(TEST_PRIVATE_GROUP_ID));
    }

    /**
     * Method to test the execution when I have a coordinator of an org of a private group.
     */
    @Test
    public void testExecuteWithSystemAdmin2()
    {
        final List<Long> adminIds = new ArrayList<Long>();
        adminIds.add(TEST_ORG_COORDINATOR_USER_ID);
        context.checking(new Expectations()
        {
            {
                oneOf(adminIdsMapper).execute(null);
                will(returnValue(adminIds));
            }
        });

        Set<Long> results = sut.execute(TEST_ORG_COORDINATOR_USER_ID);

        assertNotNull(results);
        assertTrue(results.contains(TEST_PRIVATE_GROUP_ID));
    }
}
