/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Mapper to get all private group ids.
 */
public class GetAllPrivateGroupIdsDbMapper extends ReadMapper<Serializable, List<Long>>
{
    /**
     * Return all private group ids.
     * 
     * @param ignored
     *            ignored
     * @return a list of all private group ids
     */
    @Override
    public List<Long> execute(final Serializable ignored)
    {
        return getEntityManager().createQuery("SELECT id FROM DomainGroup g WHERE g.publicGroup = false")
                .getResultList();
    }

}
