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

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for SyncParentOrgForGroupActivities.
 */
public class SyncParentOrgForGroupActivitiesTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private SyncParentOrgForGroupActivities sut;

    /**
     * Test.
     */
    @Test
    public void testNoChanges()
    {
        assertEquals(0, sut.execute("thisGroupDoesNotExist").intValue());
    }

    /**
     * Test.
     */
    @Test
    public void testChanges()
    {
        // set activity to have different recipient parent org than actual recipient parent org as set in db.
        getEntityManager().createQuery(
                "UPDATE Activity SET recipientParentOrg=(FROM Organization WHERE id = 6) WHERE id = 6793")
                .executeUpdate();

        // verify it's different
        Activity a = (Activity) getEntityManager().createQuery("From Activity where id = 6793").getSingleResult();
        assertEquals(6, a.getRecipientParentOrg().getId());

        // execute sut.
        assertEquals(1, sut.execute("group1").intValue());
        getEntityManager().flush();
        getEntityManager().clear();

        // verify sut set recipient parent org on activity correctly as defined in db.
        a = (Activity) getEntityManager().createQuery("From Activity where id = 6793").getSingleResult();
        assertEquals(7, a.getRecipientParentOrg().getId());
    }
}
