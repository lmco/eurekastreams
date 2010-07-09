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
package org.eurekastreams.server.persistence.mappers.requests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test fixture for StreamSearchRequest.
 */
public class StreamSearchRequestTest
{
    /**
     * Test the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters()
    {
        StreamSearchRequest sut = new StreamSearchRequest("heynow123", 3L, "foo", 8, 9);
        assertEquals(3L, sut.getStreamViewId());
        assertEquals("foo", sut.getSearchText());
        assertEquals(8, sut.getPageSize());
        assertEquals(9, sut.getLastSeenStreamItemId());
        assertEquals("heynow123", sut.getRequestingUserAccountId());
    }

    /**
     * Test the setters/getters.
     */
    @Test
    public void testSettersAndGetters()
    {
        StreamSearchRequest sut = new StreamSearchRequest();
        sut.setStreamViewId(3L);
        sut.setSearchText("foo");
        sut.setPageSize(8);
        sut.setLastSeenStreamItemId(9);
        sut.setRequestingUserAccountId("heynow123");
        assertEquals(3L, sut.getStreamViewId());
        assertEquals("foo", sut.getSearchText());
        assertEquals(8, sut.getPageSize());
        assertEquals(9, sut.getLastSeenStreamItemId());
        assertEquals("heynow123", sut.getRequestingUserAccountId());
    }
}
