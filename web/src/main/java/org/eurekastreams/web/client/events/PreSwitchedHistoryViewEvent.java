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

import java.util.List;

import org.eurekastreams.server.domain.Page;

/**
 * Occurs before the real history has been swtiched.
 * Allows for developers to prep for a history change or
 * alert the user of something before they navigate away
 * Or cancel the navigation if need be.
 *
 */
public class PreSwitchedHistoryViewEvent
{
    /**
     * The page (e.g. gallery).
     */
    private Page page;
    /**
     * Views.
     */
    private List<String> views;


    /**
     * Default constructor.
     * @param inPage page.
     * @param inViews views.
     */
    public PreSwitchedHistoryViewEvent(final Page inPage, final List<String> inViews)
    {
        page = inPage;
        views = inViews;
    }

    /**
     * Get the page.
     * @return the apge.
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * Get the views.
     * @return the views.
     */
    public List<String> getViews()
    {
        return views;
    }
}
