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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;


/**
 * Request object when calling the RefreshCachedCompositeStreamAction.
 *
 */
public class RefreshCachedCompositeStreamRequest implements Serializable
{
    /**
     * Serial id.
     */
    static final long serialVersionUID = 6660400976086239020L;

    /**
     * Local instance of the id of the list to update.
     */
    private Long listToUpdate;

    /**
     * Local instance of the Owner of the list being updated.
     */
    private Long listOwnerId;

    /**
     * Constructor for request object.
     * @param inListToUpdate - input param for the id of the list to be refreshed.
     * @param inListOwnerId - input param for the id of owner of the list to be refreshed.
     */
    public RefreshCachedCompositeStreamRequest(final Long inListToUpdate,
            final Long inListOwnerId)
    {
        listToUpdate = inListToUpdate;
        listOwnerId = inListOwnerId;
    }

    /**
     * Getter for the id of the list to be update.
     * @return - id of the list to update.
     */
    public Long getListToUpdate()
    {
        return listToUpdate;
    }

    /**
     * Getter for the id of the owner of the list to update.
     * @return - id of the owner of the list to update.
     */
    public Long getListOwnerId()
    {
        return listOwnerId;
    }
}
