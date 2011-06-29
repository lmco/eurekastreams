/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.List;

/**
 * Request for GetAllFollowedByActivityIdsMapper.
 * 
 */
public class GetAllFollowedByActivityIdsRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 4156317888137476119L;

    /**
     * Id for user to get all followed activity ids for.
     */
    Long userId;

    /**
     * List of stream ids for groups user is following.
     */
    List<Long> groupStreamIds;

    /**
     * Constructor.
     * 
     * @param inUserId
     *            Id for user to get all followed activity ids for.
     * @param inGroupStreamIds
     *            List of stream ids for groups user is following.
     */
    public GetAllFollowedByActivityIdsRequest(final Long inUserId, final List<Long> inGroupStreamIds)
    {
        userId = inUserId;
        groupStreamIds = inGroupStreamIds;
    }

    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }

    /**
     * @param inUserId
     *            the userId to set
     */
    public void setUserId(final Long inUserId)
    {
        userId = inUserId;
    }

    /**
     * @return the groupStreamIds
     */
    public List<Long> getGroupStreamIds()
    {
        return groupStreamIds;
    }

    /**
     * @param inGroupStreamIds
     *            the groupStreamIds to set
     */
    public void setGroupStreamIds(final List<Long> inGroupStreamIds)
    {
        groupStreamIds = inGroupStreamIds;
    }

}
