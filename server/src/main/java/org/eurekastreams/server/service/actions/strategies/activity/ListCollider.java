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

import java.util.List;

/**
 * Does an AND on two lists and returns the results. One list is expected to be sorted, the other is not.
 */
public interface ListCollider
{
    /**
     * Collide (AND) two lists and return the results.
     * 
     * Behavior is undefined if sorted list is unsorted. Unchecked in method for performance reasons.
     * 
     * @param listA
     *            a list.
     * @param listB
     *            b list.
     * @param maxResults
     *            max results to return.
     * @return common items.
     */
    List<Long> collide(final List<Long> listA, final List<Long> listB, final int maxResults);
}
