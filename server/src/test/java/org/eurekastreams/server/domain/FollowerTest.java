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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for Follower/FollowerPk classes.
 *
 */
public class FollowerTest
{
    /**
     * Constructor test (and getters for follower/following ids).
     */
    @Test
    public void testConstructor()
    {
        Follower sut = new Follower(1L, 2L);

        assertEquals("FollowerId not expected value set in constructor.", 1, sut.getFollowerId());
        assertEquals("FolloweringId not expected value set in constructor.", 2, sut.getFollowingId());
    }

    /**
     * Test the equals override for FollowerPk.
     */
    @Test
    public void testFollowerPkEquals()
    {
        Follower.FollowerPk sut = new Follower.FollowerPk(1L, 2L);
        assertFalse(sut.equals(new Object()));

        Follower.FollowerPk target = new Follower.FollowerPk(1L, 2L);
        assertTrue(sut.equals(target));
    }

    /**
     * Test the hashcode override for FollowerPk.
     */
    @Test
    public void testFollowerPkHashcode()
    {
        Follower.FollowerPk sut = new Follower.FollowerPk(1L, 2L);
        assertNotNull(sut.hashCode());
    }

}
