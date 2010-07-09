/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.directory;

import java.util.ArrayList;

import org.apache.lucene.search.Sort;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.domain.ResourceSortCriterion;
import org.eurekastreams.server.domain.ResourceSortCriterion.SortDirection;

/**
 * Builder of Sort.
 */
public class SortFieldBuilder
{
    /**
     * If set, the sort will always use this criteria.
     */
    private ResourceSortCriteria overridingSortCriteria;

    /**
     * Get the SortField[] for the input ResourceSortCriteria.
     * 
     * @param inSortCriteria
     *            the sort criteria to use
     * @return the Sort object describing the sorting
     */
    public Sort getSort(final ResourceSortCriteria inSortCriteria)
    {
        ResourceSortCriteria sortCriteria = overridingSortCriteria != null ? overridingSortCriteria : inSortCriteria;

        ArrayList<org.apache.lucene.search.SortField> sortFields = new ArrayList<org.apache.lucene.search.SortField>();
        for (ResourceSortCriterion criterion : sortCriteria.getCriteria())
        {
            boolean reverse = criterion.getSortDirection() == SortDirection.DESCENDING;
            switch (criterion.getSortField())
            {
            case DATE_ADDED:
                sortFields.add(new org.apache.lucene.search.SortField("dateAdded", reverse));
                break;
            case FOLLOWERS_COUNT:
                sortFields.add(new org.apache.lucene.search.SortField("followersCount", reverse));
                break;
            case UPDATES_COUNT:
                sortFields.add(new org.apache.lucene.search.SortField("updatesCount", reverse));
                break;
            default:
                // fall back on name
                sortFields.add(new org.apache.lucene.search.SortField("byName", reverse));
            }
        }
        return new Sort(sortFields.toArray(new org.apache.lucene.search.SortField[sortFields.size()]));
    }

    /**
     * Set the sort criteria to use, overriding the requested sort.
     * 
     * @param inOverridingSortCriteria
     *            the overridingSortCriteria to set
     */
    public void setOverridingSortCriteria(final ResourceSortCriteria inOverridingSortCriteria)
    {
        this.overridingSortCriteria = inOverridingSortCriteria;
    }
}
