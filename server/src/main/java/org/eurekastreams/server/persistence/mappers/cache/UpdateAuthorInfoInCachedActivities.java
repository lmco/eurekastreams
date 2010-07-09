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

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Paged bulk updater of cached activities. Activities passed in with either be authored by or originally authored by
 * the input user.
 */
public class UpdateAuthorInfoInCachedActivities extends UpdateCachedItemsByIds<ActivityDTO, Person>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * List of author STreamEntityDTO updaters.
     */
    private List<UpdateStreamEntityDTOFromPerson> authorStreamEntityDTOUpdaters;

    /**
     * Batch size to use when pulling down activities from cache.
     *
     * @param inBatchSize
     *            batch size to use when pulling down activities from cache.
     * @param inAuthorStreamEntityDTOUpdaters
     *            the list of stream entity dto updaters
     */
    public UpdateAuthorInfoInCachedActivities(final Integer inBatchSize,
            final List<UpdateStreamEntityDTOFromPerson> inAuthorStreamEntityDTOUpdaters)
    {
        super(inBatchSize);
        authorStreamEntityDTOUpdaters = inAuthorStreamEntityDTOUpdaters;
    }

    /**
     * return the cache key for activity by id.
     *
     * @return the cache key for activity by id
     */
    @Override
    protected String getCacheKeyPrefix()
    {
        return CacheKeys.ACTIVITY_BY_ID;
    }

    /**
     * Update the input activity dto with the info from the input authored person. Activities passed in with either be
     * authored by or originally authored by the input user, so we have to check which applies.
     *
     * @param inActivityDTO
     *            the cached activity to update
     * @param inPerson
     *            the Person to update authored info of
     * @return whether any change was made
     */
    @Override
    protected Boolean updateCachedEntity(final ActivityDTO inActivityDTO, final Person inPerson)
    {
        boolean isUpdated = false;

        if (log.isTraceEnabled())
        {
            log.trace("Looking at activity with id:" + inActivityDTO.getId() + " for personId: " + inPerson.getId());
        }

        // update author's stream entity if this person is the author
        if (inActivityDTO.getActor() != null && inActivityDTO.getActor().getType() == EntityType.PERSON
                && inActivityDTO.getActor().getId() == inPerson.getId())
        {
            if (log.isTraceEnabled())
            {
                log.trace("Person with id:" + inPerson.getId() + " is actor of activity with id:"
                        + inActivityDTO.getId() + ".  Updating it.");
            }

            for (UpdateStreamEntityDTOFromPerson updater : authorStreamEntityDTOUpdaters)
            {
                isUpdated |= updater.execute(inActivityDTO.getActor(), inPerson);
            }
        }

        // update the original actor if it's this person
        if (inActivityDTO.getOriginalActor() != null && inActivityDTO.getOriginalActor().getType() == EntityType.PERSON
                && inActivityDTO.getOriginalActor().getId() == inPerson.getId())
        {
            if (log.isTraceEnabled())
            {
                log.trace("Person with id:" + inPerson.getId() + " is original actor of activity with id:"
                        + inActivityDTO.getId() + ".  Updating it.");
            }

            for (UpdateStreamEntityDTOFromPerson updater : authorStreamEntityDTOUpdaters)
            {
                isUpdated |= updater.execute(inActivityDTO.getOriginalActor(), inPerson);
            }
        }

        return isUpdated;
    }
}
