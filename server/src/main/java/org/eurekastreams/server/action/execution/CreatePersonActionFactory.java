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
package org.eurekastreams.server.action.execution;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.service.actions.strategies.PersonCreator;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Factory to produce a ServiceAction that will create a Person record.
 */
public class CreatePersonActionFactory
{
    /**
     * TabMapper.
     */
    private TabMapper tabMapper;

    /**
     * OrganizationMapper.
     */
    private OrganizationMapper organizationMapper;

    /**
     * Constructor.
     *
     * @param inTabMapper
     *            TabMapper.
     * @param inOrganizationMapper
     *            OrganizationMapper.
     */
    public CreatePersonActionFactory(final TabMapper inTabMapper, final OrganizationMapper inOrganizationMapper)
    {
        tabMapper = inTabMapper;
        organizationMapper = inOrganizationMapper;
    }

    /**
     * Get a PersistResourceAction set up for Person.
     *
     * @param inPersonMapper
     *            the person mapper to be injected into the action
     * @param inUpdater
     *            Updater
     * @return the newly built action
     */
    public PersistResourceExecution<Person> getCreatePersonAction(final PersonMapper inPersonMapper,
            final UpdaterStrategy inUpdater)
    {
        return new PersistResourceExecution<Person>(inPersonMapper, this, inUpdater, new PersonCreator(inPersonMapper,
                tabMapper, organizationMapper));
    }
}
