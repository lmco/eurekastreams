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
package org.eurekastreams.server.persistence.mappers.stream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests getting activity DTOs from a list of activity ids.
 */
@TransactionConfiguration(defaultRollback = false)
public class BulkActivitiesMapperTest extends CachedMapperTest
{
    /**
     * The main activity id to test with.
     */
    private static final long ACTIVITY_ID = 6789;

    /**
     * An additional activity id to test with.
     */
    private static final long ACTIVITY_ID_2 = 6790;

    /**
     * User id.
     */
    private static final String PERSON_ID = "smithers";

    /**
     * Stream id.
     */
    private static final long DESTINATION_STREAM = 87433;

    /**
     * System under test.
     */
    @Autowired
    private BulkActivitiesMapper mapper;

    /**
     * Verifies that the activity returned when requesting ID 6789 is correct.
     * 
     * @param activity
     *            The activity returned.
     */
    private void verifyActivity6789(final ActivityDTO activity)
    {
        assertNotNull(activity);
        assertEquals(ActivityVerb.POST, activity.getVerb());
        assertEquals(EntityType.PERSON, activity.getDestinationStream().getType());
        assertEquals(DESTINATION_STREAM, activity.getDestinationStream().getId());
        assertEquals("smithers", activity.getDestinationStream().getUniqueIdentifier());
        assertEquals("Smithers Smithers", activity.getActor().getDisplayName());
        assertTrue(activity.isStarred());
        assertEquals(7L, activity.getRecipientParentOrgId());

        // Assert first and last comments and comment count were set correctly.
        assertEquals(1, activity.getFirstComment().getId());
        assertEquals(3, activity.getLastComment().getId());
        assertEquals(3, activity.getCommentCount());
    }

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(ACTIVITY_ID));
        List<ActivityDTO> results = mapper.execute(list, PERSON_ID);
        assertEquals(1, results.size());
        verifyActivity6789(results.get(0));

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list, PERSON_ID);
        assertEquals(1, results.size());
        verifyActivity6789(results.get(0));
    }

    /**
     * test.
     */
    @Test
    public void testExecuteSingle()
    {
        ActivityDTO result = mapper.execute(ACTIVITY_ID, PERSON_ID);
        verifyActivity6789(result);

        // now that the cache should be populated, run the execute again
        result = mapper.execute(ACTIVITY_ID, PERSON_ID);
        verifyActivity6789(result);
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(ACTIVITY_ID));
        list.add(new Long(ACTIVITY_ID_2));
        List<ActivityDTO> results = mapper.execute(list, PERSON_ID);
        assertEquals(2, results.size());

        results = mapper.execute(list, PERSON_ID);
        assertEquals(2, results.size());
        assertEquals(ACTIVITY_ID, results.get(0).getEntityId());

        // Test that order is correct, this time id_2 is first
        list = new ArrayList<Long>();
        list.add(new Long(ACTIVITY_ID_2));
        list.add(new Long(ACTIVITY_ID));
        results = mapper.execute(list, PERSON_ID);
        assertEquals(ACTIVITY_ID_2, results.get(0).getEntityId());
        assertTrue(results.get(0).getIsDestinationStreamPublic());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithNullUser()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(ACTIVITY_ID));
        List<ActivityDTO> results = mapper.execute(list, null);
        assertEquals(1, results.size());
    }
}
