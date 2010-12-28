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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private final Log log = LogFactory.getLog(ConnectionFacade.class);

    /**
     * Trust manager.
     */
    private final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
    {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }

        @Override
        public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
        {
        }

        @Override
        public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
        {
        }
    } };

    /**
     * Cache of URLs. Uses WeakHashMap to prevent memory leak.
     */
    private final Map<String, URL> urlMap = new WeakHashMap<String, URL>();

    /**
     * Cache of Image Dimensions. Uses WeakHashMap to prevent memory leak.
     */
    private final Map<String, ImageDimensions> imgMap = new WeakHashMap<String, ImageDimensions>();

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
     * List of decorators that can add headers to the connection.
     */
    private final List<ConnectionFacadeDecorator> decorators;

    /** Buffer size to use for downloading files; should be sightly larger than the typical file size. */
    private int expectedDownloadFileLimit;

    /** Maximum allowable size for downloaded files (to prevent DoS via out of memory). */
    private int maximumDownloadFileLimit;

    /**
     * Constructor.
     *
     * @param inDecorators
     *            - List of ConnectionFacadeDecorator instances.
     */
    public ConnectionFacade(final List<ConnectionFacadeDecorator> inDecorators)
    {
        decorators = inDecorators;
    }

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
        redirectCodes = inRedirectCodes;
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
        proxyPort = inProxyPort;
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
        proxyHost = inProxyHost;
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
        connectionTimeOut = inConnectionTimeOut;
    }

    /**
     * Download a file.
     *
     * @param url
     *            the URL as a string.
     * @param inAccountId
     *            accountid of the user making the request.
     * @return the file as a string.
     * @throws IOException
     *             if URL can't be opened.
     */
    public String downloadFile(final String url, final String inAccountId) throws IOException
    {
        char[] buffer = new char[expectedDownloadFileLimit];

        Reader reader = getConnectionReader(url, inAccountId);
        try
        {
            // first just read into the buffer
            int charsRead = 0;
            while (charsRead < buffer.length)
            {
                int thisRead = reader.read(buffer, charsRead, buffer.length - charsRead);
                if (thisRead < 0)
                {
                    return new String(buffer, 0, charsRead);
                }
                charsRead += thisRead;
            }

            // filled the buffer, now use a StringBuilder
            StringBuilder builder = new StringBuilder();
            do
            {
                if (builder.length() + charsRead > maximumDownloadFileLimit)
                {
                    throw new IOException("Downloaded file too large.");
                }

                builder.append(buffer, 0, charsRead);
                charsRead = reader.read(buffer);
            }
            while (charsRead >= 0);

            return builder.toString();
        }
        finally
        {
            reader.close();
        }
    }

    /**
     * Returns an input reader for downloading a file from an HTTP connection. (A separate method for unit testing.)
     *
     * @param url
     *            URL from which to download a file.
     * @param inAccountId
     *            Accountid of the user making the request.
     * @return Reader for downloading a file via HTTP.
     * @throws IOException
     *             If URL can't be opened.
     */
    protected Reader getConnectionReader(final String url, final String inAccountId) throws IOException
    {
        return new InputStreamReader(getConnection(url, inAccountId).getInputStream());
    }

    /**
     * Get the height of an image by URL.
     *
     * @param url
     *            the url.
     * @param inAccountId
     *            account id of the user making the request.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    public int getImgHeight(final String url, final String inAccountId) throws IOException
    {
        ImageDimensions dimensions = getImgDimensions(url, inAccountId);

        return dimensions.getHeight();
    }

    /**
     * Get the width of an image by URL.
     *
     * @param url
     *            the url.
     * @param inAccountId
     *            account id of the user making the request.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    public int getImgWidth(final String url, final String inAccountId) throws IOException
    {
        ImageDimensions dimensions = getImgDimensions(url, inAccountId);

        return dimensions.getHeight();
    }

    /**
     * Get the connection.
     *
     * @param url
     *            the url.
     * @param inAccountId
     *            account id of the user making the request.
     * @return the connection.
     */
    protected HttpURLConnection getConnection(final String url, final String inAccountId)
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
                    @Override
                    public boolean verify(final String hostname, final SSLSession session)
                    {
                        log.trace("Accepting host name.");
                        return true;
                    }
                });

                connection.setConnectTimeout(connectionTimeOut);

                // Loop through the Header decorators and the map of headers that they contain.

            }
            else
            {
                log.trace("Using HTTP");
                theUrl = getUrl(url);

                connection = (HttpURLConnection) theUrl.openConnection();
                connection.setConnectTimeout(connectionTimeOut);
            }

            // Decorate the connection.
            for (ConnectionFacadeDecorator decorator : decorators)
            {
                decorator.decorate(connection, inAccountId);
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
     * @param inAccountId
     *            account id of the user making the request.
     * @return the image height.
     * @throws IOException
     *             on bad url.
     */
    private ImageDimensions getImgDimensions(final String url, final String inAccountId) throws IOException
    {
        if (!imgMap.containsKey(url))
        {
            log.info("Downloading image: " + url);

            InputStream connectionStream = getConnection(url, inAccountId).getInputStream();
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
        private final int width;

        /**
         * Height.
         */
        private final int height;

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
     * @param inAccountId
     *            account id of the user making the request.
     * @return the final url.
     * @throws IOException
     *             shouldn't happen.
     */
    public String getFinalUrl(final String url, final String inAccountId) throws IOException
    {
        HttpURLConnection connection = getConnection(url, inAccountId);
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

    /**
     * @return the expectedDownloadFileLimit
     */
    public int getExpectedDownloadFileLimit()
    {
        return expectedDownloadFileLimit;
    }

    /**
     * @param inExpectedDownloadFileLimit
     *            the expectedDownloadFileLimit to set
     */
    public void setExpectedDownloadFileLimit(final int inExpectedDownloadFileLimit)
    {
        expectedDownloadFileLimit = inExpectedDownloadFileLimit;
    }

    /**
     * @return the maximumDownloadFileLimit
     */
    public int getMaximumDownloadFileLimit()
    {
        return maximumDownloadFileLimit;
    }

    /**
     * @param inMaximumDownloadFileLimit
     *            the maximumDownloadFileLimit to set
     */
    public void setMaximumDownloadFileLimit(final int inMaximumDownloadFileLimit)
    {
        maximumDownloadFileLimit = inMaximumDownloadFileLimit;
    }
}
