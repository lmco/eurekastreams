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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests collection combiner.
 */
public class CollectionCombinerTest
{
    /**
     * System under test.
     */
    private static CollectionCombiner sut;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new CollectionCombiner();
    }

    /**
     * Tests combining lists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCombine()
    {
        Collection<Long> listA = Arrays.asList(1L, 2L);
        Collection<Long> listB = Arrays.asList(3L, 4L);
        
        Collection<Long> results = sut.combine(listA, listB);
        
        Assert.assertEquals(4, results.size());
        Assert.assertTrue(results.contains(1L));
        Assert.assertTrue(results.contains(2L));
        Assert.assertTrue(results.contains(3L));
        Assert.assertTrue(results.contains(4L));
    }
}
