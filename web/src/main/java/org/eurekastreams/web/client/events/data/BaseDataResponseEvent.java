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
package org.eurekastreams.web.client.events.data;

import java.io.Serializable;

/**
 * Represents a normal response event from the server.
 *
 * @param <R> response type.
 */
public class BaseDataResponseEvent<R extends Serializable>
{
    /**
     * The response.
     */
    R response;

    /**
     * Default constructor.
     * @param inResponse the response.
     */
    public BaseDataResponseEvent(final R inResponse)
    {
        response = inResponse;
    }

    /**
     * Get the response.
     * @return the response.
     */
    public R getResponse()
    {
        return response;
    }
}
