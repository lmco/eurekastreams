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
package org.eurekastreams.server.service.servlets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for RequestUriToThemeUuidTransformer.
 * 
 */
public class RequestUriToThemeUuidTransformerTest
{
    /**
     * Test.
     */
    @Test
    public void testNormal()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("testuuid", sut.transform("/blah/whatever/someversionhere_uuid_testUUID.css"));
    }

    /**
     * Test.
     */
    @Test
    public void testNoExtension()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("testuuid", sut.transform("/blah/whatever/someversionhere_uuid_testUUID"));
    }

    /**
     * Test.
     */
    @Test
    public void testduplicateSeparators()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("testuuid", sut.transform("/blah/whatever/someversionhere_uuid_testUUID.css"));
    }

    /**
     * Test.
     */
    @Test
    public void testExtensionCaseInsensitive()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("testuuid", sut.transform("/blah/whatever/someversionhere_uuid_testUUID.cSs"));
    }

    /**
     * Test.
     */
    @Test
    public void testUuidCaseInsensitive()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("testuuid", sut.transform("/blah/whatever/someversionhere_uuid_testUUID.css"));
    }

    /**
     * Test.
     */
    @Test
    public void testEmptyUuid()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals("", sut.transform("/blah/whatever/someversionhere_uuid_"));
    }

    /**
     * Test.
     */
    @Test
    public void testNullUri()
    {
        RequestUriToThemeUuidTransformer sut = new RequestUriToThemeUuidTransformer("_uuid_", ".css");
        assertEquals(null, sut.transform(null));
    }
}
