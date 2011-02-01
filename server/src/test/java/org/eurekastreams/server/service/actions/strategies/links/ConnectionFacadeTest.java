/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ConnectionFacade.
 */
public class ConnectionFacadeTest
{
    /** Test data. */
    private static final String URL = "http://www.eurekastreams.org";

    /** Test data. */
    private static final String ACCOUNT_ID = "jdoe";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: connection. */
    private final HttpURLConnection connection = context.mock(HttpURLConnection.class);

    /** SUT. */
    private ConnectionFacade sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ConnectionFacade(new ArrayList<ConnectionFacadeDecorator>());
    }

    /**
     * Test getter and setter for redirectCodes property.
     */
    @Test
    public void testRedirectCodesProperty()
    {
        List<Integer> redirectCodes = new ArrayList<Integer>();
        sut.setRedirectCodes(redirectCodes);
        assertSame(redirectCodes, sut.getRedirectCodes());
    }

    /**
     * Test the getter and setter for the proxyPort property.
     */
    @Test
    public void testProxyPortProperty()
    {
        String proxyPort = "1234";
        sut.setProxyPort(proxyPort);
        assertEquals(proxyPort, sut.getProxyPort());
    }

    /**
     * Test the getter and setter for the proxyHost property.
     */
    @Test
    public void testProxyHostProperty()
    {
        String proxyHost = "some.proxy.host";
        sut.setProxyHost(proxyHost);
        assertEquals(proxyHost, sut.getProxyHost());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutProperty()
    {
        final int connectionTimeout = 834;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutPropertyAtLowerBoundary()
    {
        final int connectionTimeout = 0;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutPropertyAtMaxBoundary()
    {
        final int connectionTimeout = 30000;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }

    /**
     * Test the connection timeout property.
     */
    @Test(expected = InvalidParameterException.class)
    public void testConnectionTimeoutPropertyLessThanMinValue()
    {
        final int connectionTimeout = -1;
        sut.setConnectionTimeOut(connectionTimeout);
    }

    /**
     * Test the connection timeout property.
     */
    @Test(expected = InvalidParameterException.class)
    public void testConnectionTimeoutPropertyMoreThanMaxValue()
    {
        final int connectionTimeout = 300001;
        sut.setConnectionTimeOut(connectionTimeout);
    }

    /* ---- getFinalUrl tests ---- */

    /**
     * Common setup for most getFinalUrl tests.
     */
    private void setupForGetFinalUrlTests()
    {
        sut = new ConnectionFacade(new ArrayList<ConnectionFacadeDecorator>())
        {
            @Override
            protected HttpURLConnection getConnection(final String inUrl, final String inAccountId)
                    throws MalformedURLException
            {
                return connection;
            }
        };

        // checkstyle rules gone too far
        final int rc301 = 301;
        final int rc302 = 302;
        final int rc303 = 303;
        final int rc307 = 307;
        sut.setRedirectCodes(Arrays.asList(rc301, rc302, rc303, rc307));
    }

    /**
     * Tests getFinalUrl for connection setup failure.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testGetFinalUrlInvalid() throws IOException
    {
        sut = new ConnectionFacade(new ArrayList<ConnectionFacadeDecorator>())
        {
            @Override
            protected HttpURLConnection getConnection(final String inUrl, final String inAccountId)
                    throws MalformedURLException
            {
                return null;
            }
        };

        assertEquals(URL, sut.getFinalUrl(URL, ACCOUNT_ID));
    }

    /**
     * Tests getFinalUrl.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testGetFinalUrlRedirectHTTP() throws IOException
    {
        final String newUrl = "http://www.apache.org";
        final int rc = 301;

        setupForGetFinalUrlTests();
        context.checking(new Expectations()
        {
            {
                allowing(connection).getResponseCode();
                will(returnValue(rc));
                allowing(connection).getHeaderField("Location");
                will(returnValue(newUrl));
            }
        });

        assertEquals(newUrl, sut.getFinalUrl(URL, ACCOUNT_ID));
    }

    /**
     * Tests getFinalUrl.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testGetFinalUrlRedirectHTTPS() throws IOException
    {
        final String newUrl = "https://www.apache.org";
        final int rc = 301;

        setupForGetFinalUrlTests();
        context.checking(new Expectations()
        {
            {
                allowing(connection).getResponseCode();
                will(returnValue(rc));
                allowing(connection).getHeaderField("Location");
                will(returnValue(newUrl));
            }
        });

        assertEquals(URL, sut.getFinalUrl(URL, ACCOUNT_ID));
    }

    /**
     * Tests getFinalUrl.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testGetFinalUrlNoRedirect() throws IOException
    {
        final int rc = 200;

        setupForGetFinalUrlTests();
        context.checking(new Expectations()
        {
            {
                allowing(connection).getResponseCode();
                will(returnValue(rc));
            }
        });

        assertEquals(URL, sut.getFinalUrl(URL, ACCOUNT_ID));
    }

    /* ---- File download tests ---- */

    /** Test data. */
    private static final int EXPECTED_MAX_FILE_SIZE = 20;

    /** Test data. */
    private static final int MAX_FILE_SIZE = 50;

    /** Fixture: httpReader. */
    private final Reader httpReader = context.mock(Reader.class);

    /**
     * Action used to return a value when a stream is read.
     */
    private class ReadAction implements Action
    {
        /** Value to return from read. */
        private final String value;

        /**
         * Constructor.
         *
         * @param inValue
         *            Value to return from read.
         */
        public ReadAction(final String inValue)
        {
            value = inValue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void describeTo(final Description inArg0)
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(final Invocation inInvocation) throws Throwable
        {
            int targetPos = inInvocation.getParameterCount() > 1 ? (int) (Integer) inInvocation.getParameter(1) : 0;
            System.arraycopy(value.toCharArray(), 0, (inInvocation.getParameter(0)), targetPos, value.length());
            return value.length();
        }
    }

    /**
     * Common setup for download file tests.
     */
    private void setupForDownloadFileTests()
    {
        sut = new ConnectionFacade(new ArrayList<ConnectionFacadeDecorator>())
        {
            @Override
            protected Reader getConnectionReader(final String inUrl, final String inAccountId) throws IOException
            {
                if (!URL.equals(inUrl) || !ACCOUNT_ID.equals(inAccountId))
                {
                    Assert.fail("getConnectionReader invoked with wrong arguments.");
                }
                return httpReader;
            }
        };
        sut.setExpectedDownloadFileLimit(EXPECTED_MAX_FILE_SIZE);
        sut.setMaximumDownloadFileLimit(MAX_FILE_SIZE);
    }

    /**
     * Tests downloading a file.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testDownloadFileEmpty() throws IOException
    {
        setupForDownloadFileTests();

        final States state = context.states("readStep").startsAs("1");
        context.checking(new Expectations()
        {
            {
                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("1"));
                then(state.is("2"));
                will(returnValue(-1));

                oneOf(httpReader).close();
                when(state.is("2"));
                then(state.is("E"));
            }
        });

        assertEquals("", sut.downloadFile(URL, ACCOUNT_ID));

        context.assertIsSatisfied();
    }

    /**
     * Tests downloading a file.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testDownloadFileSmall() throws IOException
    {
        setupForDownloadFileTests();

        final States state = context.states("readStep").startsAs("1");
        context.checking(new Expectations()
        {
            {
                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("1"));
                then(state.is("2"));
                will(new ReadAction("ABCDEFGHIJ"));

                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("2"));
                then(state.is("3"));
                will(new ReadAction("WXYZ"));

                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("3"));
                then(state.is("4"));
                will(returnValue(-1));

                oneOf(httpReader).close();
                when(state.is("4"));
                then(state.is("E"));
            }
        });

        assertEquals("ABCDEFGHIJWXYZ", sut.downloadFile(URL, ACCOUNT_ID));

        context.assertIsSatisfied();
    }

    /**
     * Tests downloading a file.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test
    public void testDownloadFileLarge() throws IOException
    {
        setupForDownloadFileTests();

        final States state = context.states("readStep").startsAs("1");
        context.checking(new Expectations()
        {
            {
                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("1"));
                then(state.is("2"));
                will(new ReadAction("ABCDEFGHIJ"));

                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("2"));
                then(state.is("3"));
                will(new ReadAction("abcdefghij"));

                oneOf(httpReader).read(with(any(char[].class)));
                when(state.is("3"));
                then(state.is("4"));
                will(new ReadAction("MMM"));

                oneOf(httpReader).read(with(any(char[].class)));
                when(state.is("4"));
                then(state.is("5"));
                will(new ReadAction("ZZZ"));

                oneOf(httpReader).read(with(any(char[].class)));
                when(state.is("5"));
                then(state.is("6"));
                will(returnValue(-1));

                oneOf(httpReader).close();
                when(state.is("6"));
                then(state.is("E"));
            }
        });

        assertEquals("ABCDEFGHIJabcdefghijMMMZZZ", sut.downloadFile(URL, ACCOUNT_ID));

        context.assertIsSatisfied();
    }

    /**
     * Tests downloading a file.
     *
     * @throws IOException
     *             Shouldn't.
     */
    @Test(expected = AssertionError.class)
    public void testDownloadFileTooLarge() throws IOException
    {
        setupForDownloadFileTests();

        final States state = context.states("readStep").startsAs("1");
        context.checking(new Expectations()
        {
            {
                oneOf(httpReader).read(with(any(char[].class)), with(any(int.class)), with(any(int.class)));
                when(state.is("1"));
                then(state.is("2"));
                will(new ReadAction("ABCDEFGHIJabcdefghij"));

                oneOf(httpReader).read(with(any(char[].class)));
                when(state.is("2"));
                then(state.is("3"));
                will(new ReadAction("01234567890123456789"));

                oneOf(httpReader).read(with(any(char[].class)));
                when(state.is("3"));
                then(state.is("4"));
                will(new ReadAction("AbCdEfGhIjK"));
            }
        });

        sut.downloadFile(URL, ACCOUNT_ID);

        context.assertIsSatisfied();
    }
}
