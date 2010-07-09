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

import org.eurekastreams.server.domain.stream.StreamSearch;


/**
 * User switched to a saved search.
 *
 */
public class SwitchedToSavedSearchEvent
{
    /**
     * Gets an instance of the event.
     * @return the event.
     */
    public static SwitchedToSavedSearchEvent getEvent()
    {
        return new SwitchedToSavedSearchEvent(null);
    }
    
    /**
     * The new search.
     */
    private StreamSearch search;
    
    /**
     * Default constructor.
     * @param inSearch the new search.
     */
    public SwitchedToSavedSearchEvent(final StreamSearch inSearch)
    {
        search = inSearch;
    }
    
    /**
     * Returns the search.
     * @return the search.
     */
    public StreamSearch getSearch()
    {
        return search;
    }
}
