/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.services;

import java.io.Serializable;

/**
 * The container structure of the responses in the preauth Action API.
 */
public class ActionApiTransport
{
    /** Result of action - either the data or an exception. */
    private Serializable result;

    /** If action was successful. */
    private boolean success;

    /**
     * @return the result
     */
    public Serializable getResult()
    {
        return result;
    }

    /**
     * @param inResult
     *            the result to set
     */
    public void setResult(final Serializable inResult)
    {
        result = inResult;
    }

    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * @param inSuccess
     *            the success to set
     */
    public void setSuccess(final boolean inSuccess)
    {
        success = inSuccess;
    }
}
