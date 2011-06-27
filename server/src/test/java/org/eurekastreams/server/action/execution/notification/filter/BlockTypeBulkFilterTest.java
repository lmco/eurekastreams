/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.filter;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.eurekastreams.server.domain.NotificationType;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests BlockTypeBulkFilter.
 */
public class BlockTypeBulkFilterTest
{
    /** SUT. */
    private BlockTypeBulkFilter sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new BlockTypeBulkFilter(Arrays.asList(NotificationType.FOLLOW_GROUP, NotificationType.FOLLOW_PERSON));
    }

    /**
     * Tests filtering.
     */
    @Test
    public void testShouldFilterPermit()
    {
        assertFalse(sut.shouldFilter(NotificationType.COMMENT_TO_COMMENTED_POST, null, null, null));
    }

    /**
     * Tests filtering.
     */
    @Test
    public void testShouldFilterDeny()
    {
        assertTrue(sut.shouldFilter(NotificationType.FOLLOW_GROUP, null, null, null));
    }
}
