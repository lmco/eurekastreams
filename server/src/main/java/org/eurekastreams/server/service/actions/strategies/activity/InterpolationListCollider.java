package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

public class InterpolationListCollider implements ListCollider
{
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
