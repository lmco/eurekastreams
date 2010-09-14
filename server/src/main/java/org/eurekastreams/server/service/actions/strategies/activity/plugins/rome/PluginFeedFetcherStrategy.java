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

package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * A strategy for Fetching a Plugin Feed.
 *
 */
public interface PluginFeedFetcherStrategy
{
    /**
     * @return the regExpression Being used to filter sites.
     */
    String getSiteUrlRegEx();

    /**
     * @param inFeedUrl
     *            the url to create a SyndFeed From.
     * @param inRequestors
     *            List of people who requested the feed.
     * @param inProxyHost
     *            host name to use (if desires) for proxying http requests.
     * @param inProxyPort
     *            port for http proxy server.
     * @param inTimeout
     *            the period of time to wait for a response from the feed.
     * @return Syndicated feeds: key is requestor (or null if anonymous), value is the feed.
     * @throws Exception
     *             if an error occurs.
     */
    Map<String, SyndFeed> execute(String inFeedUrl, List<String> inRequestors, String inProxyHost, String inProxyPort,
            int inTimeout) throws Exception;
}
