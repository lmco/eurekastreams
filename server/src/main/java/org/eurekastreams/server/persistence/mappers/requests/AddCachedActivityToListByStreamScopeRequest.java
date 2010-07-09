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
package org.eurekastreams.server.persistence.mappers.requests;

import org.eurekastreams.server.domain.stream.StreamScope;

/**
 * Class to represent a request to AddCachedActivityToListByStreamScope.
 *
 */
public class AddCachedActivityToListByStreamScopeRequest
{
    /**
     * Local instance of the id of the list that needs to be updated
     * with new activities.
     */
    private Long listId;

    /**
     * Local instance of the stream scope that represents the activities
     * to be added to the list.
     */
    private StreamScope streamScope;

    /**
     * Id of the list owner.
     */
    private Long listOwnerId;

    /**
     * Constructor for the AddCachedActivityToListByStreamScopeRequest object.
     * @param inListId - id of the list to update with user activities.
     * @param inListOwnerId - id of the list owner.
     * @param inStreamScope - StreamScope that represents the activities to be added to the List.
     */
    public AddCachedActivityToListByStreamScopeRequest(
            final Long inListId, final Long inListOwnerId, final StreamScope inStreamScope)
    {
        listId = inListId;
        listOwnerId = inListOwnerId;
        streamScope = inStreamScope;
    }

    /**
     * Getter for ListId.
     * @return - instance of listId.
     */
    public Long getListId()
    {
        return listId;
    }

    /**
     * Getter for the list owner id.
     * @return - instance of the list owner id.
     */
    public Long getListOwnerId()
    {
        return listOwnerId;
    }

    /**
     * Getter for StreamScope.
     * @return - instance of the StreamScope that represents the activities to be added to the List.
     */
    public StreamScope getStreamScope()
    {
        return streamScope;
    }
}
