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
 * Request object for calling the RemoveCachedActivitiesFromList mapper.
 *
 */
public class RemoveCachedActivitiesFromListRequest
{
    /**
     * List id of the list that is being updated.
     */
    private final Long listId;
    
    /**
     * Id of the owner of the list that is being updated.
     */
    private final Long listOwnerId;
    
    /**
     * Id of the owner of the activities being removed from the list.
     */
    private final Long activitiesOwnerId;
    
    /**
     * Constructor.
     * @param inListId - id of the cached list that activities are being removed from.
     * @param inListOwnerId - id of the owner of the cached list that activities are 
     *          being removed from.
     * @param inActivitiesOwnerId - id of the owner of the activities that are being
     *          removed from the cached list.
     */
    public RemoveCachedActivitiesFromListRequest(
            final Long inListId,
            final Long inListOwnerId,
            final Long inActivitiesOwnerId)
    {
        listId = inListId;
        listOwnerId = inListOwnerId;
        activitiesOwnerId = inActivitiesOwnerId;
    }
    
    /**
     * Retrieve the id of the list that activities are being removed from.
     * @return id of the list.
     */
    public Long getListId()
    {
        return listId;
    }
    
    /**
     * Retrieve the id of the owner of the list that activities are being removed from.
     * @return id of the owner.
     */
    public Long getListOwnerId()
    {
        return listOwnerId;
    }
    
    /**
     * Retrieve the id of the owner of the activities that are being removed from the list.
     * @return id of the owner of the activities being removed.
     */
    public Long getActivitiesOwnerId()
    {
        return activitiesOwnerId;
    }
}
