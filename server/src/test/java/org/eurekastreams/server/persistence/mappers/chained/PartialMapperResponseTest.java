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
package org.eurekastreams.server.persistence.mappers.chained;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test fixture for PartialMapperResponse.
 */
public class PartialMapperResponseTest
{
    /**
     * Test the constructor and getters.
     */
    @Test
    public void testGetters()
    {
        Object request = new Object();
        Object response = new Object();
        PartialMapperResponse<Object, Object> sut = new PartialMapperResponse<Object, Object>(response, request);
        assertSame(request, sut.getUnhandledRequest());
        assertSame(response, sut.getResponse());
    }

    /**
     * Test hasCompleteResponse when response is incomplete.
     */
    @Test
    public void testHasCompleteResponseOnIncomplete()
    {
        Object request = null;
        Object response = new Object();
        PartialMapperResponse<Object, Object> sut = new PartialMapperResponse<Object, Object>(response, request);
        assertTrue(sut.hasCompleteResponse());
    }

    /**
     * Test hasCompleteResponse when response is complete.
     */
    @Test
    public void testHasCompleteResponseOnComplete()
    {
        Object request = new Object();
        Object response = new Object();
        PartialMapperResponse<Object, Object> sut = new PartialMapperResponse<Object, Object>(response, request);
        assertFalse(sut.hasCompleteResponse());
    }

}
