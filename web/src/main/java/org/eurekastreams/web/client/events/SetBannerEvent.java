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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.Bannerable;

/**
 * Event to set the banner.
 *
 */
public class SetBannerEvent
{
    /**
     * Banner id.
     */
    private Bannerable bannerableEntity = null;

    /**
     * Default constructor.
     *
     * @param inBannerableEntity
     *            bannerableEntity.
     */
    public SetBannerEvent(final Bannerable inBannerableEntity)
    {
        bannerableEntity = inBannerableEntity;
    }

    /**
     * Constructor signaling to just show the banner.
     */
    public SetBannerEvent()
    {
    }

    /**
     * Get the bannerable entity.
     *
     * @return the bannerable entity.
     */
    public Bannerable getBannerableEntity()
    {
        return bannerableEntity;
    }
}
