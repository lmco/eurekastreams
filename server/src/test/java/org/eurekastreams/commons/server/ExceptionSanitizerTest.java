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
package org.eurekastreams.commons.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.InvalidActionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Tests ExceptionSanitizer.
 */
public class ExceptionSanitizerTest
{
    /**
     * Common parts of tests that insure exceptions do not contain a nested cause.
     *
     * @param inputException
     *            Exception to be thrown.
     * @return Exception returned by SUT.
     */
    private Exception coreForbidNestingExceptionTest(final Exception inputException)
    {
        Transformer<Exception, Exception> sut = new ExceptionSanitizer();
        Exception outputException = sut.transform(inputException);
        assertNull(outputException.getCause());
        return outputException;
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testExecutionExceptionNested()
    {
        Exception exIn = new ExecutionException(new ArithmeticException());
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof ExecutionException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testExecutionExceptionNonNested()
    {
        Exception exIn = new ExecutionException();
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testGeneralExceptionNested()
    {
        Exception exIn = new GeneralException(new ArithmeticException());
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testGeneralExceptionNonNested()
    {
        Exception exIn = new GeneralException();
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testAuthorizationExceptionNested()
    {
        Exception exIn = new AuthorizationException(new ArithmeticException());
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof AuthorizationException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testAuthorizationExceptionNonNested()
    {
        Exception exIn = new AuthorizationException();
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testValidationExceptionNonNested()
    {
        Exception exIn = new ValidationException();
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testBadBeanException()
    {
        Exception exIn = new NoSuchBeanDefinitionException("Spring's message");
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertTrue(exOut.getCause() == null || exOut.getCause() == exOut);
        assertTrue(!exOut.getMessage().equals(exIn.getMessage()));
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testBadActionException()
    {
        Exception exIn = new InvalidActionException("Inner message");
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertTrue(exOut.getCause() == null || exOut.getCause() == exOut);
        assertTrue(!exOut.getMessage().equals(exIn.getMessage()));
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testOtherExceptionNonNested()
    {
        Exception exIn = new IllegalArgumentException("bad");
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertTrue(!exIn.getMessage().equals(exOut.getMessage()));
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testOtherExceptionNested()
    {
        final IllegalStateException exCause = new IllegalStateException("really bad");
        Exception exIn = new IllegalArgumentException(exCause);
        Exception exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertTrue(!exIn.getMessage().equals(exOut.getMessage()));
        assertTrue(!exCause.getMessage().equals(exOut.getMessage()));
    }
}
