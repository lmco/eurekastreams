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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for the GetRefreshableFeedsMapper class.
 *
 */
public class GetRefreshableFeedsMapperTest extends MapperTest
{
    /**
     * Time 1011.
     */
    private static final long TIME1011 = 1011;

    /**
     * Time 1031.
     */
    private static final long TIME1031 = 1031;

    /**
     * Time 1061.
     */
    private static final long TIME1061 = 1061;

    /**
     * Time 1005.
     */
    private static final long TIME1005 = 1005;

    /**
     * System under test.
     */
    @Autowired
    private GetRefreshableFeedsMapper sut;

    /**
     * All of the feeds in data set were updated at minute 1000. Only one of them is not pending and has an update time
     * < 11, so this should only return 1 feed, feed ID 1.
     */
    @Test
    public void testExecuteAtTime1011()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1011);
        List<Feed> results = sut.execute(request);

        assertTrue(results.size() == 1);
        assertTrue(results.get(0).getId() == 1L);
        assertTrue(results.get(0).getUrl().equals("http://www.google1.com"));
    }

    /**
     * Two of the feeds have update times of less than 31 (10 and 30, IDs 1 and 5) One of the feeds has no update time
     * but it's plugin has an update time of 30. Therefore this should return 3 feeds.
     */
    @Test
    public void testExecuteAtTime1031()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1031);
        List<Feed> results = sut.execute(request);

        assertTrue(results.size() == 3);
        assertTrue(results.get(0).getId() == 1L);
        assertTrue(results.get(1).getId() == 4L);
        assertTrue(results.get(2).getId() == 5L);
    }

    /**
     * All of the feeds have over under 60 min refresh times except the 1 pending Four results should be returned.
     */
    @Test
    public void testExecuteAtTime1061()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1061);
        List<Feed> results = sut.execute(request);

        assertTrue(results.size() == 4);
        assertTrue(results.get(0).getId() == 1L);
        assertTrue(results.get(1).getId() == 3L);
        assertTrue(results.get(2).getId() == 4L);
        assertTrue(results.get(3).getId() == 5L);
    }

    /**
     * None of the feeds have refresh times of under 5 min. No results.
     */
    @Test
    public void testExecuteAtTime1005()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1005);
        List<Feed> results = sut.execute(request);

        assertTrue(results.size() == 0);
    }

}
