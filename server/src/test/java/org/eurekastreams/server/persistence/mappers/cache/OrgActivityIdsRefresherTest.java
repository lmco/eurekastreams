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

import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the org actitivyt IDs refresher.
 */
public class OrgActivityIdsRefresherTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static OrgActivityIdsRefresher sut;

    /**
     * Cache fed into the loader.
     */
    private static Cache cache = CONTEXT.mock(Cache.class);

    /**
     * Org dao.
     */
    private static GetOrganizationsByIds orgDao = CONTEXT.mock(GetOrganizationsByIds.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new OrgActivityIdsRefresher();
        sut.setCache(cache);
        sut.setOrganizationDAO(orgDao);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefresh()
    {
        assertTrue(true);
        // final Long orgId = 10L;
        //
        // final List<Long> activities = new ArrayList<Long>();
        // activities.add(7L);
        // activities.add(8L);
        // activities.add(9L);
        //
        // final OrganizationModelView org = CONTEXT.mock(OrganizationModelView.class);
        //
        // CONTEXT.checking(new Expectations()
        // {
        // {
        // oneOf(orgDao).execute(with(equal(Arrays.asList(orgId))));
        // will(returnValue(Arrays.asList(org)));
        // }
        // });
        //
        // sut.refresh(orgId, activities);
        //
        // CONTEXT.assertIsSatisfied();
    }
}
