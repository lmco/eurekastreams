/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.links;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to cache HTTP requests from the server and for testability.
 */
public class ConnectionFacade
{
    /**
     * Max value for timeout.
     */
    private static final Integer MIN_TIMEOUT = 0;

    /**
     * Min value for timeout.
     */
    private static final Integer MAX_TIMEOUT = 30000;

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ConnectionFacade.class);

    /**
     * Trust manager.
     */
    private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
    {
        public java.security.cert.X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }

        public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
        {
        }

        public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
        {
        }
    } };

    /**
     * Cache of URLs. Uses WeakHashMap to prevent memory leak.
     */
    private Map<String, URL> urlMap = new WeakHashMap<String, URL>();

    /**
     * Cache of Image Dimensions. Uses WeakHashMap to prevent memory leak.
     */
    private Map<String, ImageDimensions> imgMap = new WeakHashMap<String, ImageDimensions>();

    /**
     * The user agent to use for requests.
     */
    private String userAgent = "";

    /**
     * Max time for connections.
     */
    private int connectionTimeOut = MIN_TIMEOUT;

    /**
     * The proxy host.
     */
    private String proxyHost = "";

    /**
     * The proxy port.
     */
    private String proxyPort = "";

    /**
     * HTTP redirect codes.
     */
    private List<Integer> redirectCodes = new ArrayList<Integer>();

    /**
     * @return the redirectCodes
     */
    public final List<Integer> getRedirectCodes()
    {
        return redirectCodes;
    }

    /**
     * @param inRedirectCodes
     *            the redirectCodes to set
     */
    public final void setRedirectCodes(final List<Integer> inRedirectCodes)
    {
        this.redirectCodes = inRedirectCodes;
    }

    /**
     * @return the proxyPort
     */
    public final String getProxyPort()
    {
        return proxyPort;
    }

    /**
     * @param inProxyPort
     *            the proxyPort to set
     */
    public final void setProxyPort(final String inProxyPort)
    {
        this.proxyPort = inProxyPort;
    }

    /**
     * @return the proxyHost
     */
    public final String getProxyHost()
    {
        return proxyHost;
    }

    /**
     * @param inProxyHost
     *            the proxyHost to set
     */
    public final void setProxyHost(final String inProxyHost)
    {
        this.proxyHost = inProxyHost;
    }

    /**
     * @return the userAgent
     */
    public final String getUserAgent()
    {
        return userAgent;
    }

    /**
     * @param inUserAgent
     *            the userAgent to set
     */
    public final void setUserAgent(final String inUserAgent)
    {
        this.userAgent = inUserAgent;
    }

    /**
     * @return the connectionTimeOut
     */
    public final int getConnectionTimeOut()
    {
        return connectionTimeOut;
    }

    /**
     * @param inConnectionTimeOut
     *            the connectionTimeOut to set
     */
    public final void setConnectionTimeOut(final int inConnectionTimeOut)
    {
        // the following takes care of input validation
        if (inConnectionTimeOut < MIN_TIMEOUT || inConnectionTimeOut > MAX_TIMEOUT)
        {
            throw new InvalidParameterException("Connection timeout must be between " + MIN_TIMEOUT + " and "
                    + MAX_TIMEOUT);
        }
        this.connectionTimeOut = inConnectionTimeOut;
    }

    /**
     * Download a file.
     *
     * @param url
     *            the URL as a string.
     * @return the file as a string.
     * @throws IOException
     *             of URL can't be opened.
     */
    public String downloadFile(final String url) throws IOException
    {
        String s = "";
        DataInputStream data = new DataInputStream(new BufferedInputStream(getConnection(url).getInputStream()));

        /**
         * I'd like to use a more robust parsing library here.
         */
        StringBuffer htmlBuffer = new StringBuffer();

        while ((s = data.readLine()) != null)
        {
            htmlBuffer.append(s);
        }

        String htmlString = htmlBuffer.toString();

        return htmlString;
    }

    /**
     * Get the height of an image by URL.
     *
     * @param url
     *            the url.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    public int getImgHeight(final String url) throws IOException
    {
        ImageDimensions dimensions = getImgDimensions(url);

        return dimensions.getHeight();
    }

    /**
     * Get the width of an image by URL.
     *
     * @param url
     *            the url.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    public int getImgWidth(final String url) throws IOException
    {
        ImageDimensions dimensions = getImgDimensions(url);

        return dimensions.getHeight();
    }

    /**
     * Get the connection.
     *
     * @param url
     *            the url.
     * @return the connection.
     */
    private HttpURLConnection getConnection(final String url)
    {
        HttpURLConnection connection = null;

        if (proxyHost.length() > 0)
        {
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", proxyPort);
        }

        log.info("Using Proxy: " + proxyHost + ":" + proxyPort);

        URL theUrl = null;
        try
        {
            theUrl = getUrl(url);
        }
        catch (MalformedURLException e1)
        {
            log.error("Bad URL");
        }

        try
        {
            /**
             * Some sites e.g. Google Images and Digg will not respond to an unrecognized User-Agent.
             */

            if (url.startsWith("https"))
            {
                log.trace("Using HTTPS");

                // Install the all-trusting trust manager
                try
                {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    log.trace("Installed all-trusting trust manager.");
                }
                catch (Exception e)
                {
                    log.error("Error setting SSL Context");
                }

                connection = (HttpsURLConnection) theUrl.openConnection();

                ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier()
                {
                    public boolean verify(final String hostname, final SSLSession session)
                    {
                        log.trace("Accepting host name.");
                        return true;
                    }
                });

                connection.setConnectTimeout(connectionTimeOut);

                connection.addRequestProperty("User-Agent", userAgent);

            }
            else
            {
                log.trace("Using HTTP");
                theUrl = getUrl(url);

                connection = (HttpURLConnection) theUrl.openConnection();
                connection.setConnectTimeout(connectionTimeOut);
                connection.addRequestProperty("User-Agent", userAgent);
            }
        }
        catch (Exception e)
        {
            log.error("caught exception: ", e);
        }

        return connection;
    }

    /**
     * Get the dimensions of an image by URL.
     *
     * @param url
     *            the url.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    private ImageDimensions getImgDimensions(final String url) throws IOException
    {
        if (!imgMap.containsKey(url))
        {
            log.info("Downloading image: " + url);

            InputStream connectionStream = getConnection(url).getInputStream();
            if (null != connectionStream)
            {
                BufferedImage img = ImageIO.read(connectionStream);
                imgMap.put(url, new ImageDimensions(img.getHeight(), img.getWidth()));
            }
            else
            {
                imgMap.put(url, new ImageDimensions(0, 0));
            }

        }

        return imgMap.get(url);
    }

    /**
     * Dimensions of an image.
     */
    protected class ImageDimensions
    {
        /**
         * Width.
         */
        private int width;

        /**
         * Height.
         */
        private int height;

        /**
         * Constructor.
         *
         * @param inHeight
         *            height.
         * @param inWidth
         *            width.
         */
        public ImageDimensions(final int inHeight, final int inWidth)
        {
            height = inHeight;
            width = inWidth;
        }

        /**
         * @return the height.
         */
        public int getHeight()
        {
            return height;
        }

        /**
         * @return the width.
         */
        public int getWidth()
        {
            return width;
        }
    }

    /**
     * Get the file host.
     *
     * @param url
     *            the url.
     * @return the host.
     *
     * @throws MalformedURLException
     *             on bad URL.
     */
    public String getHost(final String url) throws MalformedURLException
    {
        URL theUrl = getUrl(url);

        return theUrl.getHost();
    }

    /**
     * Get the protocol of a URL.
     *
     * @param url
     *            the url.
     * @return the protocol.
     *
     * @throws MalformedURLException
     *             on bad URL.
     */
    public String getProtocol(final String url) throws MalformedURLException
    {
        URL theUrl = getUrl(url);

        return theUrl.getProtocol();
    }

    /**
     * Get the path of a URL.
     *
     * @param url
     *            the url.
     * @return the protocol.
     *
     * @throws MalformedURLException
     *             on bad URL.
     */
    public String getPath(final String url) throws MalformedURLException
    {
        URL theUrl = getUrl(url);

        return theUrl.getPath();
    }

    /**
     * Used to cache URLs.
     *
     * @param url
     *            the url as a String.
     * @return the URL object.
     * @throws MalformedURLException
     *             on bad URL.
     */
    private URL getUrl(final String url) throws MalformedURLException
    {
        if (!urlMap.containsKey(url))
        {
            urlMap.put(url, new URL(url));
        }

        return urlMap.get(url);
    }

    /**
     * Get the final URL after redirect.
     *
     * @param url
     *            the initial url.
     * @return the final url.
     * @throws IOException
     *             shouldn't happen.
     */
    public String getFinalUrl(final String url) throws IOException
    {
        HttpURLConnection connection = getConnection(url);
        if (redirectCodes.contains(connection.getResponseCode()))
        {
            String redirUrl = connection.getHeaderField("Location");
            log.trace("Found redirect header to: " + redirUrl);

            // Check for protocol change
            if (redirUrl.startsWith("http://"))
            {
                log.trace("Changing protocol to HTTP");
                return redirUrl;
            }
        }

        return url;
    }
}
