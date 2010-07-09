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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.junit.Test;

/**
 * Test fixture for UpdateDestinationStreamNameInCachedDomainGroupActivity.
 */
public class UpdateDestinationStreamNameInCachedActivityTest
{
    /**
     * Batch size.
     */
    private final Integer batchSize = 2398;

    /**
     * System under test.
     */
    private UpdateDestinationStreamNameInCachedActivity
    // line break
    sut = new UpdateDestinationStreamNameInCachedActivity(batchSize);

    /**
     * Test updateCachedEntity() when the cached activity's destination stream name is not changing.
     */
    @Test
    public void testUpdateCachedEntityNameNotChanging()
    {
        final String existingDisplayName = "abcdefg";
        final String newDisplayName = existingDisplayName;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setDisplayName(existingDisplayName);

        activity.setDestinationStream(destinationStream);

        sut.updateCachedEntity(activity, newDisplayName);

        assertFalse(sut.updateCachedEntity(activity, newDisplayName));
        assertEquals(newDisplayName, destinationStream.getDisplayName());
    }

    /**
     * Test updateCachedEntity() when the cached activity's destination stream name is changing.
     */
    @Test
    public void testUpdateCachedEntityNameChanging()
    {
        final String existingDisplayName = "abcdefg";
        final String newDisplayName = "ABCDEFG";

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setDisplayName(existingDisplayName);

        activity.setDestinationStream(destinationStream);

        assertTrue(sut.updateCachedEntity(activity, newDisplayName));
        assertEquals(newDisplayName, destinationStream.getDisplayName());
    }
}
