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
package org.eurekastreams.web.client.events;

/**
 * Event telling observers that a theme category was selected.
 */
public final class ThemeCategorySelectedEvent
{
    /**
     * The selected category.
     */
    private final String category;

    /**
     * Gets the event.
     * @return the event.
     */
    public static ThemeCategorySelectedEvent getEvent()
    {
        return new ThemeCategorySelectedEvent(null);
    }

    /**
     * @param inCategory the category that was selected
     */
    public ThemeCategorySelectedEvent(final String inCategory)
    {
        category = inCategory;
    }

    /**
     * @return the selected category
     */
    public String getCategory()
    {
        return category;
    }
}
