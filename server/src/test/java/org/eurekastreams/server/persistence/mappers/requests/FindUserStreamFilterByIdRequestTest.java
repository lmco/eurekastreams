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
 * Test class for FindUserStreamViewByIdRequest.
 *
 */
public class FindUserStreamFilterByIdRequestTest
{
    /**
     * Test constructor and getters.
     */
    @Test
    public void testIt()
    {
        final long personId = 99;
        final long svId = 4895;
        FindUserStreamFilterByIdRequest sut = new FindUserStreamFilterByIdRequest(personId, svId);
        assertEquals(personId, sut.getPersonId());
        assertEquals(svId, sut.getStreamFilterId());
    }

}
