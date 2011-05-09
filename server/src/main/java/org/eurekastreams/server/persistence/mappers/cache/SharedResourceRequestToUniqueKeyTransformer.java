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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.action.request.SharedResourceRequest;

/**
 * Transformer to extract the shared resource's unique key (lower-cased) from a SharedResourceRequest.
 */
public class SharedResourceRequestToUniqueKeyTransformer implements Transformer<SharedResourceRequest, String>
{
    /**
     * Return the unique key from a SharedResourceRequest.
     * 
     * @param inSharedResourceRequest
     *            the shared request to transform
     * @return the unique key from the request, lowercased
     */
    @Override
    public String transform(final SharedResourceRequest inSharedResourceRequest)
    {
        if (inSharedResourceRequest == null || inSharedResourceRequest.getUniqueKey() == null)
        {
            return null;
        }
        return inSharedResourceRequest.getUniqueKey().toLowerCase();
    }

}
