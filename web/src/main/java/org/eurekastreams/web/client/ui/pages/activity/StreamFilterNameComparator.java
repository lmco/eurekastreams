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
package org.eurekastreams.web.client.ui.pages.activity;

import java.util.Comparator;

import org.eurekastreams.server.domain.stream.StreamFilter;

/**
 * Comparator for alphabetic sorting of StreamFilters by name.
 */
public class StreamFilterNameComparator implements Comparator<StreamFilter>
{
    /**
     * Compare two StreamFilters based on their names.
     *
     * @param inFilterA
     *            the first filter to compare
     * @param inFilterB
     *            the second filter to compare
     * @return < 0 if inFilterA's name belongs above inFilterB's name in the sorted list, > 0 if inFilterA's name
     *         belongs above inFilterB's name in the sorted list, or 0 if equal
     */
    public int compare(final StreamFilter inFilterA, final StreamFilter inFilterB)
    {
        return inFilterA.getName().compareToIgnoreCase(inFilterB.getName());
    }
}
