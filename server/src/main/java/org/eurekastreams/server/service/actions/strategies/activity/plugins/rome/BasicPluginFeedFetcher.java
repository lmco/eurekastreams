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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Fetches a feed for the stream plugin framework, using optional HTTP headers.
 */
public class BasicPluginFeedFetcher implements PluginFeedFetcherStrategy
{
    /** The Site Url's Regular Expression. */
    private String siteUrlRegEx;

    /** HTTP headers to add to the request. */
    private Map<String, String> httpHeaders;

    /** Fetcher for feeds. */
    private FeedFetcher fetcher;

    /**
     * Constructor for default use.
     *
     * @param inFetcher
     *            Fetcher for feeds.
     */
    public BasicPluginFeedFetcher(final FeedFetcher inFetcher)
    {
        this(inFetcher, null, Collections.EMPTY_MAP);
    }

    /**
     * Constructor.
     *
     * @param inFetcher
     *            Fetcher for feeds.
     * @param inSiteUrlRegEx
     *            The url RegEx.
     * @param inHttpHeaders
     *            HTTP headers to add to the request.
     */
    public BasicPluginFeedFetcher(final FeedFetcher inFetcher, final String inSiteUrlRegEx,
            final Map<String, String> inHttpHeaders)
    {
        fetcher = inFetcher;
        siteUrlRegEx = inSiteUrlRegEx;
        httpHeaders = inHttpHeaders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSiteUrlRegEx()
    {
        return siteUrlRegEx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SyndFeed> execute(final String inFeedUrl, final Collection<String> inRequestors,
            final String inProxyHost, final String inProxyPort, final int inTimeout) throws Exception
    {
        return Collections.singletonMap(null, fetcher.fetchFeed(inFeedUrl, httpHeaders, inProxyHost, inProxyPort,
                inTimeout));
    }
}
