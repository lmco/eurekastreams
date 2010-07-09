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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndContentImpl;

/**
 * ActivityStreamsModuleParser Test.
 *
 */
public class ActivityStreamsModuleParserTest
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
     * System under test.
     */
    private ActivityStreamsModuleParser sut = new ActivityStreamsModuleParser();

    /**
     * Test the getNamespaceUri function.
     */
    @Test
    public void getNamespaceUri()
    {
        assertEquals(ActivityStreamsModule.URI, sut.getNamespaceUri());
    }

    /**
     * Test parse.
     */
    @Test
    public void parse()
    {
        final String titleVal = "myTitle";
        final String contentVal = "myContent";
        final String linkVal = "myLink";
        final String summaryVal = "mySummary";
        final String objectTypeVal = "this/is/note";
        final String objectTypeError = "this/is/nothing";
        final String expectedObjType = "NOTE";

        final Element root = context.mock(Element.class);
        final Element objElement = context.mock(Element.class, "obj");
        final Element titleChild = context.mock(Element.class, "title");
        final Element contentChild = context.mock(Element.class, "content");
        final Element linkChild = context.mock(Element.class, "link");
        final Element summaryChild = context.mock(Element.class, "summary");
        final Element objTypeChild = context.mock(Element.class, "objTypeError");
        final Element objTypeErrorChild = context.mock(Element.class, "objType");

        final List<Element> children = new LinkedList<Element>();
        children.add(titleChild);
        children.add(contentChild);
        children.add(linkChild);
        children.add(summaryChild);
        children.add(objTypeChild);
        children.add(objTypeErrorChild);

        context.checking(new Expectations()
        {
            {
                oneOf(root).getChild(with(any(String.class)), with(any(Namespace.class)));
                will(returnValue(objElement));

                oneOf(objElement).getChildren();
                will(returnValue(children));

                allowing(titleChild).getName();
                will(returnValue("title"));
                oneOf(titleChild).getTextTrim();
                will(returnValue(titleVal));

                allowing(contentChild).getName();
                will(returnValue("content"));
                oneOf(contentChild).getTextTrim();
                will(returnValue(contentVal));

                allowing(linkChild).getName();
                will(returnValue("link"));
                oneOf(linkChild).getAttributeValue("href");
                will(returnValue(linkVal));

                allowing(summaryChild).getName();
                will(returnValue("summary"));
                oneOf(summaryChild).getTextTrim();
                will(returnValue(summaryVal));

                allowing(objTypeChild).getName();
                will(returnValue("object-type"));
                oneOf(objTypeChild).getTextTrim();
                will(returnValue(objectTypeVal));

                allowing(objTypeErrorChild).getName();
                will(returnValue("object-type"));
                oneOf(objTypeErrorChild).getTextTrim();
                will(returnValue(objectTypeError));
            }
        });

        ActivityStreamsModuleImpl module = (ActivityStreamsModuleImpl) sut.parse(root);

        assertEquals(titleVal, module.getAtomEntry().getTitle());
        assertEquals(contentVal, ((SyndContentImpl) module.getAtomEntry().getContents().get(0)).getValue());
        assertEquals(linkVal, module.getAtomEntry().getLink());
        assertEquals(summaryVal, module.getAtomEntry().getDescription().getValue());
        assertEquals(expectedObjType, module.getObjectType());
    }
}
