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
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.MoveOrganizationPeopleRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for MoveOrganizationPeopleDBMapper.
 * 
 */
public class MoveOrganizationPeopleDBMapperTest extends MapperTest
{

    /**
     * System under test.
     */
    @Autowired
    private MoveOrganizationPeopleDBMapper sut;

    /**
     * Destination org short name from dataset.xml.
     */
    private String destinationOrgId = "child2orgname";

    /**
     * Source org short name from dataset.xml.
     */
    private String sourceOrgId = "child1orgname";

    /**
     * Test.
     */
    @Test
    public void test()
    {
        // get pre-sut.execute counts for source and destination.
        Long destinationGroupOriginalCount = (Long) getEntityManager().createQuery(
                "SELECT COUNT(id) FROM Person WHERE parentOrganization = "
                        + "(FROM Organization WHERE shortName =:destinationOrgId)").setParameter("destinationOrgId",
                destinationOrgId).getSingleResult();

        Long sourceGroupOriginalCount = (Long) getEntityManager().createQuery(
                "SELECT COUNT(id) FROM Person WHERE parentOrganization = "
                        + "(FROM Organization WHERE shortName =:sourceOrgId)").setParameter("sourceOrgId", sourceOrgId)
                .getSingleResult();

        // verify source has people to move.
        assertTrue(0 != sourceGroupOriginalCount.longValue());

        // exectue sut.
        sut.execute(new MoveOrganizationPeopleRequest(sourceOrgId, destinationOrgId));

        // get post-sut.execute counts for source and destination.
        Long sourceGroupCount = (Long) getEntityManager().createQuery(
                "SELECT COUNT(id) FROM Person WHERE parentOrganization = "
                        + "(FROM Organization WHERE shortName =:sourceOrgId)").setParameter("sourceOrgId", sourceOrgId)
                .getSingleResult();

        Long destinationGroupCount = (Long) getEntityManager().createQuery(
                "SELECT COUNT(id) FROM Person WHERE parentOrganization = "
                        + "(FROM Organization WHERE shortName =:destinationOrgId)").setParameter("destinationOrgId",
                destinationOrgId).getSingleResult();

        // verify all persons moved out of source
        assertEquals(0, sourceGroupCount.longValue());

        // verify that destination person count is correct.
        assertEquals(destinationGroupOriginalCount + sourceGroupOriginalCount, destinationGroupCount.longValue());
    }
}
