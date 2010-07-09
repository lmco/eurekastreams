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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

/**
 * Test for CSSBuilderDecorator.
 */
public class CSSBuilderDecoratorTest
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CSSBuilderDecoratorTest.class);

    /**
     * Subject under test.
     */
    private CSSBuilderDecorator sut = null;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked Person passed to the decorator.
     */
    private Person person = context.mock(Person.class);

    /**
     * Mocked Theme that points to an XML description.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * Mocked destination for the CSS content.
     */
    private StreamResult cssResult = new StreamResult();

    /**
     * The cache lifetime for the CSS files.
     */
    private static final long ONE_DAY = 86400000;

    /**
     * Mocked ServletContext. The SUT needs this to figure out where to write the CSS file.
     */
    private ServletContext servletContext = context.mock(ServletContext.class);

    /**
     * A mocked helper to provide the ServletContext.
     */
    private ContextHolder contextHolder = context.mock(ContextHolder.class);

    /**
     * Load an XML resource and produce CSS.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void decorate() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                atLeast(1).of(person).getTheme();
                will(returnValue(theme));

                atLeast(1).of(theme).getUrl();
                will(returnValue("src/test/resources/themes/vegas.xml"));

                oneOf(theme).getCssFile();
                will(returnValue("/themes/vegas.css"));

                allowing(servletContext).getRealPath(with(any(String.class)));
                will(returnValue(new File("src/test/resources").getCanonicalPath()));

                allowing(contextHolder).getContext();
                will(returnValue(servletContext));

            }
        });

        final FileFetcher fileFetcher = new FileFetcher();

        sut = new CSSBuilderDecorator(null, "themes/css.xslt", fileFetcher, cssResult, contextHolder, ONE_DAY);

        try
        {
            // This is an integration test. We need to verify that the sut actually
            // created a file on the file system.
            File file = new File("src/test/resources/themes/vegas.css");
            file.delete();

            sut.decorate(person);

            file = new File("src/test/resources/themes/vegas.css");
            if (!file.exists())
            {
                fail("Failed to create vegas.css");
            }

            context.assertIsSatisfied();
        }
        catch (MalformedURLException ex)
        {
            fail("Caught MalformedURLException: " + ex.getStackTrace()[0]);
        }
        catch (Exception ex)
        {
            fail("Caught exception: " + ex.getClass().getName() + ": " + ex.getMessage());
            log.debug(ex.getStackTrace());
        }
    }

    /**
     * Load an XML resource and produce CSS, throwing exception based on invalid/illegal path.
     * 
     * @throws IOException
     *             on error
     */
    @Test(expected = SecurityException.class)
    public void testDecorateWithInvalidPath() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                atLeast(1).of(person).getTheme();
                will(returnValue(theme));

                atLeast(1).of(theme).getUrl();
                will(returnValue("src/test/resources/themes/vegas.xml"));

                oneOf(theme).getCssFile();
                will(returnValue("/../../../../../../themes/vegas.css"));

                allowing(servletContext).getRealPath(with(any(String.class)));
                will(returnValue(new File("src/test/resources").getCanonicalPath()));

                allowing(contextHolder).getContext();
                will(returnValue(servletContext));

            }
        });

        final FileFetcher fileFetcher = new FileFetcher();

        sut = new CSSBuilderDecorator(null, "themes/css.xslt", fileFetcher, cssResult, contextHolder, ONE_DAY);

        try
        {
            // This is an integration test. We need to verify that the sut actually
            // created a file on the file system.
            File file = new File("src/test/resources/themes/vegas.css");
            file.delete();

            sut.decorate(person);

            file = new File("src/test/resources/themes/vegas.css");
            if (!file.exists())
            {
                fail("Failed to create vegas.css");
            }

            context.assertIsSatisfied();
        }
        catch (MalformedURLException ex)
        {
            fail("Caught MalformedURLException: " + ex.getStackTrace()[0]);
        }
        catch (SecurityException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            fail("Caught exception: " + ex.getClass().getName() + ": " + ex.getMessage());
            log.debug(ex.getStackTrace());
        }
    }

    /**
     * Test whether the decorator makes proper use of caching, by setting the modified time on the file to now and
     * making sure it doesn't get recreated.
     * 
     * @throws IOException
     *             on error
     */
    @Test
    public void decorateWithFreshCache() throws IOException
    {
        context.checking(new Expectations()
        {
            {
                allowing(contextHolder).getContext();
                will(returnValue(servletContext));

                allowing(servletContext).getRealPath(with(any(String.class)));
                will(returnValue(new File("src/test/resources").getCanonicalPath()));

                atLeast(1).of(person).getTheme();
                will(returnValue(theme));

                oneOf(theme).getCssFile();
                will(returnValue("/themes/vegas.css"));
            }
        });

        final FileFetcher fileFetcher = new FileFetcher();

        sut = new CSSBuilderDecorator(null, "themes/css.xslt", fileFetcher, cssResult, contextHolder, ONE_DAY);

        try
        {
            // This is an integration test. We need to verify that the sut actually
            // created a file on the file system.
            File file = new File("src/test/resources/themes/vegas.css");
            file.createNewFile();
            // just in case it was already there:
            long now = Calendar.getInstance().getTimeInMillis();
            file.setLastModified(now);

            sut.decorate(person);

            file = new File("src/test/resources/themes/vegas.css");
            assertTrue("Timestamp on vegas.css changed; it should not have", now != file.lastModified());

            context.assertIsSatisfied();
        }
        catch (MalformedURLException ex)
        {
            fail("Caught MalformedURLException: " + ex.getStackTrace()[0]);
        }
        catch (Exception ex)
        {
            fail("Caught exception: " + ex.getClass().getName() + ": " + ex.getMessage());
            log.debug(ex.getStackTrace());
        }
    }

    /**
     * If asked to decorate a page that has no theme, the SUT should just return.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void decorateWithNoTheme() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(contextHolder).getContext();
                will(returnValue(servletContext));

                allowing(servletContext).getRealPath(with(any(String.class)));
                will(returnValue("src/test/resources"));

                atLeast(1).of(person).getTheme();
                will(returnValue(null));
            }
        });

        sut = new CSSBuilderDecorator(null, "themes/css.xslt", null, cssResult, contextHolder, ONE_DAY);

        sut.decorate(person);
    }

    /**
     * Tear down fixtures.
     */
    @After
    public final void tearDown()
    {
        File file = new File("src/test/resources/themes/vegas.css");
        file.delete();
    }
}
