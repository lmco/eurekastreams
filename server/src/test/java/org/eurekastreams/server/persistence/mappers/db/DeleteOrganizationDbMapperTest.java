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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteOrganizationDbMapper.
 */
public class DeleteOrganizationDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteOrganizationDbMapper sut;

    /**
     * Org id to delete.
     */
    private Long orgId = 7L;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        // move people out of org.
        getEntityManager().createQuery(
                "UPDATE VERSIONED Person SET parentOrganization = (From Organization where id = 6)"
                        + " WHERE parentOrganization = (From Organization where id = 7)").executeUpdate();

        // move groups out.
        getEntityManager().createQuery(
                "UPDATE DomainGroup SET parentOrganization = "
                        + "(From Organization where id = 6) WHERE parentOrganization = "
                        + "(From Organization where id = 7)").executeUpdate();

        // move activities out.
        getEntityManager().createQuery(
                "UPDATE Activity SET recipientParentOrg = (From Organization where id = 6) "
                        + " WHERE recipientParentOrg = (From Organization where id = 7)").executeUpdate();

        // get the org.
        Organization org = (Organization) getEntityManager().createQuery("From Organization where id = :orgId")
                .setParameter("orgId", orgId).getSingleResult();

        Long streamScopeId = org.getStreamScope().getId();

        sut.execute(orgId);

        getEntityManager().flush();
        getEntityManager().clear();

        // assert org is gone.
        assertEquals(0, getEntityManager().createQuery("From Organization where id = :orgId").setParameter("orgId",
                orgId).getResultList().size());

        // assert streamScope is gone
        assertEquals(0, getEntityManager().createQuery("From StreamScope where id = :streamScopeId").setParameter(
                "streamScopeId", streamScopeId).getResultList().size());
    }
}
