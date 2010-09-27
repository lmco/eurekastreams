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
package org.eurekastreams.server.domain;


/**
 * Bannerable DTO.
 *
 */
public class BannerableDTO implements Bannerable
{
    /**
     *
     */
    private static final long serialVersionUID = -9177335109930409908L;

    /**
     * BannerId of the object.
     */
    private String bannerId;

    /**
     *The Id of the Entity.
     */
    private Long entityId;

    /**
     * Private constructor for EJB compliance.
     */
    private BannerableDTO()
    {
        //intentionally left blank for EJB compliance.
    }

    /**
     * @param inBannerId
     *            The bannerId
     * @param inEntityID
     *            the entityID
     */
    public BannerableDTO(final String inBannerId, final Long inEntityID)
    {
        bannerId = inBannerId;
        entityId = inEntityID;
    }

    /**
     * {@inheritDoc}.
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Long getBannerEntityId()
    {
        return entityId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setBannerEntityId(final Long inBannerEntityId)
    {
        entityId = inBannerEntityId;

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }
}
