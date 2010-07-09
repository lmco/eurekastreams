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
package org.eurekastreams.server.service.actions.strategies.activity;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Implementation for the ActorRetrievalStrategy specific for a Person.
 *
 */
public class PersonActorRetrievalStrategy implements ActorRetrievalStrategy
{
    /**
     * Default constructor left blank to be used in spring.
     */
    public PersonActorRetrievalStrategy()
    {
        // default constructor left blank to be used in spring.
    }

    /**
     * {@inheritDoc}
     */
    public String getActorAccountId(final Principal inUser, final ActivityDTO inActivity) throws Exception
    {
        return inUser.getAccountId();
    }

    /**
     * {@inheritDoc}
     */
    public EntityType getEntityType()
    {
        return EntityType.PERSON;
    }

    /**
     * {@inheritDoc}
     */
    public Long getActorId(final Principal inUser, final ActivityDTO inActivity) throws Exception
    {
        return inUser.getId();
    }
}
