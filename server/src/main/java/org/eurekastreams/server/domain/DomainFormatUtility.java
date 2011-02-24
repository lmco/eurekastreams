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
package org.eurekastreams.server.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for consistent handling of domain formatted data.
 */
public final class DomainFormatUtility
{
    /**
     * Constructor to prevent instantiation.
     */
    private DomainFormatUtility()
    {
    }

    /**
     * Stores the keywords provided as a list of capabilities.
     * 
     * @param keywordList
     *            Comma-separated keyword list
     * @return List of capabilities described by the keyword list.
     */
    public static List<BackgroundItem> splitCapabilitiesString(final String keywordList)
    {
        List<BackgroundItem> capabilities = new ArrayList<BackgroundItem>();
        if (keywordList != null && !keywordList.isEmpty())
        {
            // normalize whitespace and split discarding empty items
            String[] keywords = keywordList.replaceAll("\\s+", " ").trim().split("\\s*,(?:\\s*,)*\\s*");

            for (String keyword : keywords)
            {
                if (!keyword.isEmpty())
                {
                    capabilities.add(new BackgroundItem(keyword, BackgroundItemType.CAPABILITY));
                }
            }
        }
        return capabilities;
    }

    /**
     * Builds a displayable string (for edit boxes) for a list of background items.
     * 
     * @param items
     *            List of background items.
     * @return Displayable string.
     */
    public static String buildCapabilitiesString(final List<BackgroundItem> items)
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (BackgroundItem item : items)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                builder.append(", ");
            }
            builder.append(item.getName());
        }
        return builder.toString();
    }

    /**
     * Builds a displayable string (for edit boxes).
     * 
     * @param items
     *            List of background items.
     * @return Displayable string.
     */
    public static String buildCapabilitiesStringFromStrings(final List<String> items)
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String item : items)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                builder.append(", ");
            }
            builder.append(item);
        }
        return builder.toString();
    }

}
