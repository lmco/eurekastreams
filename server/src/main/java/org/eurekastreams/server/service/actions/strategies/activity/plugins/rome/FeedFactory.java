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

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
 * Create a FeedFetcher object, needed for testing, but probably a good practice either way.
 *
 */
public class FeedFactory
{

    /**
     * Logger.
     */
    private Log logger = LogFactory.getLog(FeedFactory.class);

    /**
     * List of strategies.
     */
    List<PluginFeedFetcherStrategy> siteStratagies;

    /**
     * @param inSiteStratagies
     *            the list of strategies.
     */
    public FeedFactory(final List<PluginFeedFetcherStrategy> inSiteStratagies)
    {
        siteStratagies = inSiteStratagies;
    }

    /**
     * Gets the Syndicated Feed.
     *
     * @param inFeedURL
     *            the url.
     * @return the feed fetcher.
     * @throws Exception
     *             if errror occurs.
     */
    public SyndFeed getSyndicatedFeed(final URL inFeedURL) throws Exception
    {
        for (PluginFeedFetcherStrategy ps : siteStratagies)
        {
            if (Pattern.compile(ps.getSiteUrlRegEx()).matcher(inFeedURL.toString()).find())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Feed being fetched from special site" + inFeedURL);
                }

                return ps.execute(inFeedURL);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Feed being fetched from normal site" + inFeedURL);
        }

        FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();

        FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);

        return feedFetcher.retrieveFeed(inFeedURL);

    }

}
