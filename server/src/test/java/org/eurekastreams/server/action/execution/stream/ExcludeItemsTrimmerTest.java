/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests ExcludeItemsTrimmer.
 */
public class ExcludeItemsTrimmerTest
{
    /**
     * Tests trimming.
     */
    @Test
    public void test()
    {
        ExcludeItemsTrimmer sut = new ExcludeItemsTrimmer(Arrays.asList(1L, 7L, 4L));

        List<Long> ids = sut.trim(Arrays.asList(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L), null);

        assertEquals(6, ids.size());
        assertEquals((Long) 9L, ids.get(0));
        assertEquals((Long) 8L, ids.get(1));
        assertEquals((Long) 6L, ids.get(2));
        assertEquals((Long) 5L, ids.get(3));
        assertEquals((Long) 3L, ids.get(4));
        assertEquals((Long) 2L, ids.get(5));
    }
}
