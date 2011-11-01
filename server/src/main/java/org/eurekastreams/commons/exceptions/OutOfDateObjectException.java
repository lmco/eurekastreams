/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
 * Exception to be thrown on attempt to modify a persistent object that was modified by another thread.
 */
public class OutOfDateObjectException extends RuntimeException 
{

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -386727221898326562L;

	/**
	 * Default constructor.
	 */
	public OutOfDateObjectException() 
	{
		
	}

	/**
     * Constructs a new instance with the specified message.
     * 
     * @param message
     *            the detailed message
     */
	public OutOfDateObjectException(final String message) 
	{
		super(message);
	}

	/**
     * Constructs a new instance with the specified cause.
     * 
     * @param cause
     *            the cause
     */
	public OutOfDateObjectException(final Throwable cause) 
	{
		super(cause);
	}

	/**
	 * Constructs a new instance with the specified cause and detailed message.
	 * 
	 * @param message 
	 * 				the detailed message
	 * @param cause
	 * 				the cause
	 */
	public OutOfDateObjectException(final String message, final Throwable cause) 
	{
		super(message, cause);
	}

}
