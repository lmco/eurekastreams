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
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Strategy that uses a cookie to bypass a proxy.
 * 
 */
public class HttpCookieBypassStrategy implements PluginFeedFetcherStrategy
{
    /**
     * The Site Url's Regular Expression.
     */
    String siteUrlRegEx;

    /**
     * The key=value pair of the cookie to set.
     */
    String cookieToSet;

    /**
     * @param inSiteUrlRegEx
     *            The url RegEx.
     * @param inCookieToSet
     *            the (key=value) cookie to set.
     */
    public HttpCookieBypassStrategy(final String inSiteUrlRegEx, final String inCookieToSet)
    {
        siteUrlRegEx = inSiteUrlRegEx;
        cookieToSet = inCookieToSet;
    }

    /**
     * @return the sites regularExpression.
     */
    public String getSiteUrlRegEx()
    {
        return siteUrlRegEx;
    }

    /**
     * Executes the strategy.
     * 
     * @param inFeedURL
     *            the Url for the feed to feed in.
     * @return a Syndicated Feed.
     * @throws FeedException if Exception.
     * @throws ParserConfigurationException if Exception.
     * @throws SAXException if Exception.
     * @throws IOException if Exception.
     */
    public SyndFeed execute(final URL inFeedURL) throws IOException, ParserConfigurationException,
            FeedException, SAXException
    {
        URLConnection urlConn = inFeedURL.openConnection();
        urlConn.setRequestProperty("Cookie", cookieToSet);
        urlConn.connect();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();

        SyndFeedInput input = new SyndFeedInput();
        return input.build(builder.parse(urlConn.getInputStream()));
    }

}
