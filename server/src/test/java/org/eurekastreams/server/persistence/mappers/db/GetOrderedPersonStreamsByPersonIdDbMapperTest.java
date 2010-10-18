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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for GetOrderedPersonStreamsByPersonIdDbMapper.
 */
public class GetOrderedPersonStreamsByPersonIdDbMapperTest extends MapperTest
{
    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        final Long fordId = 42L;
        final long firstStreamId = 2L;
        final long secondStreamId = 1L;

        GetOrderedPersonStreamsByPersonIdDbMapper sut = new GetOrderedPersonStreamsByPersonIdDbMapper();
        sut.setEntityManager(getEntityManager());
        List<PersonStream> personStreams = sut.execute(fordId);
        assertEquals(2, personStreams.size());

        assertEquals(firstStreamId, personStreams.get(0).getStreamId());
        assertEquals(0L, personStreams.get(0).getStreamIndex());

        assertEquals(secondStreamId, personStreams.get(1).getStreamId());
        assertEquals(1L, personStreams.get(1).getStreamIndex());
    }
}
