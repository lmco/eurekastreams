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
package org.eurekastreams.server.persistence.mappers.db;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.eurekastreams.server.domain.Image;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the image by it's identifier. Null if it can't be found.
 *
 */
public class GetImageByIdentifier extends BaseArgDomainMapper<String, Image>
{
    /**
     * Get the image by an identifier.
     *
     * @param identifier
     *            the identifier.
     * @return the image.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Image execute(final String identifier)
    {
        try
        {
            Query q = getEntityManager().createQuery("FROM Image WHERE imageIdentifier = :identifier").setParameter(
                    "identifier", identifier);
            return (Image) q.getSingleResult();
        }
        catch (NoResultException ex)
        {
            return null;
        }
    }
}
