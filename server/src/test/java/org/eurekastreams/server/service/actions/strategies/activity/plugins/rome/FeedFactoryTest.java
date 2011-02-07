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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Tests FeedFactory.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FeedFactoryTest
{
    /** Test data. */
    private static final String HOST = "Host";

    /** Test data. */
    private static final String PORT = "Port";

    /** Test data. */
    private static final int TIMEOUT = 42;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: plugin 1. */
    private final PluginFeedFetcherStrategy plugin1 = context.mock(PluginFeedFetcherStrategy.class, "plugin1");

    /** Fixture: plugin 2. */
    private final PluginFeedFetcherStrategy plugin2 = context.mock(PluginFeedFetcherStrategy.class, "plugin2");

    /** Fixture: default plugin. */
    private final PluginFeedFetcherStrategy defaultPlugin = context.mock(PluginFeedFetcherStrategy.class,
            "defaultPlugin");

    /** Fixture: plugin1's result. */

    private final Map pluginResult = context.mock(Map.class, "pluginResult");

    /** SUT. */
    private FeedFactory sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new FeedFactory(Arrays.asList(plugin1, plugin2), defaultPlugin, HOST, PORT, TIMEOUT);

        context.checking(new Expectations()
        {
            {
                allowing(plugin1).getSiteUrlRegEx();
                will(returnValue("someothersite.com"));
                allowing(plugin2).getSiteUrlRegEx();
                will(returnValue("example.com"));
            }
        });
    }

    /**
     * Tests executing where the URL matches one of the special plugins.
     *
     * @throws Exception
     *             Shouldn't.
     */

    @Test
    public void testSpecialPlugin() throws Exception
    {
        final Collection requestors = context.mock(Collection.class);
        final String url = "www.example.com/example.rss";

        context.checking(new Expectations()
        {
            {
                oneOf(plugin2).execute(url, requestors, HOST, PORT, TIMEOUT);
                will(returnValue(pluginResult));
            }
        });

        Map<String, SyndFeed> result = sut.getSyndicatedFeed(url, requestors);

        context.assertIsSatisfied();
        assertSame(pluginResult, result);
    }

    /**
     * Tests executing where the URL doesn't match any of the special plugins.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testDefaultPlugin() throws Exception
    {
        final Collection requestors = context.mock(Collection.class);
        final String url = "www.ordinary.com/rss.xml";

        context.checking(new Expectations()
        {
            {
                oneOf(defaultPlugin).execute(url, requestors, HOST, PORT, TIMEOUT);
                will(returnValue(pluginResult));
            }
        });

        Map<String, SyndFeed> result = sut.getSyndicatedFeed(url, requestors);

        context.assertIsSatisfied();
        assertSame(pluginResult, result);
    }

}
