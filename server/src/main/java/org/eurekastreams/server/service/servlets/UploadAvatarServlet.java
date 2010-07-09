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
package org.eurekastreams.server.service.servlets;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.AvatarEntity;

/**
 * This class uploads an avatar.
 *
 * @param <T>
 *            the entity type.
 */
@SuppressWarnings("serial")
public abstract class UploadAvatarServlet<T extends DomainEntity> extends
        UploadImageServlet<T>
{



    /**
     * Gets the file path string.
     *
     * @param inImageId
     *            the domain entity id
     * @return the file path string
     */
    @Override
    protected String getFilePath(final String inImageId)
    {
        return "o" + inImageId;
    }

    /**
     * Gets the response string.
     *
     * @param entity
     *            the person
     * @return the response string
     */
    @Override
    protected String getResponseString(final T entity)
    {
        return ((AvatarEntity) entity).getAvatarCropX() + ","
                + ((AvatarEntity) entity).getAvatarCropY() + ","
                + ((AvatarEntity) entity).getAvatarCropSize();
    }

}
