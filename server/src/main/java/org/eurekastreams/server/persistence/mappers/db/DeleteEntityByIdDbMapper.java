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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to delete an entity by id.
 * 
 */
public class DeleteEntityByIdDbMapper extends BaseArgDomainMapper<Long, Void>
{

    /**
     * Class of entity to delete.
     */
    private Class< ? > clazz;

    /**
     * Constructor.
     * 
     * @param inClazz
     *            Class of entity to delete.
     */
    public DeleteEntityByIdDbMapper(final Class< ? > inClazz)
    {
        clazz = inClazz;
    }

    /**
     * Delete an entity by id.
     * 
     * @param inRequest
     *            entity id.
     * @return Void.
     */
    @Override
    public Void execute(final Long inRequest)
    {
        getEntityManager().remove(getHibernateSession().load(clazz, inRequest));
        return null;
    }

}
