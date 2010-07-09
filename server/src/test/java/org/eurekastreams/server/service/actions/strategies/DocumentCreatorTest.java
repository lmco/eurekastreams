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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test fixture for DocumentCreator.
 */
public class DocumentCreatorTest
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
     * Test the execute() method, making sure that it uses the constructor-fed ResourceFetcher to return an input stream
     * from the input file, then return an XML Document from its contents.
     * 
     * @throws Exception
     *             on error
     */
    @Test
    public void testExecute() throws Exception
    {
        final ResourceFetcher resourceFetcher = context.mock(ResourceFetcher.class);
        final String fileName = "sdlkfjsdljfdsj.xml";

        DocumentCreator sut = new DocumentCreator(resourceFetcher);

        context.checking(new Expectations()
        {
            {
                one(resourceFetcher).getInputStream(fileName);
                will(returnValue(new ByteArrayInputStream("<xml><foo rel=\"bar\"/></xml>".getBytes())));
            }
        });

        Document doc = sut.execute(fileName);
        assertEquals(1, doc.getChildNodes().getLength());
        assertEquals(1, doc.getElementsByTagName("xml").getLength());
        assertEquals(1, doc.getElementsByTagName("foo").getLength());
        assertEquals("bar", doc.getElementsByTagName("foo").item(0).getAttributes().item(0).getTextContent());
    }
}
