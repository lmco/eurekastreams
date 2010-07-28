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
     * Classic interpolation search, but list is expected to be sorted descending, instead of ascending.
     * 
     * @param sorted
     *            sorted list.
     * @param toFind
     *            the item to find.
     * @return true toFind exists in the sorted list, false otherwise.
     */
    private boolean contains(final List<Long> sorted, final Long toFind)
    {
        if (sorted.size() == 0)
        {
            return false;
        }
        else if (sorted.size() == 1)
        {
            return toFind.equals(sorted.get(0));
        }

        int low = 0;
        int high = sorted.size() - 1;
        int mid;

        while (sorted.get(low) >= toFind && sorted.get(high) <= toFind)
        {
            mid = (int) (low + ((toFind - sorted.get(low)) * (high - low)) / (sorted.get(high) - sorted.get(low)));

            if (sorted.get(mid) < toFind)
            {
                high = mid - 1;
            }
            else if (sorted.get(mid) > toFind)
            {
                low = mid + 1;
            }
            else
            {
                return true;
            }
        }

        return (sorted.get(high) == toFind);
    }
}
