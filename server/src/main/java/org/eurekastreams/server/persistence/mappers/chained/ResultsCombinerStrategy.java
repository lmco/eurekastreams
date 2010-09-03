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
 * Strategy interface for combining two mapper results.
 * 
 * @param <Request>
 *            the request type.
 * @param <Response>
 *            The type of responses to combine
 */
public interface ResultsCombinerStrategy<Request, Response>
{
    /**
     * Combine two responses.
     * 
     * @param response1
     *            response to combine
     * @param response2
     *            response to combine
     * @param request
     *            the original request.
     * @return a combined response
     */
    Response combine(final PartialMapperResponse<Request, Response> response1,
            final Response response2, Request request);
}
