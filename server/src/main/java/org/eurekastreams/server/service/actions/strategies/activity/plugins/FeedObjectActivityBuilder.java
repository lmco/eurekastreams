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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.plugins.Feed;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Interface for strategies which participate in building up an activity from a feed entry.
 */
public interface FeedObjectActivityBuilder
{
    /**
     * Performs all/part of building the given activity from a feed entry.
     *
     * @param feed
     *            Definition of the feed from which the entry was taken.
     * @param entry
     *            Feed entry.
     * @param activity
     *            Activity being populated.
     */
    void build(Feed feed, SyndEntry entry, Activity activity);
}

