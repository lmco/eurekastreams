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

/**
 * Exception to be thrown if user's session is expired.
 * 
 */
public class SessionException extends RuntimeException
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 7030150214564382535L;

    /**
     * Constructs new SessionExpiredException with null as its detailed message.
     */
    public SessionException()
    {
        super();
    }

    /**
     * Constructs a new instance with the specified cause.
     * 
     * @param cause
     *            the cause
     */
    public SessionException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs new SessionExpiredException with specified detailed message.
     * 
     * @param message
     *            Detailed message.
     */
    public SessionException(final String message)
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
    public SessionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
