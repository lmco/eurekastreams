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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * A collection of tests that test the OR Sorted List Collider against the Union method in Apache Commons.
 */
public class OrSortedListColliderTest
{
    /**
     * System under test.
     */
    private static ListCollider collider = new OrSortedListCollider();

    /**
     * Collider test max results.
     */
    private static final int MAX_RESULTS_TWELVE = 12;
    
    /**
     * Collider test max results.
     */
    private static final int MAX_RESULTS_ONE = 1;
    
    /**
     * Collider test dataset id.
     */
    private static final Long ID_TEN = 10L;
    
    /**
     * Collider test dataset id.
     */
    private static final Long ID_ELEVEN = 11L;
    
    /**
     * Collider test max results.
     */
    public static final int MAX_RESULTS_FIVE = 5;
    
    /**
     * Test a collision with empty lists.
     */
    @Test
    public final void testCollisionEmpty()
    {
        Long[] a = { };
        Long[] b = { };

        collideTest(a, b, MAX_RESULTS_ONE);
    }

    /**
     * Test a collision with the left-hand list being empty.
     */
    @Test
    public final void testCollisionLeftEmpty()
    {
        Long[] a = { };
        Long[] b = { 1L };

        collideTest(a, b, MAX_RESULTS_ONE);
    }

    /**
     * Test a collision with the right-hand list being empty.
     */
    @Test
    public final void testCollisionRightEmpty()
    {
        Long[] a = { 1L };
        Long[] b = { };

        collideTest(a, b, MAX_RESULTS_ONE);
    }

    /**
     * Test a simple collision.
     */
    @Test
    public final void testCollisionSimple()
    {
        Long[] a = { 5L, 4L, 2L, 1L };
        Long[] b = { 6L };

        collideTest(a, b, MAX_RESULTS_FIVE);
    }

    /**
     * Test a simple collision.
     */
    @Test
    public final void testCollisionSimple2()
    {
        Long[] a = { 6L };
        Long[] b = { 5L, 4L, 2L, 1L };

        collideTest(a, b, MAX_RESULTS_FIVE);
    }

    /**
     * Test a simple collision with two lists of the same size.
     */
    @Test
    public final void testCollisionSameSize()
    {
        Long[] a = { ID_TEN, 8L, 6L, 4L, 2L, 0L  };
        Long[] b = { ID_ELEVEN, 9L, 7L, 5L, 3L, 1L };

        collideTest(a, b, MAX_RESULTS_TWELVE);
    }

    /**
     * Test a simple collision with two lists of the same size, reversed from other test.
     */
    @Test
    public final void testCollisionSameSizeReverse()
    {
        Long[] a = { ID_ELEVEN, 9L, 7L, 5L, 3L, 1L };
        Long[] b = { ID_TEN, 8L, 6L, 4L, 2L, 0L  };

        collideTest(a, b, MAX_RESULTS_TWELVE);
    }

    /**
     * Helper method, tests against apache commons union.
     * 
     * @param a
     *            a sorted list.
     * @param b
     *            a sorted list.
     * @param maxResults
     *            the max results.
     */
    @SuppressWarnings("unchecked")
    private static void collideTest(final Long[] a, final Long[] b, final int maxResults)
    {
        List<Long> listA = Arrays.asList(a);
        List<Long> listB = Arrays.asList(b);

        Collection<Long> expected = CollectionUtils.union(listA, listB);

        List<Long> actual = collider.collide(listA, listB, maxResults);

        Assert.assertEquals(expected.size(), actual.size());

        Assert.assertTrue(actual.size() <= maxResults);

        for (int i = 0; i < actual.size(); i++)
        {
            Long actualItem = actual.get(i);

            // Confirm sort order is correct
            if (i > 0)
            {
                Assert.assertTrue(actualItem <= actual.get(i - 1));
            }

            Assert.assertTrue(expected.contains(actualItem));
        }
    }

}
