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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Abstract class for loading person collections into Organization DTO.
 * 
 */
public abstract class OrganizationPersonLoader extends OrganizationLoaderAbstract
{
    /**
     * PersonDTO DAO.
     */
    private GetPeopleByIds personDAO;

    /**
     * Constructor.
     * 
     * @param inPersonDAO
     *            PersonDTO DAO.
     */
    public OrganizationPersonLoader(final GetPeopleByIds inPersonDAO)
    {
        personDAO = inPersonDAO;
    }

    /**
     * Populate the person collection in the organization.
     * 
     * @param inOrganization
     *            Organization to populate.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void loadOrganization(final Organization inOrganization)
    {
        // get the person ids.
        Set<Long> personIds = getPersonIds(inOrganization);

        // create the result Set.
        Set<Person> result = new HashSet<Person>(personIds.size());

        // get PersonDTOs from cache if needed, otherwise don't bother calling
        // the mapper at all.
        if (!personIds.isEmpty())
        {
            List<PersonModelView> people = personDAO.execute(new ArrayList(personIds));
            for (PersonModelView pmv : people)
            {
                result.add(new Person(pmv));
            }
        }

        // set the collection in the entity.
        // NOTE: Client expects empty list to be set, not a null.
        setPeopleInOrganization(inOrganization, result);
    }

    /**
     * Returns the list of people ids for the populator.
     * 
     * @param inOrganization
     *            The Organization.
     * @return list of people ids for the populator.
     */
    protected abstract Set<Long> getPersonIds(final Organization inOrganization);

    /**
     * Sets the list of people in the organizationDTO appropriately.
     * 
     * @param inOrganization
     *            The organization.
     * @param inPeople
     *            List of {@link Person}s.
     */
    protected abstract void setPeopleInOrganization(final Organization inOrganization, final Set<Person> inPeople);

}
