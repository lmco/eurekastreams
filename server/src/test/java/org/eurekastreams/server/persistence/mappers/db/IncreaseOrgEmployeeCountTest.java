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

import org.eurekastreams.server.action.request.IncreaseOrgEmployeeCountRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for IncreaseOrgEmployeeCount mapper.
 */
public class IncreaseOrgEmployeeCountTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private IncreaseOrgEmployeeCount sut;

    /**
     * Root org id.
     */
    private final long rootOrgId = 5L;

    /**
     * Amount to increase by.
     */
    private final int incrementBy = 15;

    /**
     * Expected new amount.
     */
    private final int expectedCount = incrementBy + 2;

    /**
     * Test execute method.
     */
    @Test
    public void testExecute()
    {
        sut.execute(new IncreaseOrgEmployeeCountRequest(rootOrgId, incrementBy));

        int actualCount = (Integer) getEntityManager().createQuery(
                "select descendantEmployeeCount from Organization WHERE id = :orgId").setParameter("orgId", rootOrgId)
                .getSingleResult();

        assertEquals(expectedCount, actualCount);
    }
}
