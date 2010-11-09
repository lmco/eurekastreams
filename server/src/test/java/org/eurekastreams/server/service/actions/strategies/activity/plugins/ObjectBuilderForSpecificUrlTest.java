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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests ObjectBuilderForSpecificUrl.
 */
public class ObjectBuilderForSpecificUrlTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: builder. */
    private final FeedObjectActivityBuilder builder = context.mock(FeedObjectActivityBuilder.class);

    /** SUT. */
    private ObjectBuilderForSpecificUrl sut;

    /**
     * Tests basic functionality.
     */
    @Test
    public void test()
    {
        sut = new ObjectBuilderForSpecificUrl("example.com", builder);

        assertTrue(sut.match("http://www.example.com/somepage"));
        assertFalse(sut.match("http://www.eurekastreams.org/somepage"));
        assertSame(builder, sut.getBuilder());
        context.assertIsSatisfied();
    }
}
