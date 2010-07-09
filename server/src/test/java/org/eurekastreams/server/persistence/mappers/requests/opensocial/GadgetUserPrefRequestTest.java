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
package org.eurekastreams.server.persistence.mappers.requests.opensocial;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for GadgetUserPrefRequest.
 *
 */
public class GadgetUserPrefRequestTest
{
    /**
     * Test gadget instance id.
     */
    private final Long moduleId = new Long(31L);

    /**
     * Test gadget instance id.
     */
    private final Long moduleId2 = new Long(51L);

    /**
     * Test user prefs.
     */
    private final String userPrefs = "{userPref1:value1,userPref2:value2}";

    /**
     * Test user prefs.
     */
    private final String userPrefs2 = "{userPref3:value3,userPref4:value4}";

    /**
     * Simple test to ensure that the getters work.
     */
    @Test
    public void testGetters()
    {
        GadgetUserPrefRequest sut = new GadgetUserPrefRequest(moduleId, userPrefs);

        assertEquals(moduleId, sut.getGadgetId());
        assertEquals(userPrefs, sut.getUserPrefs());
    }

    /**
     * Simple test to ensure that the setters work.
     */
    @Test
    public void testSetters()
    {
        GadgetUserPrefRequest sut = new GadgetUserPrefRequest(moduleId, userPrefs);
        sut.setGadgetId(moduleId2);
        sut.setUserPrefs(userPrefs2);

        assertEquals(moduleId2, sut.getGadgetId());
        assertEquals(userPrefs2, sut.getUserPrefs());
    }
}
