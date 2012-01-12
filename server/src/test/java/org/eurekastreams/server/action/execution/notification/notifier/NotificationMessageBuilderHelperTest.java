/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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

import java.util.HashMap;

import org.apache.velocity.context.Context;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests NotificationMessageBuilderHelper.
 */
public class NotificationMessageBuilderHelperTest
{
    /** Test data. */
    private static final String BASE_URL = "http://demo.eurekastreams.org";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Velocity context. */
    private final Context velocityContext = context.mock(Context.class, "velocityContext");

    /** SUT. */
    private NotificationMessageBuilderHelper sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new NotificationMessageBuilderHelper(BASE_URL);
    }

    /**
     * Tests resolveActivityBody.
     */
    @Test
    public void testResolveActivityBody()
    {
        final ActivityDTO activity = context.mock(ActivityDTO.class, "activity");
        final StreamEntityDTO actor = context.mock(StreamEntityDTO.class, "actor");
        final HashMap<String, String> baseObjectProps = new HashMap<String, String>();
        context.checking(new Expectations()
        {
            {
                allowing(activity).getBaseObjectProperties();
                will(returnValue(baseObjectProps));
                allowing(activity).getActor();
                will(returnValue(actor));
                allowing(actor).getDisplayName();
                will(returnValue("John Doe"));
            }
        });
        activity.getBaseObjectProperties().put("content", "Blah %EUREKA:ACTORNAME% blah %EUREKA:NOSUCH% blah.");

        String result = sut.resolveActivityBody(activity, velocityContext);

        context.assertIsSatisfied();

        assertEquals("Blah John Doe blah %EUREKA:NOSUCH% blah.", result);
    }

    /**
     * Test.
     */
    @Test
    public void testCleanWhitespace()
    {
        assertEquals(" \t Blah blah \n blah \n blah blah.",
                sut.cleanWhitespace(" \t \r\n \t Blah blah \n blah \n blah blah. \n \n "));
    }

    /**
     * Tests resolveActivityBody.
     */
    @Test
    public void testCleanWhitespaceAllBlank()
    {
        assertEquals("", sut.cleanWhitespace("  \t  \r\n \r \n  "));
    }

    /**
     * Tests resolveActivityBody.
     */
    @Test
    public void testCleanWhitespaceEmpty()
    {
        assertEquals("", sut.cleanWhitespace(""));
    }

    /**
     * Tests resolveMarkdownForText.
     */
    @Test
    public void testResolveMarkdownForText()
    {
        String result = sut.resolveMarkdownForText("Pre-stuff [Link1](#dest1) Middle [Link2](http://xyz/abc) After");
        assertEquals("Pre-stuff Link1 (" + BASE_URL + "#dest1) Middle Link2 (http://xyz/abc) After", result);
    }

    /**
     * Tests resolveMarkdownForText.
     */
    @Test
    public void testResolveMarkdownForTextOnlyLinks()
    {
        String result = sut.resolveMarkdownForText("[Link1](#dest1)[Link2](http://xyz/abc)");
        assertEquals("Link1 (" + BASE_URL + "#dest1)Link2 (http://xyz/abc)", result);
    }

    /**
     * Tests resolveMarkdownForText.
     */
    @Test
    public void testResolveMarkdownForTextNoLinks()
    {
        String input = "A [Link1] (http://xyz/abc) B";
        String result = sut.resolveMarkdownForText(input);
        assertEquals(input, result);
    }

    /**
     * Tests resolveMarkdownForHtml.
     */
    @Test
    public void testResolveMarkdownForHtml()
    {
        String input = "Bef&ore [Link&1](#dest1?a=b&c=d) Mid&dle [Link&2](http://xyz/abc?x=y&z=w) Aft&er";
        String result = sut.resolveMarkdownForHtml(input);
        assertEquals("Bef&amp;ore <a href=\"" + BASE_URL + "#dest1?a=b&amp;c=d\">Link&amp;1</a> Mid&amp;dle "
                + "<a href=\"http://xyz/abc?x=y&amp;z=w\">Link&amp;2</a> Aft&amp;er", result);
    }

    /**
     * Tests resolveMarkdownForHtml.
     */
    @Test
    public void testResolveMarkdownForHtmlOnlyLinks()
    {
        String result = sut.resolveMarkdownForHtml("[Link&1](#dest1?a=b&c=d)[Link&2](http://xyz/abc?x=y&z=w)");
        assertEquals("<a href=\"" + BASE_URL + "#dest1?a=b&amp;c=d\">Link&amp;1</a>"
                + "<a href=\"http://xyz/abc?x=y&amp;z=w\">Link&amp;2</a>", result);
    }

    /**
     * Tests resolveMarkdownForHtml.
     */
    @Test
    public void testResolveMarkdownForHtmlNoLinks()
    {
        String result = sut.resolveMarkdownForHtml("A&A [Link&1] (http://xyz/abc?a=1&b=2) B&B");
        assertEquals("A&amp;A [Link&amp;1] (http://xyz/abc?a=1&amp;b=2) B&amp;B", result);
    }
}
