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
package org.eurekastreams.web.client.events;

import java.io.Serializable;

import org.eurekastreams.web.client.model.BaseModel;

/**
 * Event raised the result of a message to the server is an exception.
 */
public class ExceptionResponseEvent
{
    /** The exception. */
    private final Throwable error;

    /** Which model made the request. */
    private final BaseModel model;

    /** The request. */
    private final Serializable request;

    /**
     * Constructor.
     *
     * @param inError
     *            The exception.
     * @param inModel
     *            Which model made the request.
     * @param inRequest
     *            The request.
     */
    public ExceptionResponseEvent(final Throwable inError, final BaseModel inModel, final Serializable inRequest)
    {
        error = inError;
        model = inModel;
        request = inRequest;
    }

    /**
     * @return the error
     */
    public Throwable getError()
    {
        return error;
    }

    /**
     * @return the model
     */
    public BaseModel getModel()
    {
        return model;
    }

    /**
     * @return the request
     */
    public Serializable getRequest()
    {
        return request;
    }
}
