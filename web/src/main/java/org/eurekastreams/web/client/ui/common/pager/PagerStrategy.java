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
package org.eurekastreams.web.client.ui.common.pager;

/**
 * Pager Strategy.
 */
public interface PagerStrategy
{
	/**
	 * 
	 * @return the amount per page.
	 */
	int getEndIndex();
	/**
	 * 
	 * @return the start index.
	 */
	int getStartIndex();
	/**
	 * 
	 * @return the total number of things.
	 */
	int getTotal();
    /**
     * @return if there is a next page.
     */
    boolean hasNext();

    /**
     * @return if there is a previous page.
     */
    boolean hasPrev();

    /**
     * Page next.
     */
    void next();

    /**
     * Page prev.
     */
    void prev();

    /**
     * Initialize.
     */
    void init();

    /**
     * Get the event.
     * 
     * @return the event key.
     */
    String getKey();
}
