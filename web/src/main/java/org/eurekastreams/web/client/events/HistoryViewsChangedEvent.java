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
package org.eurekastreams.web.client.events;

import java.util.List;

/**
 * History views have changed, page has stayed the same.
 */
public class HistoryViewsChangedEvent
{
    /**
     * The views.
     */
    private List<String> views;

    /**
     * Constructor.
     * 
     * @param inViews
     *            the new views.
     */
    public HistoryViewsChangedEvent(final List<String> inViews)
    {
        views = inViews;
    }

    /**
     * Get the changed views.
     * 
     * @return the views.
     */
    public List<String> getViews()
    {
        return views;
    }

}
