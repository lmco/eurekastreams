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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.persistence.strategies.DescendantOrganizationStrategy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the Person Mapper interface. The tests contained in
 * here ensure proper interaction with the database.
 */

public class OrganizationMapperTest extends DomainEntityMapperTest
{
    /**
     * system under test.
     */
    @Autowired
    private OrganizationMapper jpaOrganizationMapper;

    /**
     * Autowired QueryOptimizer.
     */
    @Autowired
    private QueryOptimizer queryOptimizer;

    /**
     * PersonMapper.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * DomainGroup mapper.
     */
    @Autowired
    private DomainGroupMapper jpaGroupMapper;

    /**
     * Tab group mapper for setup.
     */
    @Autowired
    private TabGroupMapper jpaTabGroupMapper;

    /**
     * Org id.
     */
    private Long orgId = 5L;

    /**
     * Dataset short org name.
     */
    private String shortName = "TstOrgName"; // from dataset.xml

    /**
     * Get a new organization to add to a parent.
     *
     * @param rand
     *            number to use to add on to the end of string values to get around constraints
     * @return a new organization ready to be added to a parent
     */
    private Organization getNewOrganization(final String rand)
    {
        final long tabGroupId = 4231L;
        TabGroup tabGroup = jpaTabGroupMapper.findById(tabGroupId);
        Person ford = jpaPersonMapper.findByAccountId("fordp");

        Organization o = new Organization("sldfj: " + rand, "asdlkfj" + rand);
        o.setDescription("Foooo " + rand);
        o.setUrl("http://www.foo.com/" + rand);
        o.setDescription("mission: " + rand);
        o.addCoordinator(ford);
        return o;
    }

    /**
     * Test the domain entity name of the mapper - used for parent class generic operations.
     */
    @Test
    public void testGetDomainEntityName()
    {
        assertEquals("Domain entity name should be 'Organization'", "Organization", jpaOrganizationMapper
                .getDomainEntityName());
    }

    /**
     * Test the queryOptimizer getter.
     */
    @Test
    public void testGetQueryOptimizer()
    {
        OrganizationMapper mapper = new OrganizationMapper(queryOptimizer);
        assertSame(queryOptimizer, mapper.getQueryOptimizer());
    }

    /**
     * Test FindByName.
     */
    @Test
    public void testFindByShortName()
    {
        long expectedId = 5L; // from dataset.
        assertEquals(expectedId, jpaOrganizationMapper.findByShortName(shortName).getId());

        assertNull("Object should not have been found, expected null", jpaOrganizationMapper
                .findByShortName("blahWhatever"));
    }

    /**
     * Test FindById.
     */
    @Test
    public void testFindById()
    {
        long expectedId = 5L; // from dataset.
        assertEquals(expectedId, jpaOrganizationMapper.findById(expectedId).getId());
    }

    /**
     * Test add/remove coordinators.
     */
    @Test
    public void testOrganizationCoordinators()
    {
        Organization sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(2, sut.getCoordinators().size());

        Set<Person> set = new HashSet<Person>();
        set.add(jpaPersonMapper.findByAccountId("mrburns"));
        set.add(jpaPersonMapper.findByAccountId("smithers"));
        set.add(jpaPersonMapper.findByAccountId("csagan"));

        sut.setCoordinators(set);

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(3, sut.getCoordinators().size());

        sut.removeCoordinator(jpaPersonMapper.findByAccountId("mrburns"));

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(2, sut.getCoordinators().size());

        sut.addCoordinator(jpaPersonMapper.findByAccountId("mrburns"));

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(3, sut.getCoordinators().size());
    }

    /**
     * Test parentOrgId formula field.
     */
    @Test
    public void testParentOrgId()
    {
        Organization sut = jpaOrganizationMapper.findById(6L);
        assertEquals(new Long(5L), sut.getParentOrgId());
    }

    /**
     * Test add/remove coordinators.
     */
    @Test
    public void testOrganizationLeaders()
    {
        Organization sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(2, sut.getLeaders().size());

        Set<Person> set = new HashSet<Person>();
        set.add(jpaPersonMapper.findByAccountId("mrburns"));
        set.add(jpaPersonMapper.findByAccountId("smithers"));
        set.add(jpaPersonMapper.findByAccountId("csagan"));

        sut.setLeaders(set);

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(3, sut.getLeaders().size());

        sut.removeLeader(jpaPersonMapper.findByAccountId("mrburns"));

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(2, sut.getLeaders().size());

        sut.addLeader(jpaPersonMapper.findByAccountId("mrburns"));

        getEntityManager().flush();
        getEntityManager().clear();

        sut = jpaOrganizationMapper.findById(orgId);
        assertEquals(3, sut.getLeaders().size());
    }

    /**
     * Test the GetChildOrganizations query on mapper.
     */
    @Test
    public void testGetDescendantOrganizations()
    {
        // Organization with itself as parent (top of tree) and two children
        // defined in
        // dataset.xml.
        List<Organization> results = jpaOrganizationMapper.getDescendantOrganizations(5L);
        assertEquals(2, results.size());

        // add a new org to org #6
        Organization o1 = getNewOrganization("a");
        o1.setParentOrganization(jpaOrganizationMapper.findById(6L));
        jpaOrganizationMapper.insert(o1);

        // check again
        results = jpaOrganizationMapper.getDescendantOrganizations(5L);
        assertEquals(3, results.size());
    }

    /**
     * Test the GetChildOrganizations query on mapper.
     */
    @Test
    public void testGetRootOrganization()
    {
        // Organization with itself as parent (top of tree) and two children
        // defined in
        // dataset.xml.
        Organization results = jpaOrganizationMapper.getRootOrganization();
        assertNotNull(results);
        assertEquals("tstorgname", results.getShortName()); // from
        assertSame(results, jpaOrganizationMapper.getRootOrganization());
    }

    /**
     * Get the child organization count.
     */
    @Test
    public void testChildOrganizationCount()
    {
        assertEquals(2, jpaOrganizationMapper.getEntityManager().createQuery(
                "select size(o.childOrganizations) from Organization o WHERE id=:id").setParameter("id", 5L)
                .getSingleResult());
    }

    /**
     * Test hashcode.
     */
    @Test
    public void testHashCode()
    {
        assertNotNull(jpaOrganizationMapper.findById(5L).hashCode());
    }

    /**
     * Test some of the different ways to get paged results, each page not filled.
     */
    @Test
    public void testPagedResultsPartiallyFilledPage()
    {
        final int from = 0;
        final int to = 9;
        runSeveralPagedResults(from, to);
    }

    /**
     * Test some of the different ways to get paged results, each page filled.
     */
    @Test
    public void testPagedResultsFilledPage()
    {
        final int from = 0;
        final int to = 1;
        runSeveralPagedResults(from, to);
    }

    /**
     * Execute several fetches for PagedResults with the input from/to indexes.
     *
     * @param from
     *            the starting index
     * @param to
     *            the ending index
     */
    private void runSeveralPagedResults(final int from, final int to)
    {
        // getPagedResults - no count query
        PagedSet<Organization> results = jpaOrganizationMapper.getPagedResults(from, to,
                "from Organization where parentOrganization.id=5 and id <> 5", new HashMap<String, Object>());
        assertEquals(2, results.getPagedSet().size());
        assertEquals(2, results.getTotal());

        // getPagedResults - with count query
        results = jpaOrganizationMapper.getPagedResults(from, to,
                "from Organization where parentOrganization.id=5 and id <> 5",
                "select size(childOrganizations) from Organization where id=5", new HashMap<String, Object>());
        assertEquals(2, results.getPagedSet().size());
        assertEquals(2, results.getTotal());

        // getTypedPagedResults - no count query
        String queryString = "from Organization where parentOrganization.id=:parentOrganizationId "
                + "and id <> :parentOrganizationId";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("parentOrganizationId", 5L);

        PagedSet<Organization> modelViewResults = jpaOrganizationMapper.getTypedPagedResults(from, to, queryString,
                parameters);
        assertEquals(2, modelViewResults.getPagedSet().size());
        assertEquals(2, modelViewResults.getTotal());

        // getTypedPagedResults - with count query
        String countQueryString = "select count(*) from Organization "
                + "where parentOrganization.id=:parentOrganizationId " + "and id <> :parentOrganizationId";
        modelViewResults = jpaOrganizationMapper.getTypedPagedResults(from, to, queryString, countQueryString,
                parameters);
        assertEquals(2, modelViewResults.getPagedSet().size());
        assertEquals(2, modelViewResults.getTotal());
    }

    /**
     * Test getPagedResults when zero are found, and no count query is given.
     */
    @Test
    public void testPagedResultsNoCountQueryZeroResults()
    {
        String queryString = "from Organization where "
                + "parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("parentOrganizationId", 0L);

        PagedSet<Organization> results = jpaOrganizationMapper.getPagedResults(0, 9, queryString, parameters);
        assertEquals(0, results.getTotal());
        assertEquals(0, results.getPagedSet().size());
    }

    /**
     * Test getPagedResults when zero are found, and a count query is given.
     */
    @Test
    public void testPagedResultsWithCountQueryZeroResults()
    {
        String queryString = "from Organization where "
                + "parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        String countQueryString = "select count(*) from Organization "
                + "where parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("parentOrganizationId", 0L);

        PagedSet<Organization> results = jpaOrganizationMapper.getPagedResults(0, 9, queryString, countQueryString,
                parameters);
        assertEquals(0, results.getTotal());
        assertEquals(0, results.getPagedSet().size());
    }

    /**
     * Test getPagedResults with invalid to/from throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPagedResultsWithoutCountInvalidRange()
    {
        String queryString = "from Organization where "
                + "parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("parentOrganizationId", 5L);

        jpaOrganizationMapper.getPagedResults(5, 4, queryString, parameters);
    }

    /**
     * Test getPagedResults with invalid to/from throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPagedResultsWithCountInvalidRange()
    {
        String queryString = "from Organization where "
                + "parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        String countQueryString = "select count(*) from Organization "
                + "where parentOrganization.id=:parentOrganizationId and id <> :parentOrganizationId";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("parentOrganizationId", 5L);

        jpaOrganizationMapper.getPagedResults(5, 4, queryString, countQueryString, parameters);
    }

    /**
     * Test deleting an organization.
     *
     * @throws Exception
     *             on error
     */
    public void testDelete() throws Exception
    {
        jpaOrganizationMapper.delete(8L);

        // TODO: make assertions - right now this does nothing
    }

    /**
     * Test setting a parent organization.
     */
    @Test
    public void testSetParentOrganization()
    {
        OrganizationMapper m = jpaOrganizationMapper;

        // add two new orgs to org #6
        Organization o1 = getNewOrganization("a");
        o1.setParentOrganization(jpaOrganizationMapper.findById(6L));
        jpaOrganizationMapper.insert(o1);

        Organization o2 = getNewOrganization("b");
        o2.setParentOrganization(jpaOrganizationMapper.findById(6L));
        jpaOrganizationMapper.insert(o2);

        // add a new org to the new org #1 above
        Organization o3 = getNewOrganization("c");
        o3.setParentOrganization(o1);
        jpaOrganizationMapper.insert(o3);

        // update the stats
        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(o1));

        // test all the org counts
        assertEquals(2, m.findById(5L).getChildOrganizationCount());
        assertEquals(2, m.findById(6L).getChildOrganizationCount());
        assertEquals(0, m.findById(7L).getChildOrganizationCount());
        assertEquals(1, m.findById(o1.getId()).getChildOrganizationCount());
        assertEquals(0, m.findById(o2.getId()).getChildOrganizationCount());
        assertEquals(0, m.findById(o3.getId()).getChildOrganizationCount());

        // add a new org to the new org #7 above
        Organization o4 = getNewOrganization("d");
        o4.setParentOrganization(jpaOrganizationMapper.findById(7L));
        jpaOrganizationMapper.insert(o4);

        // update the stats
        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(m.findById(7L)));

        assertEquals(2, m.findById(5L).getChildOrganizationCount());
        assertEquals(2, m.findById(6L).getChildOrganizationCount());
        assertEquals(1, m.findById(7L).getChildOrganizationCount());
        assertEquals(1, m.findById(o1.getId()).getChildOrganizationCount());
        assertEquals(0, m.findById(o2.getId()).getChildOrganizationCount());
        assertEquals(0, m.findById(o3.getId()).getChildOrganizationCount());
        assertEquals(0, m.findById(o4.getId()).getChildOrganizationCount());
    }

    /**
     * Test an organization's descendant employee count update.
     */
    @Test
    public void testDescendantEmployeeCount()
    {
        Organization org5 = jpaOrganizationMapper.findById(5L);
        Organization org6 = jpaOrganizationMapper.findById(6L);
        Organization org7 = jpaOrganizationMapper.findById(7L);

        // orgs 7 & 6 are siblings - call them together
        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(org6));
        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(org7));

        assertEquals(5, org5.getDescendantEmployeeCount());
        assertEquals(1, org6.getDescendantEmployeeCount());
        assertEquals(3, org7.getDescendantEmployeeCount());

        // now add a new person to org #6 and make sure it bubbles up
        Person p = new Person("foobar", "Foo", "b", "Bar", "fooby");
        p.setEmail("Foo.Bar@foobar.com");
        p.setParentOrganization(org6);
        jpaPersonMapper.insert(p);

        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(org6));
        assertEquals(6, org5.getDescendantEmployeeCount());
        assertEquals(2, org6.getDescendantEmployeeCount());
        assertEquals(3, org7.getDescendantEmployeeCount());

    }

    /**
     * Test an organization's descendant group count update.
     */
    @Test
    public void testDescendantGroupCount()
    {
        final long tabGroupId = 4231L;
        TabGroup tabGroup = jpaTabGroupMapper.findById(tabGroupId);
        Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(jpaPersonMapper.findByAccountId("fordp"));

        Organization org5 = jpaOrganizationMapper.findById(5L);
        Organization org6 = jpaOrganizationMapper.findById(6L);
        Organization org7 = jpaOrganizationMapper.findById(7L);

        Person ford = jpaPersonMapper.findByAccountId("fordp");

        // traverse 6 & 7 - the two leaf orgs
        OrganizationHierarchyTraverser orgTraverser = new OrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(org6);
        orgTraverser.traverseHierarchy(org7);
        jpaOrganizationMapper.updateOrganizationStatistics(orgTraverser);

        // test dataset.xml setup:
        assertEquals(5, org5.getDescendantGroupCount());
        assertEquals(2, org6.getDescendantGroupCount());
        assertEquals(2, org7.getDescendantGroupCount());

        // add a group to org 5 and org 6
        DomainGroup group1 = new DomainGroup("Foo1", "Bar1", ford);
        group1.setParentOrganization(org5);
        group1.setDescription("sdflsdj");
        group1.setCoordinators(coordinators);
        jpaGroupMapper.insert(group1);

        DomainGroup group2 = new DomainGroup("Foo2", "Bar2", ford);
        group2.setParentOrganization(org6);
        group2.setDescription("sdflksdl");
        group2.setCoordinators(coordinators);
        jpaGroupMapper.insert(group2);

        // org 6 is a child of 5, so updating it will update 5
        jpaOrganizationMapper.updateOrganizationStatistics(new OrganizationHierarchyTraverser(org6));

        // test changes:
        assertEquals(7, org5.getDescendantGroupCount());
        assertEquals(3, org6.getDescendantGroupCount());
        assertEquals(2, org7.getDescendantGroupCount());
    }

    /**
     * Test that calling getDescendantOrgStrategy() when not set throws NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testGetDescendantOrgStrategyWhenNotSet()
    {
        OrganizationMapper orgMapper = new OrganizationMapper(queryOptimizer);
        orgMapper.getDescendantOrgStrategy();
    }

    /**
     * Test that calling getDescendantOrgStrategy() when set.
     */
    @Test
    public void testGetDescendantOrgStrategyWhenSet()
    {
        OrganizationMapper orgMapper = new OrganizationMapper(queryOptimizer);
        DescendantOrganizationStrategy strategy = new DescendantOrganizationStrategy();
        orgMapper.setDescendantOrgStrategy(strategy);
        assertSame(strategy, orgMapper.getDescendantOrgStrategy());
    }
}
