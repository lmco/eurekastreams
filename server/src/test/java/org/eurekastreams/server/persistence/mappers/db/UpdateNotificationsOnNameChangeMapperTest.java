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

import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.UpdateNotificationsOnNameChangeRequest;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for UpdateNotificationsOnNameChangeMapper.
 * 
 */
public class UpdateNotificationsOnNameChangeMapperTest extends MapperTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    @Autowired
    private UpdateNotificationsOnNameChangeMapper sut;

    /**
     * Name used in tests.
     */
    private String name = "new";

    /**
     * Key used in tests.
     */
    private String key = "fordp";

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        // get initial num of results that sut should operate on.
        List<ApplicationAlertNotification> alerts = getEntityManager().createQuery(
                "FROM ApplicationAlertNotification aan WHERE aan.actorAccountId = :key").setParameter("key", key)
                .getResultList();

        int numResults = alerts.size();
        assertTrue(numResults > 0);

        UpdateNotificationsOnNameChangeRequest request = new UpdateNotificationsOnNameChangeRequest(EntityType.PERSON,
                key, name);

        // execute sut.
        sut.execute(request);

        getEntityManager().flush();
        getEntityManager().clear();

        // query for results.
        alerts.clear();
        alerts = getEntityManager()
                .createQuery("FROM ApplicationAlertNotification aan WHERE aan.actorAccountId = :key").setParameter(
                        "key", key).getResultList();

        int newNumResults = alerts.size();

        // verify same number of results after change.
        assertEquals(numResults, newNumResults);

        // loop through results and make sure all names are changed.
        for (ApplicationAlertNotification alert : alerts)
        {
            assertEquals(name, alert.getActorName());
        }

    }
}
