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
package org.eurekastreams.server.action.authorization;

/**
 * Interface for determining if person has coordinator access on entity.
 * 
 * @param <personIdType>
 *            type for person id.
 * @param <entityIdType>
 *            type for entity id.
 */
public interface CoordinatorAccessAuthorizer<personIdType, entityIdType>
{
    /**
     * Determine if person has coordinator access on entity anywhere up tree.
     * 
     * @param personId
     *            person id.
     * @param entityId
     *            entity id.
     * @return True if user has coordinator access, false otherwise.
     */
    Boolean hasCoordinatorAccessRecursively(final personIdType personId, final personIdType entityId);
}
