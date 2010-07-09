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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.OrganizationMapper;

/**
 * Finds an entity.
 * 
 */
public class OrganizationFinder implements EntityFinder<Organization>
{
    /**
     * The mapper.
     */
    private OrganizationMapper mapper;

    /**
     * Default constructor.
     * 
     * @param inMapper
     *            the mapper.
     */
    public OrganizationFinder(final OrganizationMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Finds an entity.
     * 
     * @param user
     *            the currently logged in user.
     * @param id
     *            the id of the entity to find.
     * @return the entity.
     */
    public Organization findEntity(final Principal user, final String id)
    {
        return mapper.findByShortName(id);
    }

    /**
     * Finds an entity.
     * 
     * @param user
     *            the currently logged in user.
     * @param id
     *            the id of the entity to find.
     * @return the entity.
     */
    public Organization findEntity(final Principal user, final Long id)
    {
        return mapper.findById(id);
    }

}
