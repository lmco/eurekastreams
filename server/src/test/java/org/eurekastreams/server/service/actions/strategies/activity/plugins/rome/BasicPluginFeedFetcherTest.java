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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Tests BasicPluginFeedFetcher.
 */
public class BasicPluginFeedFetcherTest
{
    /** Test data. */
    private static final String REGEX = "ThisIsTheRegex";

    /** Test data. */
    private static final String URL_TEXT = "http://eurekastreams.org";

    /** Test data. */
    private static final String PROXY_HOST = "ProxyHost";

    /** Test data. */
    private static final String PROXY_PORT = "ProxyPort";

    /** Test data. */
    private static final int TIMEOUT = 9 * 9 * 9;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private BasicPluginFeedFetcher sut;

    /** HTTP headers to add to the request. */
    private Map<String, String> httpHeaders = new HashMap<String, String>();

    /** Fixture: Fetcher for feeds. */
    private BasicFeedFetcher fetcher = context.mock(BasicFeedFetcher.class);

    /** Fixture: feed. */
    private SyndFeed feed = context.mock(SyndFeed.class, "feed");

    /** Fixture: user list. */
    private Collection<String> users;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        httpHeaders.clear();
        httpHeaders.put("HeaderName", "HeaderValue");

        users = new HashSet<String>();
        users.add("jdoe");
        users.add("smith");
    }

    /**
     * Tests getting regex.
     */
    @Test
    public void testGetSiteUrlRegEx()
    {
        sut = new BasicPluginFeedFetcher(fetcher, REGEX, Collections.EMPTY_MAP);
        assertEquals(REGEX, sut.getSiteUrlRegEx());
        context.assertIsSatisfied();
    }

    /**
     * Tests executing.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testExecute() throws Exception
    {
        sut = new BasicPluginFeedFetcher(fetcher, REGEX, httpHeaders);

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).fetchFeed(URL_TEXT, httpHeaders, PROXY_HOST, PROXY_PORT, TIMEOUT);
                will(returnValue(feed));
            }
        });

        Map<String, SyndFeed> result = sut.execute(URL_TEXT, users, PROXY_HOST, PROXY_PORT, TIMEOUT);
        context.assertIsSatisfied();

        assertEquals(1, result.size());
        assertSame(feed, result.get(null));
    }

    /**
     * Tests executing.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testExecuteDefault() throws Exception
    {
        sut = new BasicPluginFeedFetcher(fetcher);

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).fetchFeed(URL_TEXT, Collections.EMPTY_MAP, PROXY_HOST, PROXY_PORT, TIMEOUT);
                will(returnValue(feed));
            }
        });

        Map<String, SyndFeed> result = sut.execute(URL_TEXT, users, PROXY_HOST, PROXY_PORT, TIMEOUT);
        context.assertIsSatisfied();

        assertEquals(1, result.size());
        assertSame(feed, result.get(null));
    }

}
