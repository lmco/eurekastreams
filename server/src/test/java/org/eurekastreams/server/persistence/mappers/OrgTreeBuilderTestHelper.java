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
package org.eurekastreams.server.persistence.mappers;

import java.util.HashSet;

import javax.persistence.EntityManager;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.junit.Test;

/**
 * Test helper to add to the org tree:
 * 
 * 5 -6 -6a -6a1 -6a2 -7 -7a -7a1 .
 */
public class OrgTreeBuilderTestHelper extends MapperTest
{
    /**
     * ID of the person to use for coordinator for created orgs.
     */
    private static final Long NEW_ORG_COORDINATOR_ID = 42L;

    /**
     * Child org to be created under org 6.
     */
    private Organization org6a;

    /**
     * Child org to be created under org 6.
     */
    private Organization org6b;

    /**
     * Child org to be created under org 6a.
     */
    private Organization org6a1;

    /**
     * Child org to be created under org 6a.
     */
    private Organization org6a2;

    /**
     * Child org to be created under org 7.
     */
    private Organization org7a;

    /**
     * Child org to be created under org 7a.
     */
    private Organization org7a1;

    /**
     * Empty constructor.
     */
    public OrgTreeBuilderTestHelper()
    {
    }

    /**
     * To live in the test namespace, we have to have one test.
     */
    @Test
    public void noTest()
    {
        // no-op
    }

    /**
     * Constructor.
     * 
     * @param inEntityManager
     *            the entity manager
     */
    public OrgTreeBuilderTestHelper(final EntityManager inEntityManager)
    {
        setEntityManager(inEntityManager);
    }

    /**
     * Build the org tree. Org 6->(6a, 6b). Org 6a->(6a1, 6a2). Org 7->(7a). Org 7a->(7a1).
     */
    public void buildOrgTree()
    {
        // Org
        Organization org6 = getEntityManager().find(Organization.class, 6L);
        Organization org7 = getEntityManager().find(Organization.class, 7L);

        org6a = getNewOrganization("org6a");
        org6b = getNewOrganization("org6b");
        org6a1 = getNewOrganization("org6a1");
        org6a2 = getNewOrganization("org6a2");
        org7a = getNewOrganization("org7a");
        org7a1 = getNewOrganization("org7a1");

        org6a.setParentOrganization(org6);
        org6b.setParentOrganization(org6);
        org6a1.setParentOrganization(org6a);
        org6a2.setParentOrganization(org6a);
        org7a.setParentOrganization(org7);
        org7a1.setParentOrganization(org7a);

        getEntityManager().persist(org6a);
        getEntityManager().persist(org6b);
        getEntityManager().persist(org6a1);
        getEntityManager().persist(org6a2);
        getEntityManager().persist(org7a);
        getEntityManager().persist(org7a1);

        getEntityManager().flush();
    }

    /**
     * Get a new organization to add to a parent.
     * 
     * @param rand
     *            number to use to add on to the end of string values to get around constraints
     * @return a new organization ready to be added to a parent
     */
    private Organization getNewOrganization(final String rand)
    {
        Person ford = getEntityManager().find(Person.class, NEW_ORG_COORDINATOR_ID);

        StreamView entityStreamView = new StreamView();
        entityStreamView.setIncludedScopes(new HashSet<StreamScope>());
        entityStreamView.setName("FOO-" + rand);
        entityStreamView.setType(Type.NOTSET);

        StreamScope streamScope = new StreamScope();
        streamScope.setDisplayName("FOO-" + rand);
        streamScope.setScopeType(ScopeType.ORGANIZATION);
        streamScope.setUniqueKey("UniqueKey" + rand);

        Organization o = new Organization("sldfj: " + rand, "asdlkfj" + rand);
        o.setDescription("Foooo " + rand);
        o.setUrl("http://www.foo.com/" + rand);
        o.setDescription("mission: " + rand);
        o.addCoordinator(ford);

        o.setStreamScope(streamScope);
        o.setEntityStreamView(entityStreamView);

        return o;
    }

    /**
     * @return the org6a
     */
    public Organization getOrg6a()
    {
        return org6a;
    }

    /**
     * @return the org6b
     */
    public Organization getOrg6b()
    {
        return org6b;
    }

    /**
     * @return the org6a1
     */
    public Organization getOrg6a1()
    {
        return org6a1;
    }

    /**
     * @return the org6a2
     */
    public Organization getOrg6a2()
    {
        return org6a2;
    }

    /**
     * @return the org7a
     */
    public Organization getOrg7a()
    {
        return org7a;
    }

    /**
     * @return the org7a1
     */
    public Organization getOrg7a1()
    {
        return org7a1;
    }
}
