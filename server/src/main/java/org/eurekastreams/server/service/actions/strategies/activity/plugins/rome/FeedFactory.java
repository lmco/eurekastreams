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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

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
    List<PluginFeedFetcherStrategy> siteStratagies;
    
    /**
     * The connection timeout (in milliseconds).
     */
    int timeout;
    
    /**
     * Proxy hostname.
     */
    String proxyHost;
    
    /**
     * Proxy port.
     */
    String proxyPort;

    /**
     * @param inSiteStratagies
     *            the list of strategies.
     * @param inProxyHost
     *            the http proxy hostname (if necessary).
     * @param inProxyPort
     *            the http proxy port.
     * @param inTimeout
     *            the connection timeout (in ms).
     */
    public FeedFactory(final List<PluginFeedFetcherStrategy> inSiteStratagies, final String inProxyHost,
            final String inProxyPort, final int inTimeout)
    {
        siteStratagies = inSiteStratagies;
        proxyHost = inProxyHost;
        proxyPort = inProxyPort;
        timeout = inTimeout;
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

                return ps.execute(inFeedURL, proxyHost, proxyPort, timeout);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Feed being fetched from normal site" + inFeedURL);
        }

        HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
        managerParams.setSoTimeout(timeout);
        managerParams.setConnectionTimeout(timeout);
        
        HttpConnectionManager manager = new SimpleHttpConnectionManager();
        manager.setParams(managerParams);
        
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerTimeout(timeout);
        params.setSoTimeout(timeout);
        
        HttpClient client = new HttpClient(params, manager);
        HttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(1, true);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
        
        if (!proxyHost.isEmpty())
        {
            client.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
        }
        
        GetMethod get = new GetMethod(inFeedURL.toString());

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
