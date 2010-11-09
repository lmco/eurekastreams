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

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eurekastreams.server.service.utility.http.HttpDocumentFetcher;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Fetches a feed (for use by the stream plugin framework).
 */
public class BasicFeedFetcher implements FeedFetcher
{
    /** Fetches the contents of a URL as an XML document. */
    private HttpDocumentFetcher documentFetcher;

    /**
     * Constructor.
     *
     * @param inDocumentFetcher
     *            Fetches the contents of a URL as an XML document.
     */
    public BasicFeedFetcher(final HttpDocumentFetcher inDocumentFetcher)
    {
        documentFetcher = inDocumentFetcher;
    }

    /**
     * {@inheritDoc}
     */
    public SyndFeed fetchFeed(final String inFeedUrl, final Map<String, String> inHttpHeaders,
            final String inProxyHost, final String inProxyPort, final int inTimeout) throws IOException,
            ParserConfigurationException, FeedException, SAXException
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        Document doc =
                documentFetcher.fetchDocument(inFeedUrl, inHttpHeaders, inProxyHost, inProxyPort, inTimeout,
                        domFactory);

        SyndFeedInput input = new SyndFeedInput();
        return input.build(doc);
    }
}
