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

import java.io.Serializable;

/**
 * Bannerable interface for Entities that want to support Banners.
 * 
 */
public interface Bannerable extends Serializable
{

    /**
     * @return bannerId.
     */
    String getBannerId();

    /**
     * @param inBannerId
     *            BannerId to set.
     */
    void setBannerId(final String inBannerId);

    /**
     * Set the EntityId associated with the current banner id. The entity id is needed to provide the context for the
     * settings page.
     * 
     * @param inBannerEntityId
     *            - long id of the entity that owns the currently set banner id.
     */
    void setBannerEntityId(final Long inBannerEntityId);

    /**
     * Retrieve the EntityId associated with the current banner id. The entity id is needed to provide the context for
     * the settings page.
     * 
     * @return long id of the entity that owns the currently set banner id.
     */
    Long getBannerEntityId();
}
