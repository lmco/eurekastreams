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

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.service.actions.strategies.FileFetcher;
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
     * FileFetcher to use.
     */
    private FileFetcher fileFetcher = new FileFetcher();

    /**
     * Validate theme. Valid theme so expect true.
     */
    @Test
    public void execute()
    {
        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher);

        sut.validate(getActionContext("src/test/resources/themes/vegas.xml"));
    }

    /**
     * Validate theme. Bad xml so expect false.
     */
    @Test
    public void executeBadTheme()
    {

        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher);

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
    }

    /**
     * Validate theme. Missing so expect false.
     */
    @Test
    public void executeMissingTheme()
    {
        sut = new UrlXmlValidator("http://www.w3.org/2001/XMLSchema", "/themes/theme.xsd", fileFetcher);

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
