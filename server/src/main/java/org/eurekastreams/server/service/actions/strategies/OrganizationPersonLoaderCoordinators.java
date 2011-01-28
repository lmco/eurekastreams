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

import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetOrgCoordinators;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populator for loading Organization coordinators.
 * 
 */
public class OrganizationPersonLoaderCoordinators extends OrganizationPersonLoader
{

    /**
     * Organization coordinator person id DAO.
     */
    private GetOrgCoordinators orgCoordinatorDAO;

    /**
     * Constructor.
     * 
     * @param inOrgCoordinatorDAO
     *            Organization coordinator person id DAO.
     * @param inPersonDAO
     *            PersonDTO DAO.
     */
    public OrganizationPersonLoaderCoordinators(final GetOrgCoordinators inOrgCoordinatorDAO,
            final DomainMapper<List<Long>, List<PersonModelView>> inPersonDAO)
    {
        super(inPersonDAO);
        orgCoordinatorDAO = inOrgCoordinatorDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<Long> getPersonIds(final Organization inOrganization)
    {
        return orgCoordinatorDAO.execute(inOrganization.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setPeopleInOrganization(final Organization inOrganization, final Set<Person> inPeople)
    {
        inOrganization.setCoordinators(inPeople);
    }
}
