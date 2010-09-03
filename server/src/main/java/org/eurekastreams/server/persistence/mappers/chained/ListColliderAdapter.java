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

import java.util.List;

import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;

/**
 * Adapts a ListCollider to be used as a ResultsCombinerStrategy.
 */
public class ListColliderAdapter implements ResultsCombinerStrategy<Object, List<Long>>
{
    /**
     * The collider.
     */
    private ListCollider collider = null;

    /**
     * The max number of results.
     */
    private int maxResults = 0;

    /**
     * Constructor.
     * 
     * @param inCollider
     *            collider.
     * @param inMaxResults
     *            max results.
     */
    public ListColliderAdapter(final ListCollider inCollider, final int inMaxResults)
    {
        collider = inCollider;
        maxResults = inMaxResults;
    }

    /**
     * Combine two lists.
     * 
     * @param response1
     *            the first list.
     * @param response2
     *            the second list.
     * @param request
     *            original request.
     * @return the combined list.
     */
    public List<Long> combine(final PartialMapperResponse<Object, List<Long>> response1, final List<Long> response2,
            final Object request)
    {
        return collider.collide(response1.getResponse(), response2, maxResults);
    }
}
