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

/**
 * This class represents the properties necessary to add a User's Cached Activities
 * to a cached list.
 *
 */
public class AddCachedActivityToListRequest
{
    /**
     * Local instance of the id of the list that needs to be updated 
     * with new activities.
     */
    private Long listId;
    
    /**
     * Local instance of the id of the owner of the activities that
     * need to be added to the list.
     */
    private Long activityOwnerId;
    
    /**
     * Id of the list owner.
     */
    private Long listOwnerId;
    
    /**
     * Constructor for the AddCachedActivityToListRequest object.
     * @param inListId - id of the list to update with user activities.
     * @param inListOwnerId - id of the list owner.
     * @param inActivityOwnerId - id of the user to look up activities to add.
     */
    public AddCachedActivityToListRequest(
            final Long inListId, final Long inListOwnerId, final Long inActivityOwnerId)
    {
        listId = inListId;
        listOwnerId = inListOwnerId;
        activityOwnerId = inActivityOwnerId;
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
     * Getter for ActivityOwnerId.
     * @return - instance of activityOwnerId;
     */
    public Long getActivityOwnerId()
    {
        return activityOwnerId;
    }
    
}
