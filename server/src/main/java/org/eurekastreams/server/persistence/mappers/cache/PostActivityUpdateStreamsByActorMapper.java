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

import java.util.Collections;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This mapper adds an activity to the entity streams that are related to the actor posting the activity.
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
     * Constructor for the {@link PostActivityUpdateStreamsByActorMapper}.
     * 
     * @param inBulkPeopleByAccountIdMapper
     *            - instance of the {@link GetPeopleByAccountIds} mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            - instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    public PostActivityUpdateStreamsByActorMapper(final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper)
    {
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
    }

    /**
     * Post the provided {@link ActivityDTO} into the entity stream related to the actor.
     * 
     * @param activity
     *            - {@link ActivityDTO} to be posted into the streams.
     */
    public void execute(final ActivityDTO activity)
    {
        long activityId = activity.getId();

        // Add to the appropriate entity stream.
        EntityType streamType = activity.getDestinationStream().getType();

        switch (streamType)
        {
        case GROUP:
            DomainGroupModelView group = bulkDomainGroupsByShortNameMapper.execute(
                    Collections.singletonList(activity.getDestinationStream().getUniqueIdentifier())).get(0);

            getCache().addToTopOfList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + group.getStreamId(), activityId);
            break;
        case PERSON:
            PersonModelView person = bulkPeopleByAccountIdMapper.execute(
                    Collections.singletonList(activity.getDestinationStream().getUniqueIdentifier())).get(0);

            getCache().addToTopOfList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + person.getStreamId(), activityId);
            break;
        default:
            break;
        }
    }
}
