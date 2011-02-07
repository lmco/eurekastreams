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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eurekastreams.server.service.utility.http.HttpDocumentFetcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

/**
 * Tests BasicFeedFetcher.
 */
public class BasicFeedFetcherTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: document fetcher. */
    private final HttpDocumentFetcher docFetcher = context.mock(HttpDocumentFetcher.class);

    /**
     * Tests fetching document.
     *
     * @throws SAXException
     *             Shouldn't.
     * @throws ParserConfigurationException
     *             Shouldn't.
     * @throws IOException
     *             Shouldn't.
     * @throws FeedException
     *             Shouldn't.
     */
    @Test
    public void test() throws IOException, ParserConfigurationException, SAXException, FeedException
    {
        final String url = "http://www.example.com/feed.xml";
        final String host = "Host";
        final String port = "Port";
        final int timeout = 42;

        BasicFeedFetcher sut = new BasicFeedFetcher(docFetcher);

        final Map headers = context.mock(Map.class);
        String documentText = "<rss version=\"2.0\"><channel><title>The Title</title>"
                + "<link>http://www.example.com</link>"
                + "<description>The Description</description><item><title>Item Title</title>"
                + "<link>http://www.example.com/entry/1</link><pubDate>Tue, 01 Feb 2011 01:01:01 +0000</pubDate>"
                + "</item></channel></rss>";
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(documentText)));

        context.checking(new Expectations()
        {
            {
                oneOf(docFetcher).fetchDocument(with(equal(url)), with(same(headers)), with(equal(host)),
                        with(equal(port)), with(equal(timeout)), with(any(DocumentBuilderFactory.class)));
                will(returnValue(doc));
            }
        });

        SyndFeed result = sut.fetchFeed(url, headers, host, port, timeout);

        context.assertIsSatisfied();
        assertEquals("The Title", result.getTitle());
        List<SyndEntry> entries = result.getEntries();
        assertEquals(1, entries.size());
        assertEquals("Item Title", entries.get(0).getTitle());
    }
}
