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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the link builder.
 */
public class SamePageActivityLinkBuilderTest
{
    /** Test data. */
    private static final long ACTIVITY_ID = 87L;

    /** Test data. */
    private static final String STREAM_UNIQUE_ID = "mystreamentityuniqueid";

    /** SUT. */
    private SamePageActivityLinkBuilder sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SamePageActivityLinkBuilder();
    }

    /**
     * Tests building activity permalink URL request.
     */
    @Test
    public void testBuildActivityPermalink()
    {
        CreateUrlRequest result = sut.buildActivityPermalinkUrlRequest(ACTIVITY_ID, EntityType.PERSON,
                STREAM_UNIQUE_ID, null);

        assertEquals(Page.ACTIVITY, result.getPage());
        assertEquals(ACTIVITY_ID, Long.parseLong(result.getViews().get(0)));
    }
}
