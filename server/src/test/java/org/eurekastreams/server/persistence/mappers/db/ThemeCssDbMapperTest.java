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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.actions.strategies.FileFetcher;
import org.eurekastreams.server.service.actions.strategies.ResourceFetcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ThemeCssDbMapper.
 * 
 */
public class ThemeCssDbMapperTest
{
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
     * Path to xslt resource.
     */
    private String xsltPath = "/themes/css.xslt";

    /**
     * Fetcher for the XML theme definition.
     */
    private ResourceFetcher xmlFetcher = new FileFetcher();

    /**
     * Theme xml url mapper.
     */
    private DomainMapper<String, String> getThemeXmlUrlByUuidDbMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private ThemeCssDbMapper sut = new ThemeCssDbMapper(xsltPath, getThemeXmlUrlByUuidDbMapper, xmlFetcher);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getThemeXmlUrlByUuidDbMapper).execute("uuid");
                will(returnValue("src/test/resources/themes/vegas.xml"));
            }
        });

        String result = sut.execute("uuid");
        assertTrue(result.contains("div.banner-container"));

        context.assertIsSatisfied();
    }

}
