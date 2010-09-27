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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for OrganizationHierarchyCachePopulator.
 */
public class OrganizationHierarchyCacheLoaderTest extends CachedMapperTest
{
    /**
     * ID of the person to use for coordinator for created orgs.
     */
    private static final Long NEW_ORG_COORDINATOR_ID = 42L;

    /**
     * System under test.
     */
    @Autowired
    private OrganizationHierarchyCacheLoader sut;

    /**
     * Org Cache.
     */
    @Autowired
    private OrganizationHierarchyCache orgCache;

    /**
     * Test initializedOrganizationHierarchyCache().
     */
    @Test
    public void testInitializeOrganizationHierarchyCache()
    {
        sut.initialize();

        assertNotNull(getCache().get("Org:5"));

        // test direct children of all orgs:
        assertEquals(2, orgCache.getDirectChildOrganizations(5).size());
        assertEquals(0, orgCache.getDirectChildOrganizations(6).size());
        assertEquals(0, orgCache.getDirectChildOrganizations(7).size());
        assertTrue(orgCache.getDirectChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getDirectChildOrganizations(5).contains(7L));

        // test recursive children of all orgs
        assertEquals(2, orgCache.getRecursiveChildOrganizations(5).size());
        assertEquals(0, orgCache.getRecursiveChildOrganizations(6).size());
        assertEquals(0, orgCache.getRecursiveChildOrganizations(7).size());
        assertTrue(orgCache.getRecursiveChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getRecursiveChildOrganizations(5).contains(7L));

        // test self-and-recursive children of all orgs
        assertEquals(3, orgCache.getSelfAndRecursiveChildOrganizations(5).size());
        assertEquals(1, orgCache.getSelfAndRecursiveChildOrganizations(6).size());
        assertEquals(1, orgCache.getSelfAndRecursiveChildOrganizations(7).size());
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(5L));
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(7L));

        // test parent trees
        assertEquals(0, orgCache.getParentOrganizations(5L).size());
        assertArrayEquals(new Long[] { 5L }, orgCache.getParentOrganizations(6L).toArray());
        assertArrayEquals(new Long[] { 5L }, orgCache.getParentOrganizations(7L).toArray());

        assertArrayEquals(new Long[] { 5L }, orgCache.getSelfAndParentOrganizations(5L).toArray());
        assertArrayEquals(new Long[] { 5L, 6L }, orgCache.getSelfAndParentOrganizations(6L).toArray());
        assertArrayEquals(new Long[] { 5L, 7L }, orgCache.getSelfAndParentOrganizations(7L).toArray());
    }

    /**
     * Put org 7 under org 6 and make sure that the direct and recursive collections are correctly initialized.
     */
    @Test
    public void testInitializeOrganizationHierarchyCacheAfterOrgHierarchyChange()
    {
        // put org 7 under org 6
        Organization org6 = getEntityManager().find(Organization.class, 6L);
        Organization org7 = getEntityManager().find(Organization.class, 7L);

        org7.setParentOrganization(org6);
        getEntityManager().flush();
        getEntityManager().clear();

        // populate cache
        sut.initialize();

        // test direct children of all orgs:
        assertEquals(1, orgCache.getDirectChildOrganizations(5).size());
        assertEquals(1, orgCache.getDirectChildOrganizations(6).size());
        assertEquals(0, orgCache.getDirectChildOrganizations(7).size());
        assertTrue(orgCache.getDirectChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getDirectChildOrganizations(6).contains(7L));

        // ** test recursive children of all orgs
        assertEquals(2, orgCache.getRecursiveChildOrganizations(5).size());
        assertEquals(1, orgCache.getRecursiveChildOrganizations(6).size());
        assertEquals(0, orgCache.getRecursiveChildOrganizations(7).size());

        // 5 has 6 & 7
        assertTrue(orgCache.getRecursiveChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getRecursiveChildOrganizations(5).contains(7L));

        // 6 has 7
        assertTrue(orgCache.getRecursiveChildOrganizations(6).contains(7L));

        // ** test self-and-recursive children of all orgs
        assertEquals(3, orgCache.getSelfAndRecursiveChildOrganizations(5).size());
        assertEquals(2, orgCache.getSelfAndRecursiveChildOrganizations(6).size());
        assertEquals(1, orgCache.getSelfAndRecursiveChildOrganizations(7).size());

        // 5 has 5, 6, 7
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(5L));
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(6L));
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(5).contains(7L));

        // 6 has 6 & 7
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(6).contains(6L));
        assertTrue(orgCache.getSelfAndRecursiveChildOrganizations(6).contains(7L));

        // test parent trees
        assertEquals(0, orgCache.getParentOrganizations(5L).size());
        assertArrayEquals(new Long[] { 5L }, orgCache.getParentOrganizations(6L).toArray());
        assertArrayEquals(new Long[] { 5L, 6L }, orgCache.getParentOrganizations(7L).toArray());

        assertArrayEquals(new Long[] { 5L }, orgCache.getSelfAndParentOrganizations(5L).toArray());
        assertArrayEquals(new Long[] { 5L, 6L }, orgCache.getSelfAndParentOrganizations(6L).toArray());
        assertArrayEquals(new Long[] { 5L, 6L, 7L }, orgCache.getSelfAndParentOrganizations(7L).toArray());
    }

    /**
     * Test the org short name cache loading.
     */
    @Test
    public void testOrgShortNameCacheLoading()
    {
        sut.initialize();

        assertEquals(new Long(5), orgCache.getOrganizationIdFromShortName("tstorgname"));
        assertEquals(new Long(6), orgCache.getOrganizationIdFromShortName("child1orgname"));
        assertEquals(new Long(7), orgCache.getOrganizationIdFromShortName("child2orgname"));
    }

    /**
     * Test onPostPersist.
     */
    @Test
    public void testOnPostPersist()
    {
        sut.initialize();

        // create a new org under org #6
        Organization o = getNewOrganization("ShortName");
        o.setParentOrganization(getEntityManager().find(Organization.class, 6L));
        getEntityManager().persist(o);

        // stick something random in the org tree cache to make sure it's deleted on persist
        getCache().set(CacheKeys.ORGANIZATION_TREE_DTO, "FOO");
        getCache().set(CacheKeys.ORGANIZATION_DIRECT_CHILDREN + 6L, "BAR");

        // SUT: call post-update
        sut.onPostPersist(o);

        // make sure the org tree and parent org ids were blown out of cache
        assertNull(getCache().get(CacheKeys.ORGANIZATION_TREE_DTO));
        assertNull(getCache().get(CacheKeys.ORGANIZATION_DIRECT_CHILDREN + 6L));
    }

    /**
     * Get a new organization to add to a parent.
     *
     * @param inShortName
     *            The org short name
     * @return a new organization ready to be added to a parent
     */
    private Organization getNewOrganization(final String inShortName)
    {
        Person ford = getEntityManager().find(Person.class, NEW_ORG_COORDINATOR_ID);

        StreamScope streamScope = new StreamScope();
        streamScope.setDisplayName("FOO-" + inShortName);
        streamScope.setScopeType(ScopeType.ORGANIZATION);
        streamScope.setUniqueKey("UniqueKey" + inShortName);

        Organization o = new Organization("Name for " + inShortName, inShortName);
        o.setDescription("Foooo " + inShortName);
        o.setUrl("http://www.foo.com/" + inShortName);
        o.setDescription("mission: " + inShortName);
        o.addCoordinator(ford);

        o.setStreamScope(streamScope);

        return o;
    }
}
