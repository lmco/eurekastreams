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
package org.eurekastreams.web.client.events;

import java.util.ArrayList;

/**
 * Got popular hash tags event.
 */
public class GotStreamPopularHashTagsEvent
{
    /**
     * Popular hash tags.
     */
    private ArrayList<String> popularHashTags;

    /**
     * Constructor.
     * 
     * @param inPopularHashTags
     *            the popular hash tags.
     */
    public GotStreamPopularHashTagsEvent(final ArrayList<String> inPopularHashTags)
    {
        popularHashTags = inPopularHashTags;
    }

    /**
     * Get the popular hash tags.
     * 
     * @return the popular hash tags.
     */
    public ArrayList<String> getPopularHashTags()
    {
        return popularHashTags;
    }
}
