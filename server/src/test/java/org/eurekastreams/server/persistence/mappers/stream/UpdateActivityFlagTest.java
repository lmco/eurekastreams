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
package org.eurekastreams.server.persistence.mappers.stream;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.UpdateActivityFlagRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the UpdateActivityFlag mapper.
 */
public class UpdateActivityFlagTest extends MapperTest
{
    /** Test data. */
    private static final long FLAGGED_ID = 6789L;

    /** Test data. */
    private static final long UNFLAGGED_ID = 6790L;

    /** SUT. */
    private UpdateActivityFlag sut;

    /**
     * Preparation for the test suite.
     */
    @Before
    public void setup()
    {
        sut = new UpdateActivityFlag();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Core test.
     *
     * @param id
     *            ID of activity to test with.
     * @param toFlag
     *            Whether to set or clear the flag.
     * @param shouldChange
     *            If the activity should be updated.
     */
    @SuppressWarnings("unchecked")
    private void coreTestExecute(final long id, final boolean toFlag, final boolean shouldChange)
    {
        UpdateActivityFlagRequest rqst = new UpdateActivityFlagRequest(id, toFlag);
        boolean result = sut.execute(rqst);

        getEntityManager().flush();

        Query q = getEntityManager().createQuery("FROM Activity WHERE id = :id").setParameter("id", id);
        List<Activity> results = q.getResultList();

        assertEquals(1, results.size());
        assertEquals(toFlag, results.get(0).isFlagged());
        assertEquals(shouldChange, result);
    }

    /**
     * Test execute method: not flagged yet, try to flag.
     */
    @Test
    public void testExecuteFlag()
    {
        coreTestExecute(UNFLAGGED_ID, true, true);
    }

    /**
     * Test execute method: not flagged yet, try to unflag.
     */
    @Test
    public void testExecuteUnflagAlready()
    {
        coreTestExecute(UNFLAGGED_ID, false, false);
    }

    /**
     * Test execute method: flagged yet, try to flag.
     */
    @Test
    public void testExecuteFlagAlready()
    {
        coreTestExecute(FLAGGED_ID, true, false);
    }

    /**
     * Test execute method: flagged yet, try to unflag.
     */
    @Test
    public void testExecuteUnflag()
    {
        coreTestExecute(FLAGGED_ID, false, true);
    }

}
