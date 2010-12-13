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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PersonPagePropertiesDTOToThemeCssUrlTransformer.
 * 
 */
public class PersonPagePropertiesDTOToThemeCssUrlTransformerTest
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
     * Mapper to return most recent theme version.
     */
    private DomainMapper<String, String> themeVersionByUuidMapper = context.mock(DomainMapper.class);

    /**
     * {@link PersonPagePropertiesDTO}.
     */
    private PersonPagePropertiesDTO pppDto = context.mock(PersonPagePropertiesDTO.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final String uuid = "uuid";
        final String version = "version";
        PersonPagePropertiesDTOToThemeCssUrlTransformer sut = new PersonPagePropertiesDTOToThemeCssUrlTransformer(
                "/prefix/", ".extension", "_sep_", themeVersionByUuidMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(pppDto).getThemeUuid();
                will(returnValue(uuid));

                oneOf(themeVersionByUuidMapper).execute(uuid);
                will(returnValue(version));
            }
        });

        assertEquals("/prefix/version_sep_uuid.extension", sut.transform(pppDto));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNullUuid()
    {
        final String uuid = null;
        PersonPagePropertiesDTOToThemeCssUrlTransformer sut = new PersonPagePropertiesDTOToThemeCssUrlTransformer(
                "/prefix/", ".extension", "_sep_", themeVersionByUuidMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(pppDto).getThemeUuid();
                will(returnValue(uuid));
            }
        });

        assertEquals(null, sut.transform(pppDto));
        context.assertIsSatisfied();
    }
}
