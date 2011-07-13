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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for HashtagLinkTransformer.
 */
public class HashtagLinkTransformerTest
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
     * Mocked StreamSearchLinkBuilder.
     */
    private final StreamSearchLinkBuilder linkBuilder = context.mock(StreamSearchLinkBuilder.class);

    /**
     * System under test.
     */
    private final HashtagLinkTransformer sut = new HashtagLinkTransformer(linkBuilder);

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes1()
    {
        String input = "hello there, what's up?";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes2()
    {
        String input = "hello there, #(what)'s up?";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes3()
    {
        String input = "nothing to see here: www.foo.bar/hi#what, move along";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes4()
    {
        String input = "nothing to see here: ww#what, move along";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes5()
    {
        String input = "nothing to see here: ww/#what, move along";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes6()
    {
        String input = "nothing to see here: ww.com/#what, move along";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoHashes7()
    {
        String input = "no <a href=\"foo.com\"> foo #bar</a>";
        String expected = input;
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag1()
    {
        String input = "hello there, #what's up?";
        String expected = "hello there, <a href=\"FOOBAR-what\">#what</a>'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#what", null);
                will(returnValue("FOOBAR-what"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag2()
    {
        String input = "hello there, #what-is's up?";
        String expected = "hello there, <a href=\"FOOBAR-what-is\">#what-is</a>'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#what-is", null);
                will(returnValue("FOOBAR-what-is"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag3()
    {
        String input = "hello there, #what_is's up?";
        String expected = "hello there, <a href=\"FOOBAR-what_is\">#what_is</a>'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#what_is", null);
                will(returnValue("FOOBAR-what_is"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag4()
    {
        String input = "hello there, #12345's up?";
        String expected = "hello there, <a href=\"FOOBAR-12345\">#12345</a>'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#12345", null);
                will(returnValue("FOOBAR-12345"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag5()
    {
        String input = "hello there, (#12345)'s up?";
        String expected = "hello there, (<a href=\"FOOBAR-12345\">#12345</a>)'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#12345", null);
                will(returnValue("FOOBAR-12345"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag6()
    {
        String input = "hello there, #1(2345)'s up?";
        String expected = "hello there, <a href=\"FOOBAR-1\">#1</a>(2345)'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#1", null);
                will(returnValue("FOOBAR-1"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag7()
    {
        String input = "hello there, ##heynow's up?";
        String expected = "hello there, #<a href=\"FOOBAR-heynow\">#heynow</a>'s up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#heynow", null);
                will(returnValue("FOOBAR-heynow"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag8()
    {
        String input = "hello www.com##foo";
        String expected = "hello www.com#<a href=\"FOOBAR-foo\">#foo</a>";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#foo", null);
                will(returnValue("FOOBAR-foo"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test with an href'd hyperlink already in the content - that should be ignored.
     */
    @Test
    public void testWithHashTag9()
    {
        String input = "no <a href=\"foo.com\"> foo #bar</a> #way!";
        String expected = "no <a href=\"foo.com\"> foo #bar</a> <a href=\"FOOBAR-way\">#way</a>!";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#way", null);
                will(returnValue("FOOBAR-way"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test with an href'd hyperlink already in the content, with a hashtag in that url - that should be ignored.
     */
    @Test
    public void testWithHashTag10()
    {
        String input = "no <a href=\"foo.com#bar\"> foo #bar</a> #way!";
        String expected = "no <a href=\"foo.com#bar\"> foo #bar</a> <a href=\"FOOBAR-way\">#way</a>!";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#way", null);
                will(returnValue("FOOBAR-way"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test with an href'd hyperlink - should be ignored.
     */
    @Test
    public void testWithHashTag11()
    {
        String input = "no <a href=\"http://www.google.com#foo\">http://www.google.com#foo</a> #way!";
        String expected = "no <a href=\"http://www.google.com#foo\">http://www.google.com#foo</a> "
                + "<a href=\"FOOBAR-way\">#way</a>!";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#way", null);
                will(returnValue("FOOBAR-way"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithHashTag12()
    {
        String input = "Here goes... #woot#woot!!";
        String expected = "Here goes... <a href=\"heynow\">#woot</a>#woot!!";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#woot", null);
                will(returnValue("heynow"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithMultipleHashTags()
    {
        String input = "hello there, #what's #foo up?";
        String expected = "hello there, <a href=\"FOOBAR-what\">#what</a>'s <a href=\"FOOBAR-foo\">#foo</a> up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#what", null);
                will(returnValue("FOOBAR-what"));

                oneOf(linkBuilder).buildHashtagSearchLink("#foo", null);
                will(returnValue("FOOBAR-foo"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithOneHashtagAtBeginningOfLine()
    {
        String input = "#hello there, what's up?";
        String expected = "<a href=\"FOOBAR-hello\">#hello</a> there, what's up?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#hello", null);
                will(returnValue("FOOBAR-hello"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testEndingWithHashtag()
    {
        String input = "hello there, what's #up";
        String expected = "hello there, what's <a href=\"FOOBAR-up\">#up</a>";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#up", null);
                will(returnValue("FOOBAR-up"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtag1()
    {
        String input = "Check out this cool link - http://eurekastreams.org#hey-now - awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtag2()
    {
        String input = "Check out this cool link - ftp://eurekastreams.org#hey-now - awesome, eh?";
        String expected = "Check out this cool link - ftp://eurekastreams.org#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtag3()
    {
        String input = "Check out this cool link - abcdefg://eurekastreams.org#hey-now - awesome, eh?";
        String expected = "Check out this cool link - abcdefg://eurekastreams.org#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtag4()
    {
        String input = "Check out this cool link - http://eurekastreams.org?foo=bar#hey-now - awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org?foo=bar#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtag5()
    {
        String input = "Check out this cool link - http://eurekastreams.org:1337?t=hi+boo&s=(hi%20)/[sd].&#hey-now "
                + "-#heh awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org:1337?t=hi+boo&s=(hi%20)/[sd].&#hey-now"
                + " -<a href=\"FOOBAR-heh\">#heh</a> awesome, eh?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#heh", null);
                will(returnValue("FOOBAR-heh"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterAValidHashTag()
    {
        String input = "Check out this cool #link - http://eurekastreams.org#hey-now - awesome, eh?";
        String expected = "Check out this cool <a href=\"FOOBAR-link\">#link</a>"
                + " - http://eurekastreams.org#hey-now - awesome, eh?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#link", null);
                will(returnValue("FOOBAR-link"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterSlash()
    {
        String input = "Check out this cool link - http://eurekastreams.org/#hey-now - awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org/#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterSlashAfterAValidHashTag()
    {
        String input = "Check out this cool #link - http://eurekastreams.org/#hey-now - awesome, eh?";
        String expected = "Check out this cool <a href=\"FOOBAR-link\">#link</a>"
                + " - http://eurekastreams.org/#hey-now - awesome, eh?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#link", null);
                will(returnValue("FOOBAR-link"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterQuestionMark()
    {
        String input = "Check out this cool link - http://eurekastreams.org?#hey-now - awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org?#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterQuestionMarkAValidHashTag()
    {
        String input = "Check out this cool #link - http://eurekastreams.org?#hey-now - awesome, eh?";
        String expected = "Check out this cool <a href=\"FOOBAR-link\">#link</a>"
                + " - http://eurekastreams.org?#hey-now - awesome, eh?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#link", null);
                will(returnValue("FOOBAR-link"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterPeriod()
    {
        String input = "Check out this cool link - http://eurekastreams.org?#hey-now - awesome, eh?";
        String expected = "Check out this cool link - http://eurekastreams.org?#hey-now - awesome, eh?";
        assertEquals(expected, sut.transform(input));
    }

    /**
     * Test.
     */
    @Test
    public void testWithUrlHavingHashtagAfterPeriodAValidHashTag()
    {
        String input = "Check out this cool #link - http://eurekastreams.org?#hey-now - awesome, eh?";
        String expected = "Check out this cool <a href=\"FOOBAR-link\">#link</a>"
                + " - http://eurekastreams.org?#hey-now - awesome, eh?";

        context.checking(new Expectations()
        {
            {
                oneOf(linkBuilder).buildHashtagSearchLink("#link", null);
                will(returnValue("FOOBAR-link"));
            }
        });

        assertEquals(expected, sut.transform(input));
    }
}
