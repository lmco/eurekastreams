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
package org.eurekastreams.server.action.execution.notification;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.UpdateNotificationsOnNameChangeRequest;

/**
 * Implementation of {@link UpdateNotificationsOnNameChangeRequestGenerator} for person name changes.
 * 
 */
public class UpdateNotificationsOnPersonNameChangeRequestGenerator implements
        UpdateNotificationsOnNameChangeRequestGenerator
{
    /**
     * {@link FindByIdMapper}.
     */
    FindByIdMapper<Person> entityFinder;

    /**
     * Constructor.
     * 
     * @param inEntityFinder
     *            {@link FindByIdMapper}.
     */
    public UpdateNotificationsOnPersonNameChangeRequestGenerator(final FindByIdMapper<Person> inEntityFinder)
    {
        entityFinder = inEntityFinder;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public UpdateNotificationsOnNameChangeRequest getUpdateNotificationsOnNameChangeRequest(final Long inId)
    {
        Person p = entityFinder.execute(new FindByIdRequest("Person", inId));
        return new UpdateNotificationsOnNameChangeRequest(EntityType.PERSON, p.getAccountId(), p.getDisplayName());
    }

}
