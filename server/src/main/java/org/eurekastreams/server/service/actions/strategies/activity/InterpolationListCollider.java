package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Collides (AND) two lists using Interpolation Search to find common items.
 */
public class InterpolationListCollider implements ListCollider
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
    public List<Long> collide(final List<Long> sorted, final List<Long> unsorted)
    {
        final ArrayList<Long> commonItems = new ArrayList<Long>();

        for (Long item : unsorted)
        {
            if (contains(sorted, item))
            {
                commonItems.add(item);
            }
        }

        return commonItems;
    }

    /**
     * Classis interpolation search, but list is expected to be sorted descending, instead of ascending.
     * 
     * @param sorted
     *            sorted list.
     * @param toFind
     *            the item to find.
     * @return true toFind exists in the sorted list, false otherwise.
     */
    private boolean contains(final List<Long> sorted, final Long toFind)
    {
        int low = 0;
        int high = sorted.size() - 1;
        int mid;

        while (sorted.get(low) >= toFind && sorted.get(high) <= toFind)
        {
            mid = (int) (low + ((toFind - sorted.get(low)) * (high - low)) / (sorted.get(high) - sorted.get(low)));

            if (sorted.get(mid) < toFind)
                high = mid - 1;
            else if (sorted.get(mid) > toFind)
                // Repetition of the comparison code is forced by syntax limitations.
                low = mid + 1;
            else
                return true;
        }

        if (sorted.get(high) == toFind)
            return true;
        else
            return false; // Not found

    }
}
