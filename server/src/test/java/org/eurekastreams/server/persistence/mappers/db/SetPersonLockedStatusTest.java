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

import java.util.List;

import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetPersonIdsByLockedStatus.
 * 
 */
public class SetPersonLockedStatusTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private SetPersonLockedStatus sut;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        Long total = (Long) getEntityManager().createQuery("SELECT COUNT(id) FROM Person").getSingleResult();
        assertTrue(total > 0);

        // set all false
        getEntityManager().createQuery("UPDATE Person SET accountLocked = false").executeUpdate();

        // use sut to set one true.
        sut.execute(new SetPersonLockedStatusRequest("fordp", true));

        // get back account id of anyone that is true.
        List<String> oddball = getEntityManager().createQuery(
                "SELECT accountId FROM Person WHERE accountLocked = :lockedStatus").setParameter("lockedStatus", true)
                .getResultList();

        // verify that there's only one and it's the correct one.
        assertEquals(1, oddball.size());
        assertEquals("fordp", oddball.get(0));

        // lather rinse repeat with reversed value of locked.
        getEntityManager().createQuery("UPDATE Person SET accountLocked = true").executeUpdate();
        sut.execute(new SetPersonLockedStatusRequest("fordp", false));

        oddball = getEntityManager().createQuery("SELECT accountId FROM Person WHERE accountLocked = :lockedStatus")
                .setParameter("lockedStatus", false).getResultList();

        assertEquals(1, oddball.size());
        assertEquals("fordp", oddball.get(0));

    }
}
