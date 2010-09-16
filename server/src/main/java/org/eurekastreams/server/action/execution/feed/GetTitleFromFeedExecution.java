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
package org.eurekastreams.server.action.execution.feed;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.FeedFactory;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Gets the title from a feed. Basically is checking to make sure the feed exists and lets the user see its title.
 *
 */
public class GetTitleFromFeedExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Feed fetcher factory, really only needed for testing.
     */
    private FeedFactory feedFetcherFactory;

    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * Default constructor.
     *
     * @param inFeedFetcherFactory
     *            feed fetcher factory, basically just here for testing purposes.
     */
    public GetTitleFromFeedExecution(final FeedFactory inFeedFetcherFactory)
    {
        feedFetcherFactory = inFeedFetcherFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String feedUrlText = (String) inActionContext.getParams();
        try
        {
            new URL(feedUrlText);
        }
        catch (MalformedURLException e)
        {
            logger.error("Error fetching feed");
            throw new ExecutionException(e);
        }

        // fetch the feeds; use the first result since there should be exactly one
        try
        {
            Map<String, SyndFeed> synFeeds =
                    feedFetcherFactory.getSyndicatedFeed(feedUrlText, Collections.singletonList(inActionContext
                            .getPrincipal().getAccountId()));
            SyndFeed synFeed = synFeeds.values().iterator().next();
            return synFeed.getTitle();
        }
        catch (Exception e)
        {
            throw new ExecutionException(e);
        }
    }
}
