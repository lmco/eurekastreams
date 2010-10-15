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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetCommentsByIdsDbMapper.
 * 
 */
public class GetCommentsByIdsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetCommentsByIdsDbMapper sut;

    /**
     * test execute method.
     */
    @Test
    public void testExecute()
    {
        List<Long> params = new ArrayList<Long>(Arrays.asList(1L, 3L, 2L));

        List<CommentDTO> results = sut.execute(params);
        assertEquals(3, results.size());

        // assert results are sorted by request id order
        assertEquals(1L, results.get(0).getId());
        assertEquals(3L, results.get(1).getId());
        assertEquals(2L, results.get(2).getId());

        // change order and try again
        params = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));

        results = sut.execute(params);
        assertEquals(3, results.size());

        // assert results are sorted by request id order
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
        assertEquals(3L, results.get(2).getId());

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
