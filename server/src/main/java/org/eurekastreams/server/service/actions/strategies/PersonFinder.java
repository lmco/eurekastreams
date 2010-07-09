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
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Finds an entity.
 * 
 */
public class PersonFinder implements EntityFinder<Person>
{
    /**
     * The mapper.
     */
    private PersonMapper personMapper;

    /**
     * Default constructor.
     * 
     * @param inMapper
     *            the mapper.
     */
    public PersonFinder(final PersonMapper inMapper)
    {
        personMapper = inMapper;
    }

    /**
     * Finds an entity.
     * 
     * @param user
     *            Principal of the person to load
     * @param id
     *            not used
     * @return the entity.
     */
    public Person findEntity(final Principal user, final String id)
    {
        return personMapper.findByAccountId(user.getAccountId());
    }

    /**
     * Finds an entity.
     * 
     * @param user
     *            Principal of the person to load
     * @param id
     *            not used
     * @return the entity.
     */
    public Person findEntity(final Principal user, final Long id)
    {
        return personMapper.findByAccountId(user.getAccountId());
    }

}
