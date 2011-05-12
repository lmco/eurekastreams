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
package org.eurekastreams.server.action.authorization;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Authorization to determine if current user is a system administrator.
 */
public class CurrentUserIsSystemAdministratorAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get system administrators.
     */
    private DomainMapper<Serializable, List<Long>> systemAdministratorMapper;

    /**
     * Constructor.
     * 
     * @param inSystemAdministratorMapper
     *            mapper to get the system administrator ids
     */
    public CurrentUserIsSystemAdministratorAuthorization(
            final DomainMapper<Serializable, List<Long>> inSystemAdministratorMapper)
    {
        systemAdministratorMapper = inSystemAdministratorMapper;
    }

    /**
     * Determine if current user is a system administrator.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        Long personId = inActionContext.getPrincipal().getId();
        List<Long> administrators = systemAdministratorMapper.execute(null);
        if (!administrators.contains(personId))
        {
            throw new AuthorizationException("Insufficient permissions.");
        }
    }

}
