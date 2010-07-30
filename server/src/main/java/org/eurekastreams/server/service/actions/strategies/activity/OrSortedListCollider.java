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
 * Collides (OR) two lists that are both sorted.
 */
public class OrSortedListCollider implements ListCollider
{
    /**
     * Collides (OR) two lists that are both sorted.
     * 
     * @param listA
     *            sorted list.
     * @param listB
     *            sorted list.
     * @param maxResults
     *            the max results to find.
     * @return OR of items.
     */
    public List<Long> collide(final List<Long> listA, final List<Long> listB, final int maxResults)
    {
        if (listA.size() == 0)
        {
            return listB;
        }
        else if (listB.size() == 0)
        {
            return listA;
        }

        List<Long> results = new ArrayList<Long>();

        int aIndex = 0;
        int bIndex = 0;

        while (results.size() < maxResults && (aIndex < listA.size() || bIndex < listB.size()))
        {
            if (bIndex >= listB.size() || (aIndex < listA.size() && listA.get(aIndex) > listB.get(bIndex)))
            {
                results.add(listA.get(aIndex));
                aIndex++;
            }
            else
            {
                results.add(listB.get(bIndex));
                bIndex++;
            }
        }

        return results;
    }
}
