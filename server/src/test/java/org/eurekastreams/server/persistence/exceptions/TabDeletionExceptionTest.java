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
package org.eurekastreams.server.persistence.exceptions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import org.eurekastreams.server.domain.Tab;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test the TabDeletionException class.
 */
public class TabDeletionExceptionTest
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
     * Test the TabDeletionException constructor with no cause exception.
     */
    @Test
    public void testConstructorNoCause()
    {
        String exMessage = "This statement is false.";
        Tab deletedTab = context.mock(Tab.class);

        TabDeletionException sut = new TabDeletionException(exMessage,
                deletedTab);

        assertSame("Didn't get the input deleted Tab from getTab()",
                deletedTab, sut.getTab());
        assertEquals(
                "Didn't get the input exception message from getMessage()",
                exMessage, sut.getMessage());
    }

    /**
     * Test the TabDeletionException constructor with an inner/cause exception.
     */
    @Test
    public void testConstructorWithCause()
    {
        String causeMessage = "Need a cool word for this unit test";
        String exMessage = "Found it: antepenultimate.";

        Tab deletedTab = context.mock(Tab.class);

        Exception cause = new Exception(causeMessage);
        TabDeletionException sut = new TabDeletionException(exMessage, cause,
                deletedTab);

        assertSame("Didn't get the input cause exception from getCause()",
                cause, sut.getCause());
        assertSame("Didn't get the input deleted Tab from getTab()",
                deletedTab, sut.getTab());
        assertEquals(
                "Didn't get the input exception message from getMessage()",
                exMessage, sut.getMessage());
    }
}
