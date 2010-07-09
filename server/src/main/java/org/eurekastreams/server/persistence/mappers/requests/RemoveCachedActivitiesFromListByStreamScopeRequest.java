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
package org.eurekastreams.server.persistence.mappers.requests;

import org.eurekastreams.server.domain.stream.StreamScope;

/**
 * Request object used to call the RemoveCachedActivitiesFroMListByStreamScope
 * mapper.
 *
 */
public class RemoveCachedActivitiesFromListByStreamScopeRequest
{
    /**
     * Id of the list from which activities are being removed.
     */
    private final Long listId;
    
    /**
     * Id of the owner of the list from which activities are being removed.
     */
    private final Long listOwnerId;
    
    /**
     * StreamScope object that represents the set of activities to be removed from
     * the list.
     */
    private final StreamScope streamScope;
    
    /**
     * Constructor.
     * @param inListId - id of the list from which activities are being removed.
     * @param inListOwnerId - id of the owner of the list from which activities are being removed.
     * @param inStreamScope - StreamScope that represents the set of activities to be removed from
     * the list.
     */
    public RemoveCachedActivitiesFromListByStreamScopeRequest(
            final Long inListId,
            final Long inListOwnerId,
            final StreamScope inStreamScope)
    {
        listId = inListId;
        listOwnerId = inListOwnerId;
        streamScope = inStreamScope;
    }
    
    /**
     * Retrieve the list id from which activities are to be removed.
     * @return - id of the list.
     */
    public Long getListId()
    {
        return listId;
    }
    
    /**
     * Retrieve the id of the owner of the list from which activities are to be removed.
     * @return - id of the list owner.
     */
    public Long getListOwnerId()
    {
        return listOwnerId;
    }
    
    /**
     * Retrieve the StreamScope that represents the set of activities to be removed from the 
     * list.
     * @return - streamscope representing activities to be removed.
     */
    public StreamScope getStreamScope()
    {
        return streamScope;
    }
}
