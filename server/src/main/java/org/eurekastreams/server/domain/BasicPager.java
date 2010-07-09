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
package org.eurekastreams.server.domain;


/**
 * This class keeps the page state for a view & controller. The page state is modified accordingly based on the paging
 * action taken. Page state represents the result set currently viewed. Each set of results has a start point, an end
 * point, total items, and range (or page size).
 * 
 * 
 */
public class BasicPager implements Pager
{
    /**
     * the number of items to view at one time.
     */
    private int pageSize = 9 + 1;

    /**
     * Goes by index.
     */
    private int startItem = 0;

    /**
     * Goes by index.
     */
    private int endItem = 9;

    /**
     * the total number of things that can be paged around. The max usable index should be maxCount-1
     */
    private int maxCount = 0;

    /**
     * Constructor.
     */
    public BasicPager()
    {

    }

    /**
     * for bean attribute.
     * 
     * @return the pageSize
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * for bean attribute.
     * 
     * @param inPageSize
     *            the pageSize to set
     */
    public void setPageSize(final int inPageSize)
    {
        this.pageSize = inPageSize;
    }

    /**
     * for bean attribute.
     * 
     * @return the startItem
     */
    public int getStartItem()
    {
        return startItem;
    }

    /**
     * for bean attribute.
     * 
     * @param inStartItem
     *            the startItem to set
     */
    public void setStartItem(final int inStartItem)
    {
        this.startItem = inStartItem;
    }

    /**
     * @param inEndItem
     *            the endItem to set
     */
    public void setEndItem(final int inEndItem)
    {
        this.endItem = inEndItem;
    }

    /**
     * index, inclusive.
     * 
     * @return the endItem
     */
    public int getEndItem()
    {
        return endItem;
    }

    /**
     * for bean attribute.
     * 
     * @return the maxCount
     */
    public int getMaxCount()
    {
        return maxCount;
    }

    /**
     * for bean attribute.
     * 
     * @param inMaxCount
     *            the maxCount to set
     */
    public void setMaxCount(final int inMaxCount)
    {
        this.maxCount = inMaxCount;
    }

    /**
     * Calculates the start and end bounds for the next page based on page size (number of displayable items).
     */
    public void nextPage()
    {
        // don't do anything if you'd page past the available items
        if (startItem + pageSize >= maxCount)
        {
            return;
        }

        // make the change
        startItem += pageSize;
        endItem += pageSize;

        // correct for right edge case if necessary
        if (endItem >= maxCount)
        {
            endItem = maxCount - 1;
        }

    }

    /**
     * returns current range.
     * 
     * This method is here for symmetry with the other paging methods to be able to express your intent that you want to
     * stay on the same page.
     * 
     */
    public void samePage()
    {

    }

    /**
     * Calculates the start and end bounds for the previous page based on page size (number of displayable items).
     */
    public void previousPage()
    {
        // don't do anything if you'd page past the available items
        if (startItem - pageSize < 0)
        {
            return;
        }

        // make the change
        endItem = startItem - 1;
        startItem -= pageSize;
    }

    /**
     * Change the page.
     * 
     * @param direction
     *            the direction to change the page.
     */
    public void changePage(final PagingDirection direction)
    {
        if (direction.equals(PagingDirection.FORWARD))
        {
            nextPage();
        }
        else if (direction.equals(PagingDirection.BACKWARD))
        {
            previousPage();
        }
        else
        {
            samePage();
        }

    }

    /**
     * Determine if the pager is pageable in a given direction.
     * 
     * @param direction
     *            the direction.
     * @return if the pager has another page.
     */
    public boolean isPageable(final PagingDirection direction)
    {
        if (direction.equals(PagingDirection.FORWARD))
        {
            return isNextPageable();
        }
        else if (direction.equals(PagingDirection.BACKWARD))
        {
            return isPreviousPageable();
        }

        // "Same Page" case.
        return true;
    }

    /**
     * Shorthand for setting page location.
     * 
     * @param inStartItem
     *            start item for a range of data.
     * @param inEndItem
     *            end item for a range of data.
     * @param inMaxCount
     *            total number of items in the result set (not the displayable range).
     */
    public void updateRange(final int inStartItem, final int inEndItem, final int inMaxCount)
    {
        this.startItem = inStartItem;
        this.endItem = inEndItem;
        this.maxCount = inMaxCount;
    }

    /**
     * if the page can be moved ahead.
     * 
     * @return if a next page exists.
     */
    public boolean isNextPageable()
    {
        return endItem < maxCount - 1; // less than the last index
    }

    /**
     * if the page can be moved back.
     * 
     * @return if a previous. page exists.
     */
    public boolean isPreviousPageable()
    {
        return startItem > 0;
    }

}
