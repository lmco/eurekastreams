/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamView;

/**
 * Interface for CompositeStreamLoaders.
 *
 */
public interface CompositeStreamLoader
{
    
    /**
     * Returns list of ActivityIds for a given CompositeStream and user.
     * @param inCompositeStream The CompositeStream
     * @param inUserId The user id
     * @return List of ActivityIds for a given CompositeStream and user.
     */
    List<Long> getActivityIds(StreamView inCompositeStream, long inUserId);
    
}
