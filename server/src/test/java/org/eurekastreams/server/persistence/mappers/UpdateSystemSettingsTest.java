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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.SystemSettings;
import org.hibernate.validator.InvalidStateException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

/**
 * Integration test with UpdateMapper for system settings.
 * 
 */
public class UpdateSystemSettingsTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private UpdateMapper<SystemSettings> updateMapper;

    /**
     * Test execute normal scenario - updating an entry that is in db.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        Query q = getEntityManager().createQuery("FROM SystemSettings");

        // verify that entry is present.
        List<SystemSettings> results = q.getResultList();
        assertTrue(results.size() == 1);

        SystemSettings systemSettings = results.get(0);
        systemSettings.setSiteLabel("some other site label");
        systemSettings.setContentExpiration(1);
        systemSettings.setTosPromptInterval(1);

        // update.
        assertTrue(updateMapper.execute(null));

        // verify it's still there and not duplicated.
        assertTrue(q.getResultList().size() == 1);

        systemSettings = results.get(0);
        assertEquals(systemSettings.getSiteLabel(), "some other site label");
    }

    /**
     * Test execute with invalid tos prompt interval.
     */
    @SuppressWarnings("unchecked")
    @ExpectedException(InvalidStateException.class)
    @Test
    public void testExecuteWithInvalidTOSPromptInterval()
    {
        Query q = getEntityManager().createQuery("FROM SystemSettings");

        // verify that entry is present.
        List<SystemSettings> results = q.getResultList();
        assertTrue(results.size() == 1);

        SystemSettings systemSettings = results.get(0);
        systemSettings.setTosPromptInterval(-1);

        // update.
        updateMapper.execute(null);
    }

    /**
     * Test execute with invalid content expiration.
     */
    @SuppressWarnings("unchecked")
    @ExpectedException(InvalidStateException.class)
    @Test
    public void testExecuteWithInvalidContentExpiration()
    {
        Query q = getEntityManager().createQuery("FROM SystemSettings");

        // verify that entry is present.
        List<SystemSettings> results = q.getResultList();
        assertTrue(results.size() == 1);

        SystemSettings systemSettings = results.get(0);
        systemSettings.setContentExpiration(-1);

        // update.
        updateMapper.execute(null);
    }
}
