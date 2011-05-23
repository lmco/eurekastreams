/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the link builder.
 */
public class InContextActivityLinkBuilderTest
{
    /** Test data. */
    private static final long ACTIVITY_ID = 87L;

    /** Test data. */
    private static final String STREAM_UNIQUE_ID = "mystreamentityuniqueid";

    /** SUT. */
    private InContextActivityLinkBuilder sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new InContextActivityLinkBuilder();
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalinkUrlRequestNoExtras()
    {
        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.PERSON,
                STREAM_UNIQUE_ID, null);

        assertEquals(Page.PEOPLE, result.getPage());
        assertTrue(result.getReplacePrevious());
        assertEquals(1, result.getViews().size());
        assertEquals(STREAM_UNIQUE_ID, result.getViews().get(0));
        assertEquals(1, result.getParameters().size());
        assertEquals("87", result.getParameters().get("activityId"));
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalinkUrlRequestEmptyExtras()
    {
        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.GROUP, STREAM_UNIQUE_ID,
                new HashMap<String, String>());

        assertEquals(Page.GROUPS, result.getPage());
        assertTrue(result.getReplacePrevious());
        assertEquals(1, result.getViews().size());
        assertEquals(STREAM_UNIQUE_ID, result.getViews().get(0));
        assertEquals(1, result.getParameters().size());
        assertEquals("87", result.getParameters().get("activityId"));
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalinkUrlRequestExtras()
    {
        Map<String, String> parms = new HashMap<String, String>();
        parms.put("p1", "v1");
        parms.put("p2", "v2");
        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.GROUP, STREAM_UNIQUE_ID,
                parms);

        assertEquals(Page.GROUPS, result.getPage());
        assertTrue(result.getReplacePrevious());
        assertEquals(1, result.getViews().size());
        assertEquals(STREAM_UNIQUE_ID, result.getViews().get(0));
        assertEquals(3, result.getParameters().size());
        assertEquals("87", result.getParameters().get("activityId"));
        assertEquals("v1", result.getParameters().get("p1"));
        assertEquals("v2", result.getParameters().get("p2"));
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalinkUrlRequestUnknownStreamType()
    {
        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.APPLICATION,
                STREAM_UNIQUE_ID, null);

        assertEquals(Page.ACTIVITY, result.getPage());
        assertTrue(result.getReplacePrevious());
        assertTrue(result.getViews().isEmpty());
        assertEquals(1, result.getParameters().size());
        assertEquals("87", result.getParameters().get("activityId"));
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalinkUrlRequestUsesStoredParams()
    {
        sut.addExtraParameter("p1", "v1");
        sut.addExtraParameter("p2", "v2");
        sut.addExtraParameter("activityId", "overwriteThis");

        Map<String, String> extraParms = new HashMap<String, String>();
        extraParms.put("p2", "v2a");
        extraParms.put("p3", "v3");
        extraParms.put("activityId", "overwriteMe");

        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.APPLICATION,
                STREAM_UNIQUE_ID, extraParms);

        assertEquals(Page.ACTIVITY, result.getPage());
        assertTrue(result.getReplacePrevious());
        assertTrue(result.getViews().isEmpty());
        assertMap(result.getParameters(), "activityId", "87", "p1", "v1", "p2", "v2a", "p3", "v3");
    }

    /**
     * Tests that a result map contains what is expected.
     * 
     * @param actual
     *            Map actually returned.
     * @param entries
     *            Keys and values expected to be in the map.
     */
    private void assertMap(final Map<String, String> actual, final String... entries)
    {
        assertTrue("Test code is broken:  assertMap must have key-value pairs.", entries.length % 2 == 0);
        assertNotNull(actual);
        assertEquals(entries.length / 2, actual.size());
        for (int i = 0; i < entries.length; i += 2)
        {
            assertEquals(entries[i + 1], actual.get(entries[i]));
        }
    }
}
