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

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for HyperlinkTransformer.
 */
public class HyperlinkTransformerTest
{

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked WidgetJSNIFacadeImpl.
     */
    private WidgetJSNIFacadeImpl jsni = context.mock(WidgetJSNIFacadeImpl.class);

    /**
     * System under test.
     */
    private HyperlinkTransformer sut = new HyperlinkTransformer(jsni);

    /**
     * Test with no content.
     */
    @Test
    public void test1()
    {
        final String input = "";
        final String expected = "";
        performTest(input, expected);
    }

    /**
     * Test with http link.
     */
    @Test
    public void testWithHttp()
    {
        final String input = "foo http://eurekastreams.org bar";
        final String expected = "foo <a target=\"_blank\" href=\"http://eurekastreams.org\">"
                + "http://eurekastreams.org</a> bar";
        performTest(input, expected);
    }

    /**
     * Test with https link.
     */
    @Test
    public void testWithHttps()
    {
        final String input = "foo https://eurekastreams.org bar";
        final String expected = "foo <a target=\"_blank\" href=\"https://eurekastreams.org\">"
                + "https://eurekastreams.org</a> bar";
        performTest(input, expected);
    }

    /**
     * Test with no www link.
     */
    @Test
    public void testWithWww()
    {
        final String input = "foo www.eurekastreams.org bar";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a> bar";
        performTest(input, expected);
    }

    /**
     * Test with link only.
     */
    @Test
    public void testWithLinkOnly()
    {
        final String input = "www.eurekastreams.org";
        final String expected = "<a target=\"_blank\" href=\"http://www.eurekastreams.org\">www.eurekastreams.org</a>";
        performTest(input, expected);
    }

    /**
     * Test with link only, inside parens.
     */
    @Test
    public void testWithLinkAndParens()
    {
        final String input = "(www.eurekastreams.org)";
        final String expected = "(<a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>)";
        performTest(input, expected);
    }

    /**
     * Test with link only, and closing paren.
     */
    @Test
    public void testWithLinkAndClosingParens()
    {
        final String input = "www.eurekastreams.org)";
        final String expected = "<a target=\"_blank\" href=\"http://www.eurekastreams.org)\">"
                + "www.eurekastreams.org)</a>";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test2()
    {
        final String input = "foo (www.eurekastreams.org ).";
        final String expected = "foo (<a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a> ).";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test3()
    {
        final String input = "foo (www.eurekastreams.org )\n";
        final String expected = "foo (<a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a> )\n";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test4()
    {
        final String input = "foo (www.eurekastreams.org ";
        final String expected = "foo (<a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a> ";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test5()
    {
        final String input = "foo www.eurekastreams.org/. ";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org/\">"
                + "www.eurekastreams.org/</a>. ";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test6()
    {
        final String input = "foo www.eurekastreams.org\n";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>\n";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test7()
    {
        final String input = "foo www.eurekastreams.org.";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>.";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test8()
    {
        final String input = "foo www.eurekastreams.org.\n";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>.\n";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test9()
    {
        final String input = "foo www.eurekastreams.org)\n";
        final String expected = "foo <a target=\"_blank\" href=\"http://www.eurekastreams.org)\">"
                + "www.eurekastreams.org)</a>\n";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test10()
    {
        final String input = "foo ( www.eurekastreams.org) ";
        final String expected = "foo ( <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>) ";
        performTest(input, expected);
    }

    /**
     * Test parens fake-out.
     */
    @Test
    public void test11()
    {
        final String input = "foo (hi) www.eurekastreams.org) ";
        final String expected = "foo (hi) <a target=\"_blank\" href=\"http://www.eurekastreams.org)\">"
                + "www.eurekastreams.org)</a> ";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test12()
    {
        final String input = "foo (hi) www.eurekastreams.o(rg) ";
        final String expected = "foo (hi) <a target=\"_blank\" href=\"http://www.eurekastreams.o(rg)\">"
                + "www.eurekastreams.o(rg)</a> ";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test13()
    {
        final String input = "foo (hi) www.eurekastreams.o(rg)";
        final String expected = "foo (hi) <a target=\"_blank\" href=\"http://www.eurekastreams.o(rg)\">"
                + "www.eurekastreams.o(rg)</a>";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test14()
    {
        final String input = "www.eurekastreams.org www.eurekastreams.org www.eurekastreams.org www.eurekastreams.org";
        final String expected = "<a target=\"_blank\" href=\"http://www.eurekastreams.org\">www.eurekastreams.org</a> "
                + "<a target=\"_blank\" href=\"http://www.eurekastreams.org\">www.eurekastreams.org</a> "
                + "<a target=\"_blank\" href=\"http://www.eurekastreams.org\">www.eurekastreams.org</a> "
                + "<a target=\"_blank\" href=\"http://www.eurekastreams.org\">www.eurekastreams.org</a>";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test15()
    {
        final String input = "((foo http://www.eurekastreams.org))";
        final String expected = "((foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "http://www.eurekastreams.org</a>))";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test16()
    {
        final String input = "((foo http://www.eurekastreams.org))))))";
        final String expected = "((foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "http://www.eurekastreams.org</a>))))))";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test17()
    {
        final String input = "((foo http://www.eurekastreams.org))))a foo))";
        final String expected = "((foo <a target=\"_blank\" href=\"http://www.eurekastreams.org))))a\">"
                + "http://www.eurekastreams.org))))a</a> foo))";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test18()
    {
        final String input = "(foo www.eurekastreams.org), bar";
        final String expected = "(foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>), bar";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test19()
    {
        final String input = "(foo www.eurekastreams.org),\nbar";
        final String expected = "(foo <a target=\"_blank\" href=\"http://www.eurekastreams.org\">"
                + "www.eurekastreams.org</a>),\nbar";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test20()
    {
        final String input = "( http://www.eurekastreams.org)abc http://www.eurekastreams.org)abc)";
        final String expected = "( <a target=\"_blank\" href=\"http://www.eurekastreams.org)abc\">"
                + "http://www.eurekastreams.org)abc</a> <a target=\"_blank\" href=\"http://www.eurekastreams.org)abc\">"
                + "http://www.eurekastreams.org)abc</a>)";
        performTest(input, expected);
    }

    /**
     * Test.
     */
    @Test
    public void test21()
    {
        final String input = "Nested (http://nested.a.test.com ad asdfa http://www.google.com.";
        final String expected = "Nested (<a target=\"_blank\" href=\"http://nested.a.test.com\">"
                + "http://nested.a.test.com</a>"
                + " ad asdfa <a target=\"_blank\" href=\"http://www.google.com\">http://www.google.com</a>.";
        performTest(input, expected);
    }

    /**
     * Perform the test.
     *
     * @param input
     *            the input into the transformer
     * @param expected
     *            the expected output
     */
    private void performTest(final String input, final String expected)
    {
        context.checking(new Expectations()
        {
            {
                one(jsni).escapeHtml(input);
                will(returnValue(input));
            }
        });

        String output = sut.transform(input);
        assertEquals(expected, output);
    }
}
