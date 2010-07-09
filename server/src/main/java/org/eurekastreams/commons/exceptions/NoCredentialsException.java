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
package org.eurekastreams.commons.exceptions;

/**
 * Exception to be thrown if action that requires credentials is attempted anonymously.
 * 
 */
public class NoCredentialsException extends RuntimeException
{

    /**
     * Serialization uid.
     */
    private static final long serialVersionUID = 7241382958805870419L;

    /**
     * Constructs new NoCredentialsException with null as its detailed message.
     */
    public NoCredentialsException()
    {
        super();
    }

    /**
     * Constructs a new instance with the specified cause.
     * 
     * @param cause
     *            the cause
     */
    public NoCredentialsException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs new NoCredentialsException with specified detailed message.
     * 
     * @param message
     *            Detailed message.
     */
    public NoCredentialsException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new instance with the specified message and cause.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public NoCredentialsException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
