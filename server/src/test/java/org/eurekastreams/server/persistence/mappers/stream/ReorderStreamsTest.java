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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests ReorderStreams class.
 */
public class ReorderStreamsTest extends CachedMapperTest
{
    /**
     * Test user id.
     */
    private static final long FORDP_ID = 42L;

    /**
     * New hiddden line index.
     */
    private static int newHiddenLineIndex = 1;

    /**
     * System under test.
     */
    @Autowired
    private ReorderStreams sut;

    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        // Check the initial order
        String query = "from PersonStream where personId=:personId order by streamindex";
        Query q = getEntityManager().createQuery(query).setParameter("personId", FORDP_ID);
        
        List<PersonStream> result = q.getResultList();
        assertEquals(1L, result.get(0).getStreamId());
        assertEquals(2L, result.get(1).getStreamId());
        final Stream stream1 = new Stream();
        stream1.setId(1L);

        final Stream stream2 = new Stream();
        stream2.setId(2L);

        List<Stream> streams = Arrays.asList(stream2, stream1);

        sut.execute(FORDP_ID, streams, newHiddenLineIndex);

        // Check the resulting order
        q = getEntityManager().createQuery(query).setParameter("personId", FORDP_ID);
        result = q.getResultList();
        assertEquals(2L, result.get(0).getStreamId());
        assertEquals(1L, result.get(1).getStreamId());
    }
}
