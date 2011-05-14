/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;

/**
 * Interface for different implementations to retrieve parent organization of activity recipient.
 */
// TODO: each of these methods probably hits the database once, so if we need
// all three, we're running three SQL queries when we need one. We should
// consider collapsing this down to one method that returns a simple object that
// encapsulates the different values we want, taking in boolean parameters of
// which properties to fetch.
public interface RecipientRetriever
{
    /**
     * Retrieve stream scope for recipient stream scope.
     * 
     * @param inActivityDTO
     *            - ActivityDTO instance.
     * @return recipient stream scope.
     */
    StreamScope getStreamScope(ActivityDTO inActivityDTO);

    /**
     * Get whether the destination stream of this activity is public.
     * 
     * @param inActivityDTO
     *            - ActivityDTO instance.
     * @return whether the destination stream of this activity is public.
     */
    Boolean isDestinationStreamPublic(ActivityDTO inActivityDTO);
}
