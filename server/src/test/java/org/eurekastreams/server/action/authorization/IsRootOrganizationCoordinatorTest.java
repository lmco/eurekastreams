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
package org.eurekastreams.server.action.authorization;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for IsRootOrganizationCoordinatorStrategy class.
 */
public class IsRootOrganizationCoordinatorTest
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
     * Mapper to get the system admin ids.
     */
    final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private IsRootOrganizationCoordinator sut;

    /**
     * User id.
     */
    private Long userId = 1L;

    /**
     * Pre-test setup.
     */
    @Before
    public void setUp()
    {
        sut = new IsRootOrganizationCoordinator(systemAdminIdsMapper);
    }

    /**
     * Test.
     */
    @Test
    public void testTrue()
    {
        final List<Long> adminIds = new ArrayList<Long>();
        adminIds.add(userId);
        adminIds.add(9L);
        context.checking(new Expectations()
        {
            {
                oneOf(systemAdminIdsMapper).execute(null);
                will(returnValue(adminIds));
            }
        });

        assertTrue(sut.isRootOrganizationCoordinator(userId));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testFalse()
    {
        final List<Long> adminIds = new ArrayList<Long>();
        adminIds.add(8L);
        adminIds.add(9L);
        context.checking(new Expectations()
        {
            {
                oneOf(systemAdminIdsMapper).execute(null);
                will(returnValue(adminIds));
            }
        });

        Assert.assertFalse(sut.isRootOrganizationCoordinator(userId));
        context.assertIsSatisfied();
    }
}
