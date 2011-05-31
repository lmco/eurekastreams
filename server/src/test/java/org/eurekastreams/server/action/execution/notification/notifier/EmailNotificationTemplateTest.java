/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.notifier;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests EmailNotificationTemplate. This is really just a DTO, but the tests confirm the multiple setters behave
 * properly.
 */
public class EmailNotificationTemplateTest
{
    /** Test data. */
    private static final String SUBJECT_TEMPLATE = "Actual template for the subject";

    /** Test data. */
    private static final String TEXT_BODY_TEMPLATE_RESOURCE_PATH = "Resource path for the text form of the body";

    /** Test data. */
    private static final String HTML_BODY_TEMPLATE_RESOURCE_PATH = "Resource path for the HTML form of the body";

    /** SUT. */
    private EmailNotificationTemplate sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new EmailNotificationTemplate();
    }

    /**
     * Verifies using the getters.
     */
    private void verifyGetters()
    {
        assertEquals(SUBJECT_TEMPLATE, sut.getSubjectTemplate());
        assertEquals(TEXT_BODY_TEMPLATE_RESOURCE_PATH, sut.getTextBodyTemplateResourcePath());
        assertEquals(HTML_BODY_TEMPLATE_RESOURCE_PATH, sut.getHtmlBodyTemplateResourcePath());
    }

    /**
     * Tests setters and getters.
     */
    @Test
    public void testPrimarySetters()
    {
        sut.setSubjectTemplate(SUBJECT_TEMPLATE);
        sut.setTextBodyTemplateResourcePath(TEXT_BODY_TEMPLATE_RESOURCE_PATH);
        sut.setHtmlBodyTemplateResourcePath(HTML_BODY_TEMPLATE_RESOURCE_PATH);

        verifyGetters();
    }

    /**
     * Tests setters and getters.
     */
    @Test
    public void testConvenienceSetters()
    {
        sut.setSubject(SUBJECT_TEMPLATE);
        sut.setTextBody(TEXT_BODY_TEMPLATE_RESOURCE_PATH);
        sut.setHtmlBody(HTML_BODY_TEMPLATE_RESOURCE_PATH);

        verifyGetters();
    }
}
