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
package org.eurekastreams.server.persistence.strategies;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.testing.DBUnitFixtureSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test fixture for the DescendantOrganizationStrategy.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DescendantOrganizationStrategyTest
{
    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Set the entity manager to use for all ORM operations.
     *
     * @param inEntityManager
     *            the EntityManager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * system under test.
     */
    @Autowired
    private OrganizationMapper jpaOrganizationMapper;

    /**
     * System under test.
     */
    private DescendantOrganizationStrategy descOrgStrategy;

    /**
     * PersonMapper.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * Setup method - create the SUT.
     *
     * @throws Exception
     *             on error
     */
    @Before
    public void setup() throws Exception
    {
        DBUnitFixtureSetup.loadDataSet("/dataset.xml");

        descOrgStrategy = new DescendantOrganizationStrategy();
        descOrgStrategy.setEntityManager(entityManager);
    }

    /**
     * Test getOrgIdByShortName().
     */
    @Test
    public void testGetOrgIdByShortName()
    {
        assertEquals(5L, descOrgStrategy.getOrgIdByShortName("tstorgname"));
        assertEquals(6L, descOrgStrategy.getOrgIdByShortName("child1orgname"));
        assertEquals(0L, descOrgStrategy.getOrgIdByShortName("sdifljsdlkjfsdf"));
    }

    /**
     * Test getDescendantOrganizationIdsForJpql.
     */
    @Test
    public void testGetDescendantOrganizationIdsForJpql()
    {
        assertEquals("5,6,7", descOrgStrategy.getDescendantOrganizationIdsForJpql(5, new HashMap<String, String>()));

        // add two new orgs to org #6
        Organization o1 = getNewOrganization("a");
        o1.setParentOrganization(jpaOrganizationMapper.findById(6L));
        jpaOrganizationMapper.insert(o1);

        assertEquals("5,6,7," + o1.getId(), descOrgStrategy.getDescendantOrganizationIdsForJpql(5,
                new HashMap<String, String>()));
        assertEquals("6," + o1.getId(), descOrgStrategy.getDescendantOrganizationIdsForJpql(6,
                new HashMap<String, String>()));

        Organization o2 = getNewOrganization("b");
        o2.setParentOrganization(jpaOrganizationMapper.findById(6L));
        jpaOrganizationMapper.insert(o2);

        HashMap<String, String> cache = new HashMap<String, String>();
        assertEquals("5,6,7," + o1.getId() + "," + o2.getId(), descOrgStrategy.getDescendantOrganizationIdsForJpql(5,
                cache));
        assertEquals("6," + o1.getId() + "," + o2.getId(), descOrgStrategy
                .getDescendantOrganizationIdsForJpql(6, cache));

        // add a new org to the new org #1 above
        Organization o3 = getNewOrganization("c");
        o3.setParentOrganization(o1);
        jpaOrganizationMapper.insert(o3);

        // need new cache since we changed stuff:
        cache = new HashMap<String, String>();

        assertEquals("5,6,7," + o1.getId() + "," + o2.getId() + "," + o3.getId(), descOrgStrategy
                .getDescendantOrganizationIdsForJpql(5, cache));
        assertEquals("6," + o1.getId() + "," + o2.getId() + "," + o3.getId(), descOrgStrategy
                .getDescendantOrganizationIdsForJpql(6, cache));
    }

    /**
     * Get a new organization to add to a parent.
     *
     * @param rand
     *            number to use to add on to the end of string values to get
     *            around constraints
     * @return a new organization ready to be added to a parent
     */
    private Organization getNewOrganization(final String rand)
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");

        Organization o = new Organization("sldfj: " + rand, "asdlkfj" + rand);
        o.setDescription("Foooo " + rand);
        o.setUrl("http://www.foo.com/" + rand);
        o.setDescription("mission: " + rand);
        o.addCoordinator(ford);
        return o;
    }
}
