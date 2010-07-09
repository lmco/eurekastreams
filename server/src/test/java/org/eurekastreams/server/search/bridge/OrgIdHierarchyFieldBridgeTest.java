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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

/**
 * Test fixture for OrgShortNameHierarchyFieldBridge.
 */
public class OrgIdHierarchyFieldBridgeTest
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
     * Teardown method.
     */
    @After
    public void tearDown()
    {
        OrgIdHierarchyFieldBridge.setOrganizationHierarchyCache(null);
    }

    /**
     * Test converting an org to its parent hierarchy.
     */
    @Test
    public void testObjectToString1()
    {
        OrgIdHierarchyFieldBridge sut = new OrgIdHierarchyFieldBridge();
        final OrganizationHierarchyCache cache = context.mock(OrganizationHierarchyCache.class);
        final List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(1L);

        final Organization rootOrg = context.mock(Organization.class);
        context.checking(new Expectations()
        {
            {
                allowing(rootOrg).getId();
                will(returnValue(1L));

                allowing(cache).getSelfAndParentOrganizations(1L);
                will(returnValue(parentOrgIds));
            }
        });

        OrgIdHierarchyFieldBridge.setOrganizationHierarchyCache(cache);
        assertEquals("1", sut.objectToString(rootOrg).trim());

        context.assertIsSatisfied();
    }

    /**
     * Test converting an org to its parent hierarchy.
     */
    @Test
    public void testObjectToString2()
    {
        OrgIdHierarchyFieldBridge sut = new OrgIdHierarchyFieldBridge();
        final OrganizationHierarchyCache cache = context.mock(OrganizationHierarchyCache.class);
        final List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(1L);
        parentOrgIds.add(2L);
        parentOrgIds.add(3L);
        parentOrgIds.add(4L);
        parentOrgIds.add(5L);

        final Organization org = context.mock(Organization.class);
        context.checking(new Expectations()
        {
            {
                allowing(org).getId();
                will(returnValue(5L));

                allowing(cache).getSelfAndParentOrganizations(5L);
                will(returnValue(parentOrgIds));
            }
        });

        OrgIdHierarchyFieldBridge.setOrganizationHierarchyCache(cache);
        assertEquals("1 2 3 4 5", sut.objectToString(org).trim());

        context.assertIsSatisfied();
    }

    /**
     * Test objectToString returns null on bad input.
     */
    @Test(expected = RuntimeException.class)
    public void testObjectToStringWithoutCache()
    {
        OrgIdHierarchyFieldBridge sut = new OrgIdHierarchyFieldBridge();
        sut.objectToString(new Organization());
    }
}
