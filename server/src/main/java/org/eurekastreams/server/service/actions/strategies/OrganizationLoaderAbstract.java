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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.domain.Organization;

/**
 * Abstract class for OrganizationLoader to implement that takes care of decoration logic.
 */
public abstract class OrganizationLoaderAbstract implements OrganizationLoader
{
    /**
     * OrganizationLoader to decorate.
     */
    private OrganizationLoader decorated = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Organization load(final Organization inOrganization)
    {
        loadOrganization(inOrganization);
        if (decorated != null)
        {
            decorated.load(inOrganization);
        }

        return inOrganization;
    }

    /**
     * Sets the OrganizationLoader to be decorated.
     * 
     * @param inOrganizationLoader
     *            the OrganizationLoader to be decorated.
     */
    public void setOrganizationLoader(final OrganizationLoader inOrganizationLoader)
    {
        decorated = inOrganizationLoader;
    }

    /**
     * Populated the desired information in the Organization.
     * 
     * @param inOrganization
     *            Ortanization to populate.
     */
    public abstract void loadOrganization(Organization inOrganization);

}
