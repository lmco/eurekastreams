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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

/**
 * Request object for the UpdateBanner action.
 *
 */
public class UpdateBannerRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 888775009398411565L;

    /**
     * Local instance of the string based banner id.
     */
    private String bannerId;

    /**
     * Local instance of the Long entity id.
     */
    private Long entityId;

    /**
     * Default constructor for ejb compliance.
     */
    @SuppressWarnings("unused")
    private UpdateBannerRequest()
    {
        //default constructor for ejb compliance.
    }

    /**
     * Constructor for the UpdateBannerRequest class.
     * @param inBannerId - instance of the Banner id to pass to the action.
     * @param inEntityId - instance of the Entity id to pass to the action.
     */
    public UpdateBannerRequest(final String inBannerId, final Long inEntityId)
    {
        bannerId = inBannerId;
        entityId = inEntityId;
    }

    /**
     * @return the bannerId
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * @param inBannerId the bannerId to set
     */
    public void setBannerId(final String inBannerId)
    {
        this.bannerId = inBannerId;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId()
    {
        return entityId;
    }

    /**
     * @param inEntityId the entityId to set
     */
    public void setEntityId(final Long inEntityId)
    {
        this.entityId = inEntityId;
    }
}
