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
 * Partial response returned by a mapper, containing the response and a new request for another mapper to try.
 *
 * @param <Request>
 *            The request type
 * @param <Response>
 *            The response type
 */
public class PartialMapperResponse<Request, Response>
{
    /**
     * The new request to send to a subsequent mapper.
     */
    private Request unhandledRequest;

    /**
     * The response found by the mapper.
     */
    private Response response;

    /**
     * Constructor.
     *
     * @param inResponse
     *            the respnonse that was found by the mapper
     * @param inUnhandledRequest
     *            a new request object containing anything that the mapper couldn't find
     */
    public PartialMapperResponse(final Response inResponse, final Request inUnhandledRequest)
    {
        unhandledRequest = inUnhandledRequest;
        response = inResponse;
    }

    /**
     * Get the new request to send to a subsequent mapper.
     *
     * @return the new request to send to a subsequent mapper
     */
    public Request getUnhandledRequest()
    {
        return unhandledRequest;
    }

    /**
     * Get the response from this mapper.
     *
     * @return the response from this mapper.
     */
    public Response getResponse()
    {
        return response;
    }

    /**
     * Return whether the request was completely satisfied with the mapper.
     *
     * @return whether the request was completely satisfied with the mapper.
     */
    public boolean hasCompleteResponse()
    {
        return unhandledRequest == null;
    }
}
