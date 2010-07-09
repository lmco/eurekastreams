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
package org.eurekastreams.server.domain.strategies;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for OrganizationHierarchyTraverser.
 */
public class OrganizationHierarchyTraverserTest
{
    /**
     * Root organization.
     */
    private Organization root = new Organization("root", "root");

    /**
     * direct descendant org of the root org.
     */
    private Organization o1 = new Organization("o1", "o1");

    /**
     * direct descendant org of the root org.
     */
    private Organization o2 = new Organization("o2", "o2");

    /**
     * child org of o1.
     */
    private Organization o1a = new Organization("o1a", "o1a");

    /**
     * child org of o2.
     */
    private Organization o2a = new Organization("o2a", "o2a");

    /**
     * child org of o2a.
     */
    private Organization o2a1 = new Organization("o2a1", "o2a1");

    /**
     * Person that has the parent org o1.
     */
    private Person person = new Person();

    /**
     * Domain group that has the parent org, o2.
     */
    private DomainGroup domainGroup = new DomainGroup();

    /**
     * setup method for each test.
     */
    @Before
    public void setup()
    {
        root.setParentOrganization(root);
        o1.setParentOrganization(root);
        o2.setParentOrganization(root);
        o1a.setParentOrganization(o1);
        o2a.setParentOrganization(o2);
        o2a1.setParentOrganization(o2a);
        person.setParentOrganization(o1);
        domainGroup.setParentOrganization(o2);
    }

    /**
     * Test traversing the hierarchy.
     */
    @Test
    public void testTraverseHierarchyStartingWithOrganization()
    {
        OrganizationHierarchyTraverser sut;

        // climb the root
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(root);
        assertTrue(checkSet(new Organization[]{ root }, sut.getOrganizations()));

        // climb o1
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(o1);
        assertTrue(checkSet(new Organization[]{ root, o1 }, sut.getOrganizations()));

        // climb o2
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(o2);
        assertTrue(checkSet(new Organization[]{ root, o2 }, sut.getOrganizations()));

        // climb o1a
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(o1a);
        assertTrue(checkSet(new Organization[]{ root, o1, o1a }, sut.getOrganizations()));

        // climb o2a
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(o2a);
        assertTrue(checkSet(new Organization[]{ root, o2, o2a }, sut.getOrganizations()));

        // climb o2a1
        sut = new OrganizationHierarchyTraverser();
        sut.traverseHierarchy(o2a1);
        assertTrue(checkSet(new Organization[]{ root, o2, o2a, o2a1 }, sut.getOrganizations()));
    }

    /**
     * Test the constructor that starts traversing.
     */
    @Test
    public void testTraversingConstructor()
    {
        OrganizationHierarchyTraverser sut = new OrganizationHierarchyTraverser(o1);
        assertTrue(checkSet(new Organization[]{ root, o1 }, sut.getOrganizations()));

        sut.traverseHierarchy(o2a1);
        assertTrue(checkSet(new Organization[]{ root, o1, o2, o2a, o2a1 }, sut.getOrganizations()));
    }

    /**
     * Test traversing the hierarchy.
     */
    @Test
    public void testTraverseHierarchyWithDifferentTypes()
    {
        OrganizationHierarchyTraverser sut = new OrganizationHierarchyTraverser();

        // add the person
        sut.traverseHierarchy(person);
        assertTrue(checkSet(new Organization[]{ root, o1 }, sut.getOrganizations()));

        // add the domain group
        sut.traverseHierarchy(domainGroup);
        assertTrue(checkSet(new Organization[]{ root, o1, o2 }, sut.getOrganizations()));

        // add an org - it should add itself to the hierarchy
        sut.traverseHierarchy(o2a1);
        assertTrue(checkSet(new Organization[]{ root, o1, o2, o2a, o2a1 }, sut.getOrganizations()));
    }

    /**
     * Check to see if the input Set has the expected organization list.
     * 
     * @param expected
     *            array of expected organizations
     * @param actual
     *            the actual Set of returned organizations
     * @return true if the match, false otherwise
     */
    private boolean checkSet(final Organization[] expected, final Set<Organization> actual)
    {
        if (expected.length != actual.size())
        {
            throw new RuntimeException("Sizes aren't equal - was: " + actual.size() + ", expected: " + expected.length);
        }

        for (Organization expectedOrg : expected)
        {
            if (!contains(actual, expectedOrg.getShortName()))
            {
                throw new RuntimeException("Can't find " + expectedOrg.getShortName());
            }
        }

        return true;
    }

    /**
     * Check whether the Organization with the input shortName appears in the
     * input set.
     * 
     * @param orgs
     *            set of orgs to check
     * @param shortName
     *            the shortname of the org to look for
     * @return true if found, false otherwise
     */
    private boolean contains(final Set<Organization> orgs, final String shortName)
    {
        for (Organization org : orgs)
        {
            if (org.getShortName() == shortName)
            {
                return true;
            }
        }
        return false;
    }
}
