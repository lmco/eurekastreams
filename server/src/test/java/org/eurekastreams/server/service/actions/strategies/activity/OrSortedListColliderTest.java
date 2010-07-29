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
     * Test a collision with empty lists.
     */
    @Test
    public final void testCollisionEmpty()
    {
        Long[] a = { };
        Long[] b = { };

        collideTest(a, b, 1);
    }

    /**
     * Test a collision with the left-hand list being empty.
     */
    @Test
    public final void testCollisionLeftEmpty()
    {
        Long[] a = { };
        Long[] b = { 1L };

        collideTest(a, b, 1);
    }

    /**
     * Test a collision with the right-hand list being empty.
     */
    @Test
    public final void testCollisionRightEmpty()
    {
        Long[] a = { 1L };
        Long[] b = { };

        collideTest(a, b, 1);
    }

    /**
     * Test a simple collision.
     */
    @Test
    public final void testCollisionSimple()
    {
        Long[] a = { 5L, 4L, 2L, 1L };
        Long[] b = { 6L };

        collideTest(a, b, 5);
    }

    /**
     * Test a simple collision.
     */
    @Test
    public final void testCollisionSimple2()
    {
        Long[] a = { 6L };
        Long[] b = { 5L, 4L, 2L, 1L };

        collideTest(a, b, 5);
    }

    /**
     * Test a simple collision with two lists of the same size.
     */
    @Test
    public final void testCollisionSameSize()
    {
        Long[] a = { 10L, 8L, 6L, 4L, 2L, 0L  };
        Long[] b = { 11L, 9L, 7L, 5L, 3L, 1L };

        collideTest(a, b, 12);
    }

    /**
     * Test a simple collision with two lists of the same size, reversed from other test.
     */
    @Test
    public final void testCollisionSameSizeReverse()
    {
        Long[] a = { 11L, 9L, 7L, 5L, 3L, 1L };
        Long[] b = { 10L, 8L, 6L, 4L, 2L, 0L  };

        collideTest(a, b, 12);
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
