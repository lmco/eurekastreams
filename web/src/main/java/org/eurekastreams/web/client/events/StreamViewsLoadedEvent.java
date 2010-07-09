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

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamFilter;

/**
 * The users stream views have been loaded.
 *
 */
public class StreamViewsLoadedEvent
{
    /**
     * Gets an instance of the event.
     * 
     * @return the event.
     */
    public static StreamViewsLoadedEvent getEvent()
    {
        return new StreamViewsLoadedEvent(null);
    }

    /**
     * The new view.
     */
    private List<StreamFilter> views;

    /**
     * Default constructor.
     * 
     * @param inViews
     *            the new view.
     */
    public StreamViewsLoadedEvent(final List<StreamFilter> inViews)
    {
        views = inViews;
    }

    /**
     * Returns the view.
     * 
     * @return the view.
     */
    public List<StreamFilter> getViews()
    {
        return views;
    }
}
