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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Create a FeedFetcher object, needed for testing, but probably a good practice either way.
 *
 */
public class FeedFactory
{
    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * List of strategies.
     */
    private List<PluginFeedFetcherStrategy> siteStratagies;

    /** Feed fetcher used when no other applies. */
    private PluginFeedFetcherStrategy defaultFetcher;

    /**
     * The connection timeout (in milliseconds).
     */
    private int timeout;

    /**
     * Proxy hostname.
     */
    private String proxyHost;

    /**
     * Proxy port.
     */
    private String proxyPort;

    /**
     * @param inSiteStratagies
     *            the list of strategies.
     * @param inDefaultFetcher
     *            Feed fetcher used when no other applies.
     * @param inProxyHost
     *            the http proxy hostname (if necessary).
     * @param inProxyPort
     *            the http proxy port.
     * @param inTimeout
     *            the connection timeout (in ms).
     */
    public FeedFactory(final List<PluginFeedFetcherStrategy> inSiteStratagies,
            final PluginFeedFetcherStrategy inDefaultFetcher, final String inProxyHost, final String inProxyPort,
            final int inTimeout)
    {
        siteStratagies = inSiteStratagies;
        defaultFetcher = inDefaultFetcher;
        proxyHost = inProxyHost;
        proxyPort = inProxyPort;
        timeout = inTimeout;
    }

    /**
     * Gets the Syndicated Feed.
     *
     * @param inFeedUrl
     *            the feed.
     * @param inRequestors
     *            List of people who requested the feed.
     * @return List of feeds by requestor: key is requestor (or null if anonymous), value is the feed.
     * @throws Exception
     *             if error occurs.
     */
    public Map<String, SyndFeed> getSyndicatedFeed(final String inFeedUrl, final Collection<String> inRequestors)
            throws Exception
    {
        for (PluginFeedFetcherStrategy ps : siteStratagies)
        {
            if (Pattern.compile(ps.getSiteUrlRegEx()).matcher(inFeedUrl.toString()).find())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Feed being fetched from special site " + inFeedUrl);
                }

                return ps.execute(inFeedUrl, inRequestors, proxyHost, proxyPort, timeout);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Feed being fetched from normal site " + inFeedUrl);
        }

        return defaultFetcher.execute(inFeedUrl, inRequestors, proxyHost, proxyPort, timeout);
    }
}
