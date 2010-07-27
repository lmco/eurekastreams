package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.List;

/**
 * Does an AND on two lists and returns the results. One list is expected to be sorted, the other is not.
 */
public interface ListCollider
{
    /**
     * Collide (AND) two lists and return the results.
     * 
     * Behavior is undefined if sorted list is unsorted. Unchecked in method for performance reasons.
     * 
     * @param sorted
     *            sorted list.
     * @param unsorted
     *            unsorted list.
     * @return common items.
     */
    public List<Long> collide(final List<Long> sorted, final List<Long> unsorted);
}
