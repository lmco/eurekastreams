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
package org.eurekastreams.commons.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test for ExecutionException.
 * 
 */
public class ExecutionExceptionTest
{
    /**
     * Test empty constructor.
     */
    @Test
    public void testEmptyConstructor()
    {
        ExecutionException sut = new ExecutionException();
        assertNull(sut.getMessage());
    }

    /**
     * Test constructor w/message.
     */
    @Test
    public void testConstructorWithMessage()
    {
        ExecutionException sut = new ExecutionException("msg");
        assertEquals("msg", sut.getMessage());
    }

    /**
     * Test constructor w/Throwable.
     */
    @Test
    public void testConstructorWithThrowable()
    {
        RuntimeException cause = new RuntimeException("cause");
        ExecutionException sut = new ExecutionException(cause);
        assertEquals(cause, sut.getCause());
    }

    /**
     * Test constructor w/message and throwable.
     */
    @Test
    public void testConstructorWithMessageAndThrowable()
    {
        RuntimeException cause = new RuntimeException("cause");
        ExecutionException sut = new ExecutionException("msg", cause);
        assertEquals("msg", sut.getMessage());
        assertEquals(cause, sut.getCause());
    }
}
