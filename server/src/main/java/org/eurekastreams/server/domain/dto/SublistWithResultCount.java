/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Container for a sublist with the total count.
 * 
 * @param <ElementType>
 *            the type of list element
 */
public class SublistWithResultCount<ElementType> implements Serializable
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 8459267810903607142L;

    /**
     * The total number of results.
     */
    private Long totalResultsCount;

    /**
     * The sublist.
     */
    private List<ElementType> resultsSublist;

    /**
     * Empty constructor for serialization.
     */
    public SublistWithResultCount()
    {
    }

    /**
     * Constructor.
     * 
     * @param inResultsSublist
     *            the sublist
     * @param inTotalResultsCount
     *            the total number of results
     */
    public SublistWithResultCount(final List<ElementType> inResultsSublist, final Long inTotalResultsCount)
    {
        resultsSublist = inResultsSublist;
        totalResultsCount = inTotalResultsCount;
    }

    /**
     * @return the totalResultsCount
     */
    public Long getTotalResultsCount()
    {
        return totalResultsCount;
    }

    /**
     * @param inTotalResultsCount
     *            the totalResultsCount to set
     */
    public void setTotalResultsCount(final Long inTotalResultsCount)
    {
        totalResultsCount = inTotalResultsCount;
    }

    /**
     * @return the resultsSublist
     */
    public List<ElementType> getResultsSublist()
    {
        return resultsSublist;
    }

    /**
     * @param inResultsSublist
     *            the resultsSublist to set
     */
    public void setResultsSublist(final List<ElementType> inResultsSublist)
    {
        resultsSublist = inResultsSublist;
    }

}
