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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for StreamSearch object.
 *
 */
public class StreamSearchTest
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
     * keyword set.
     */
    private HashSet<String> keywords;
    
    /**
     * StreamView mock.
     */
    private StreamView streamView = context.mock(StreamView.class);
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        keywords = new HashSet<String>(2);
        keywords.add("foo");
        keywords.add("bar");
        
    }
    
    /**
     * Test Constructor.
     */
    @Test
    public void testConstructorParams()
    {
        StreamSearch sut = new StreamSearch("test", streamView, keywords);
        
        assertNotNull(sut.getStreamView());
        assertTrue(sut.getKeywords().contains("foo"));
        assertTrue(sut.getKeywords().contains("bar"));
        assertEquals("test", sut.getName());
    }
    
    /**
     * Test getters/setters.
     */
    @Test
    public void testSetGet()
    {
        StreamSearch sut = new StreamSearch("test", streamView, keywords);
        sut.setStreamView(null);
        sut.setKeywords(null);
        sut.setName(null);
        
        assertNull(sut.getStreamView());
        assertNull(sut.getKeywords()); 
        assertNull(sut.getName()); 
    }

    /**
     * Test getAsString method.
     */
    @Test
    public void testGetAsString()
    {
        StreamSearch sut = new StreamSearch("test", streamView, keywords);
        assertEquals("bar foo", sut.getKeywordsAsString());
        
        sut = new StreamSearch("test", streamView, new HashSet<String>());
        assertEquals("", sut.getKeywordsAsString());
    }
}
