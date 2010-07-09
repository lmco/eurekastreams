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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetCommentsById.
 *
 */
public class GetCommentsByIdTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetCommentsById sut;

    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;

    /**
     * test execute method.
     */
    @Test
    public void testExecute()
    {
        String comment1key = CacheKeys.COMMENT_BY_ID + 1;
        String comment2key = CacheKeys.COMMENT_BY_ID + 2;
        String comment3key = CacheKeys.COMMENT_BY_ID + 3;

        final List<Long> params = new ArrayList<Long>(3);
        params.add(1L);
        params.add(3L);
        params.add(2L);

        // assert that cache is empty for items of interest.
        assertNull(memcachedCache.get(comment1key));
        assertNull(memcachedCache.get(comment2key));
        assertNull(memcachedCache.get(comment3key));

        List<CommentDTO> results = sut.execute(params);
        assertEquals(3, results.size());

        // assert results are sorted desc by commentId
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
        assertEquals(3L, results.get(2).getId());

        // verify that items now in cache under correct key
        assertNotNull(memcachedCache.get(comment1key));
        assertNotNull(memcachedCache.get(comment2key));
        assertNotNull(memcachedCache.get(comment3key));
    }

    /**
     * Test execute method with null and empty param lists.
     */
    @Test
    public void testExecuteNullEmptyParamList()
    {
        List<CommentDTO> results = sut.execute(null);
        assertEquals(0, results.size());

        results = sut.execute(new ArrayList<Long>(0));
        assertEquals(0, results.size());
    }

}
