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
 * An interface to be implemented by any object desiring to be a pager object. A pager object will keep the state of the
 * page location in a result set.
 * 
 * 
 */
public interface Pager
{

    /**
     * Tells this command which direction to move for next page.
     */
    public static enum PagingDirection
    {
        /**
         * Forward direction for paging.
         */
        FORWARD,
        /**
         * No direction for paging.
         */
        NONE,
        /**
         * Backward direction for paging.
         */
        BACKWARD
    };

    /**
     * for bean attribute.
     * 
     * @return start item.
     */
    int getStartItem();

    /**
     * for bean attribute.
     * 
     * @return end item.
     */
    int getEndItem();

    /**
     * for bean attribute.
     * 
     * @return max count.
     */
    int getMaxCount();

    /**
     * for bean attribute.
     * 
     * @return page size.
     */
    int getPageSize();

    /**
     * for bean attribute.
     * 
     * @param startItem
     *            start item range number.
     * @param endItem
     *            end item range number.
     * @param maxCount
     *            total number of items.
     */
    void updateRange(int startItem, int endItem, int maxCount);

    /**
     * Calculates the start and end bounds for the next page based on page size (number of displayable items).
     */
    void nextPage();

    /**
     * Calculates the start and end bounds for the previous page based on page size (number of displayable items).
     */
    void previousPage();

    /**
     * returns current range.
     * 
     */
    void samePage();

    /**
     * if the page can be moved ahead.
     * 
     * @return boolean.
     */
    boolean isNextPageable();

    /**
     * if the page can be moved back.
     * 
     * @return boolean.
     */
    boolean isPreviousPageable();
}
