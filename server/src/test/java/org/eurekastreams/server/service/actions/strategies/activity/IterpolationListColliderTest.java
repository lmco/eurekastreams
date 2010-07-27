package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * A collection of tests that test the Interpolation List Collider against the Intersect method in Apache Commons.
 */
public class IterpolationListColliderTest
{
    /**
     * System under test.
     */
    final static ListCollider collider = new InterpolationListCollider();

    /**
     * Test collision miss where item is too high.
     */
    @Test
    public final void testCollisionMissHigh()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 6L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision miss where item is too low.
     */
    @Test
    public final void testCollisionMissLow()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 0L };

        collideTest(sorted, unsorted);
    }

    /**
     * Simple Test collision.
     */
    @Test
    public final void testCollision()
    {
        Long[] sorted = { 5L, 4L, 3L, 2L, 1L };
        Long[] unsorted = { 8L, 0L, 3L, 5L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding same lists.
     */
    @Test
    public final void testCollisionSameList()
    {
        Long[] sorted = { 5L, 4L, 3L, 2L, 1L };
        Long[] unsorted = sorted;

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision where the item misses in the middle of the list.
     */
    @Test
    public final void testCollisionSingleMiss()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 3L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision more complex.
     */
    @Test
    public final void testCollisionSparseList()
    {
        Long[] sorted = { 1000L, 900L, 899L, 898L, 897L, 896L, 895L, 799L, 501L, 500L, 499L, 5L, 4L, 3L, 2L, 1L };
        Long[] unsorted = { 1L, 1000L, 600L, 502L, 3L, 2L, 2500L, 999L, 899L, 895L, 894L, 900L, 901L };

        collideTest(sorted, unsorted);
    }

    /**
     * Helpder method, tests against apache commons intersection.
     * 
     * @param sorted
     *            the sorted list.
     * @param unsorted
     *            the unsorted list.
     */
    @SuppressWarnings("unchecked")
    private static final void collideTest(Long[] sorted, Long[] unsorted)
    {
        List<Long> sortedList = Arrays.asList(sorted);
        List<Long> unsortedList = Arrays.asList(unsorted);

        Collection<Long> expected = CollectionUtils.intersection(sortedList, unsortedList);

        List<Long> actual = collider.collide(sortedList, unsortedList);

        Assert.assertEquals(expected.size(), actual.size());

        for (Long expectedItem : expected)
        {
            Assert.assertTrue(actual.contains(expectedItem));
        }
    }
}
