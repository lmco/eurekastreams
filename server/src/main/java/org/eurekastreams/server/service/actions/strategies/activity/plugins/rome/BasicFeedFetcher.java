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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Fetches a feed (for use by the stream plugin framework).
 */
public class BasicFeedFetcher
{
    /**
     * Fetches a feed.
     *
     * @param inFeedUrl
     *            the Url for the feed to feed in.
     * @param inHttpHeaders
     *            HTTP headers to add to the request.
     * @param inProxyHost
     *            host name to use (if desires) for proxying http requests.
     * @param inProxyPort
     *            port for http proxy server.
     * @param inTimeout
     *            the timeout period to wait for the feed to return (in ms).
     * @return a Syndicated Feed.
     * @throws FeedException
     *             if Exception.
     * @throws ParserConfigurationException
     *             if Exception.
     * @throws SAXException
     *             if Exception.
     * @throws IOException
     *             if Exception.
     */
    public SyndFeed fetchFeed(final String inFeedUrl, final Map<String, String> inHttpHeaders,
            final String inProxyHost, final String inProxyPort, final int inTimeout) throws IOException,
            ParserConfigurationException,
            FeedException, SAXException
    {
        HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
        managerParams.setSoTimeout(inTimeout);
        managerParams.setConnectionTimeout(inTimeout);

        HttpConnectionManager manager = new SimpleHttpConnectionManager();
        manager.setParams(managerParams);

        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerTimeout(inTimeout);
        params.setSoTimeout(inTimeout);

        HttpClient client = new HttpClient(params, manager);
        HttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(1, true);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);

        if (!inProxyHost.isEmpty())
        {
            client.getHostConfiguration().setProxy(inProxyHost, Integer.parseInt(inProxyPort));
        }

        GetMethod get = new GetMethod(inFeedUrl);

        for (Map.Entry<String, String> header : inHttpHeaders.entrySet())
        {
            get.setRequestHeader(header.getKey(), header.getValue());
        }

        try
        {
            client.executeMethod(get);

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();

            SyndFeedInput input = new SyndFeedInput();
            return input.build(builder.parse(get.getResponseBodyAsStream()));
        }
        finally
        {
            get.releaseConnection();
        }
    }
}
