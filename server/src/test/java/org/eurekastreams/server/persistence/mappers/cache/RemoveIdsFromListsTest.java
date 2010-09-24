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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests RemoveIdsFromLists class.
 */
public class RemoveIdsFromListsTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    RemoveIdsFromLists sut;

    /**
     * Tests execute method.
     */
    @Test
    public void testExecute()
    {
        final String cacheKey1 = "FOO" + 1;
        final String cacheKey2 = "FOO" + 2;
        final String cacheKey3 = "FOO" + 3;

        final List<Long> cacheValues1 = Arrays.asList(1L, 2L, 3L);
        final List<Long> cacheValues2 = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        final List<Long> cacheValues3 = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);

        getCache().setList(cacheKey1, cacheValues1);
        getCache().setList(cacheKey2, cacheValues2);
        getCache().setList(cacheKey3, cacheValues3);

        assertEquals(3, getCache().getList(cacheKey1).size());
        assertEquals(6, getCache().getList(cacheKey2).size());
        assertEquals(9, getCache().getList(cacheKey3).size());

        List<String> keys = Arrays.asList(cacheKey1, cacheKey2, cacheKey3);
        List<Long> values = Arrays.asList(1L, 2L, 3L);
        DeleteIdsFromListsRequest request = new DeleteIdsFromListsRequest(keys, values);

        sut.execute(request);

        assertEquals(0, getCache().getList(cacheKey1).size());
        assertEquals(3, getCache().getList(cacheKey2).size());
        assertEquals(6, getCache().getList(cacheKey3).size());
    }
}
