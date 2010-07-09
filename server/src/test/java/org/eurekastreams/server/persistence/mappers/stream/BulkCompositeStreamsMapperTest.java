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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests getting composite streams from a list of composite stream ids.
 */
public class BulkCompositeStreamsMapperTest extends CachedMapperTest
{
    /**
     * The main id to test with.
     */
    private static final long COMPOSITE_STREAM_ID = 883;

    /**
     * An additional id to test with.
     */
    private static final long COMPOSITE_STREAM_ID_2 = 938;

    /**
     * An bogus id to test with.
     */
    private static final long BOGUS_COMPOSITE_STREAM_ID = 99999999;

    /**
     * System under test.
     */
    @Autowired
    private BulkCompositeStreamsMapper mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(COMPOSITE_STREAM_ID));
        List<StreamFilter> results = mapper.execute(list);
        assertEquals(1, results.size());

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(1, results.size());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithBadId()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(BOGUS_COMPOSITE_STREAM_ID));
        List<StreamFilter> results = mapper.execute(list);
        assertEquals(0, results.size());

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(0, results.size());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(COMPOSITE_STREAM_ID));
        list.add(new Long(COMPOSITE_STREAM_ID_2));
        List<StreamFilter> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
        assertEquals(COMPOSITE_STREAM_ID, results.get(0).getId());

        // Make sure order is maintained.
        list = new ArrayList<Long>();
        list.add(new Long(COMPOSITE_STREAM_ID_2));
        list.add(new Long(COMPOSITE_STREAM_ID));
        results = mapper.execute(list);
        assertEquals(COMPOSITE_STREAM_ID_2, results.get(0).getId());
    }
}
