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
package org.eurekastreams.server.action.validation.gallery;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.service.actions.strategies.ContextHolder;
import org.eurekastreams.server.service.actions.strategies.FileFetcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for UrlXmlValidator.
 *
 */
public class UrlXmlValidatorTest
{
    /**
     * Subject under test.
     */
    private UrlXmlValidator sut = null;

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
     * Mocked ServletContext. The SUT needs this to figure out where to write the CSS file.
     */
    private ServletContext servletContext = context.mock(ServletContext.class);

    /**
     * A mocked helper to provide the ServletContext.
     */
    private ContextHolder contextHolder = context.mock(ContextHolder.class);

    /**
     * FileFetcher to use.
     */
    private FileFetcher fileFetcher = new FileFetcher();

    /**
     * Validate theme. Valid theme so expect true.
     */
    @Test
    public void execute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(servletContext).getRealPath(with(any(String.class)));
                will(returnValue("src/test/resources"));

                oneOf(contextHolder).getContext();
                will(returnValue(servletContext));
            }
        });
        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher, contextHolder);

        sut.validate(getActionContext("src/test/resources/themes/vegas.xml"));

        context.assertIsSatisfied();
    }

    /**
     * Validate theme. Bad xml so expect false.
     */
    @Test
    public void executeBadTheme()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(servletContext).getRealPath(with(any(String.class)));
                will(returnValue("src/test/resources"));

                oneOf(contextHolder).getContext();
                will(returnValue(servletContext));
            }
        });

        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher, contextHolder);

        boolean exceptionFired = false;
        try
        {
            sut.validate(getActionContext("src/test/resources/themes/badtheme.xml"));
        }
        catch (ValidationException e)
        {
            exceptionFired = true;
        }
        assertTrue("Didn't get the validation exception that was expected from the bad theme", exceptionFired);

        context.assertIsSatisfied();
    }

    /**
     * Validate theme. Missing so expect false.
     */
    @Test
    public void executeMissingTheme()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(servletContext).getRealPath(with(any(String.class)));
                will(returnValue("src/test/resources"));

                oneOf(contextHolder).getContext();
                will(returnValue(servletContext));
            }
        });

        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher, contextHolder);

        boolean exceptionFired = false;
        try
        {
            sut.validate(getActionContext("src/test/resources/themes/noSuchTheme.xml"));
        }
        catch (ValidationException e)
        {
            exceptionFired = true;
        }
        assertTrue("Didn't get the validation exception that was expected from the missing theme", exceptionFired);
        context.assertIsSatisfied();
    }

    /**
     * Get a principal action context for testing, with the input url as the xml source.
     *
     * @param url
     *            the url to use for the xml
     * @return the principal action context for testing
     */
    private ServiceActionContext getActionContext(final String url)
    {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", url);
        return new ServiceActionContext(params, null);
    }
}
