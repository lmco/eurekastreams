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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * This mapper adds an activity to the composite streams that are related to the actor posting the activity.
 *
 */
public class PostActivityUpdateStreamsByActorMapper extends CachedDomainMapper
{

    /**
     * Local instance of the {@link GetPeopleByAccountIds} mapper.
     */
    private final GetPeopleByAccountIds bulkPeopleByAccountIdMapper;

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Local instance of the {@link GetOrganizationsByShortNames} mapper.
     */
    private final GetOrganizationsByShortNames organizationsByShortNameDAO;

    /**
     * Constructor for the {@link PostActivityUpdateStreamsByActorMapper}.
     *
     * @param inBulkPeopleByAccountIdMapper
     *            - instance of the {@link GetPeopleByAccountIds} mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            - instance of the {@link GetDomainGroupsByShortNames} mapper.
     * @param inOrganizationsByShortNameDAO
     *            - instance of the {@link GetOrganizationsByShortNames} mapper.
     */
    public PostActivityUpdateStreamsByActorMapper(final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper,
            final GetOrganizationsByShortNames inOrganizationsByShortNameDAO)
    {
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
        organizationsByShortNameDAO = inOrganizationsByShortNameDAO;
    }

    /**
     * Post the provided {@link ActivityDTO} into the cached composite streams related to the actor.
     *
     * @param activity
     *            - {@link ActivityDTO} to be posted into the streams.
     */
    public void execute(final ActivityDTO activity)
    {
        // TODO: add to the person or group's stream

        // TODO: add to all org's streams
    }
}
