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

import java.util.ArrayList;
import java.util.List;

/**
 * Combines two collections.
 * 
 * @param <Request>
 *            the request type.
 * @param <Type>
 *            the type of list objects to combine
 */
public class CollectionCombiner<Request, Type> implements ResultsCombinerStrategy<List<Request>, List<Type>>
{
    /**
     * Combine two lists.
     * 
     * @param partialResponse
     *            the partial response.
     * @param response2
     *            the second collection.
     * @param request
     *            the original request.
     * @return the combined collection.
     */
    public List<Type> combine(final PartialMapperResponse<List<Request>, List<Type>> partialResponse,
            final List<Type> response2, final List<Request> request)
    {
        List<Type> allItems = new ArrayList<Type>();

        int partialIndex = 0;
        int remainingIndex = 0;

        for (Request req : request)
        {
            if (partialResponse.getUnhandledRequest().contains(req))
            {
                allItems.add(response2.get(remainingIndex));
                remainingIndex++;
            }
            else
            {
                allItems.add(partialResponse.getResponse().get(partialIndex));
                partialIndex++;
            }
        }

        return allItems;
    }

}
