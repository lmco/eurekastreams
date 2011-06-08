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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for FeaturedStream.
 * 
 */
public class FeaturedStreamTest
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
     * System under test.
     */
    private FeaturedStream sut;

    /**
     * Date.
     */
    private Date date = context.mock(Date.class);

    /**
     * StreamScope.
     */
    private StreamScope streamScope = context.mock(StreamScope.class);

    /**
     * Description.
     */
    private String description = "description";

    /**
     * Test.
     */
    @Test
    public void testGetSet()
    {
        sut = new FeaturedStream();
        sut.setDescription(description);
        sut.setCreated(date);
        sut.setStreamScope(streamScope);

        assertEquals(description, sut.getDescription());
        assertEquals(date, sut.getCreated());
        assertEquals(streamScope, sut.getStreamScope());
    }

    /**
     * Test.
     */
    @Test
    public void testConstructor()
    {
        sut = new FeaturedStream(description, streamScope);

        assertEquals(description, sut.getDescription());
        assertEquals(null, sut.getCreated());
        assertEquals(streamScope, sut.getStreamScope());
    }
}
