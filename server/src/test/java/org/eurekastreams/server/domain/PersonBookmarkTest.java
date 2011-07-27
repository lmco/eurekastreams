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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for PersonBookmark classe.
 * 
 */
public class PersonBookmarkTest
{
    /**
     * Constructor test (and getters for person and stream ids).
     */
    @Test
    public void testConstructor()
    {
        PersonBookmark sut = new PersonBookmark(1L, 2L);

        assertEquals("StreamId not expected value set in constructor.", 1, sut.getScopeId());
        assertEquals("PersonId not expected value set in constructor.", 2, sut.getPersonId());
    }

    /**
     * Test the equals override for PersonBookmarkPk.
     */
    @Test
    public void testPersonBookmarkPkEquals()
    {
        PersonBookmark.PersonBookmarkPk sut = new PersonBookmark.PersonBookmarkPk(1L, 2L);
        assertFalse(sut.equals(new Object()));

        PersonBookmark.PersonBookmarkPk target = new PersonBookmark.PersonBookmarkPk(1L, 2L);
        assertTrue(sut.equals(target));
    }

    /**
     * Test the hashcode override for PersonBookmarkPk.
     */
    @Test
    public void testBookmarkPkHashcode()
    {
        PersonBookmark.PersonBookmarkPk sut = new PersonBookmark.PersonBookmarkPk(1L, 2L);
        assertNotNull(sut.hashCode());
    }
}
