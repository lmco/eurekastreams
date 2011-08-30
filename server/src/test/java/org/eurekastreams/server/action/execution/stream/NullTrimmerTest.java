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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertSame;

import java.util.List;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


/**
 * Tests NullTrimmer.
 */
public class NullTrimmerTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests trimming.
     */
    @Test
    public void test()
    {
        NullTrimmer sut = new NullTrimmer();

        // by using a mock, we can insure that the input list is not altered in any way
        List<Long> idsIn = context.mock(List.class);

        List<Long> idsOut = sut.trim(idsIn, null);

        context.assertIsSatisfied();
        assertSame(idsIn, idsOut);
    }
}
