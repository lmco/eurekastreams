/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Fetcher that's given all of its IDs.
 */
public class ListWrappingPageFetcher implements PageFetcher<Long>
{
    /**
     * The wrapped list.
     */
    private List<Long> wrappedList;

    /**
     * Constructor - taking the wrapped list.
     * 
     * @param inWrappedList
     *            the list to wrap paging around
     */
    public ListWrappingPageFetcher(final List<Long> inWrappedList)
    {
        wrappedList = inWrappedList;
    }

    /**
     * Fetch a page by returning a page from the wrapped list.
     * 
     * @param inStartIndex
     *            the starting index
     * @param inPageSize
     *            the size of the page to fetch
     * @return a sublist starting at inStartIndex, with page size of inPageSize
     */
    @Override
    public List<Long> fetchPage(final int inStartIndex, final int inPageSize)
    {
        // note: the toIndex is not included, which is why we don't need a -1
        int toIndex = inStartIndex + inPageSize;
        if (inStartIndex > wrappedList.size())
        {
            return new ArrayList<Long>();
        }
        if (toIndex > wrappedList.size())
        {
            toIndex = wrappedList.size();
        }
        return wrappedList.subList(inStartIndex, toIndex);
    }

}
