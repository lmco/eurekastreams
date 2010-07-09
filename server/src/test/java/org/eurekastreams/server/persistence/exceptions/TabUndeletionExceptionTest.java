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

import org.junit.Test;

/**
 * Test the TabUndeletionException class.
 */
public class TabUndeletionExceptionTest
{
    /**
     * Test the constructor with no embedded/cause exception.
     */
    @Test
    public void testConstructorNoCause()
    {
        String exMessage = "Witty exception message.";
        final long tabId = 317L;

        TabUndeletionException sut = new TabUndeletionException(exMessage,
                tabId);

        assertEquals("Didn't get the input deleted Tab from getTab()", tabId,
                sut.getTabId());
        assertEquals(
                "Didn't get the input exception message from getMessage()",
                exMessage, sut.getMessage());
    }

    /**
     * Test the constructor with an embedded/cause exception.
     */
    @Test
    public void testConstructorWithCause()
    {
        String causeMessage = "How do you know so much about swallows?";
        String exMessage = "Well, you have to know these things when you're a king, you know.";
        final long tabId = 1031L;

        Exception cause = new Exception(causeMessage);
        TabUndeletionException sut = new TabUndeletionException(exMessage,
                cause, tabId);

        assertSame("Didn't get the input cause exception from getCause()",
                cause, sut.getCause());
        assertEquals("Didn't get the input deleted Tab from getTab()", tabId,
                sut.getTabId());
        assertEquals(
                "Didn't get the input exception message from getMessage()",
                exMessage, sut.getMessage());
    }
}
