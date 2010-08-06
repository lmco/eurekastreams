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
package org.eurekastreams.server.persistence.mappers.chained;

/**
 * Strategy interface for updating a data source with results found in a different mapper.
 *
 * @param <Request>
 *            the type of request that generated the response
 * @param <Response>
 *            the type of response to feed back into the data source
 */
public interface RefreshStrategy<Request, Response>
{
    /**
     * Refresh the data source with the input response which was found in another data source from the input request.
     *
     * @param request
     *            the request that generated the response
     * @param respose
     *            the response to feed back into the data source
     */
    void refresh(final Request request, final Response respose);
}
