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
package org.eurekastreams.web.client.events.data;

import java.io.Serializable;

/**
 * Indicates a request to the server failed.
 *
 * @param <R>
 *            Type of the request.
 */
public class BaseDataFailureEvent<R extends Serializable>
{
    /** The request. */
    private final R request;

    /** The exception. */
    private final Throwable exception;

    /**
     * Default constructor.
     * 
     * @param inRequest
     *            The request that failed.
     * @param inException
     *            TODO
     */
    public BaseDataFailureEvent(final R inRequest, final Throwable inException)
    {
        request = inRequest;
        exception = inException;
    }

    /**
     * Get the response.
     * 
     * @return the response.
     */
    public R getResponse()
    {
        return request;
    }

    /**
     * @return the exception
     */
    public Throwable getException()
    {
        return exception;
    }
}
