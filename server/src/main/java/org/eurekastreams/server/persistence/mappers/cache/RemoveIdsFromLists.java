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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * This cached mapper wraps a call to the cache method to remove a list of ids from a list of cache keys.
 */
public class RemoveIdsFromLists extends CachedDomainMapper
{
    /**
     * Execute the cache method to remove the ids from the necessary lists.
     * 
     * @param request
     *            the request object containing the keys and values.
     */
    public void execute(final DeleteIdsFromListsRequest request)
    {
        getCache().removeFromLists(request.getKeys(), request.getValues());
    }
}
