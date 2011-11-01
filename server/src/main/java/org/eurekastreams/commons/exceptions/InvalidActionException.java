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
package org.eurekastreams.commons.exceptions;

/**
 * Generic exception to indicate an error invoking an action - either the action does not exist, or an attempt was made
 * to invoke a system-only action being invoked from a externally accessible interface (GWT RPC service or API, etc.),
 * or other improper/illegal invocation.
 */
public class InvalidActionException extends RuntimeException
{
    /** Fingerprint. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs new instance with null as its detailed message.
     */
    public InvalidActionException()
    {
        super();
    }

    /**
     * Constructs a new instance with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public InvalidActionException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs new instance with specified detailed message.
     *
     * @param message
     *            Detailed message.
     */
    public InvalidActionException(final String message)
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
    public InvalidActionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
