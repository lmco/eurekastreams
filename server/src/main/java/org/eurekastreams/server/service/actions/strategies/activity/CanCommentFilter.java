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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * A filter to set the can comment bit to an activityDTO.
 *
 */
public class CanCommentFilter implements ActivityFilter
{

    /**
     * Mapper to get PersonModelViews by account ids.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;

    /**
     * DomainGroup model view mapper.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Mapper to get all Coordinators of a Group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinators;

    /**
     * Constructor.
     *
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            Mapper to get PersonModelViews by account ids
     * @param inGroupMapper
     *            A group Mapper to get if a group has restricted access.
     * @param inGroupCoordinators
     *            The groupCoordinatormapper used to get coordinators.
     */
    public CanCommentFilter(
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetDomainGroupsByShortNames inGroupMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordinators)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        groupMapper = inGroupMapper;
        groupCoordinators = inGroupCoordinators;
    }

    /**
     * apply the filter.
     *
     * @param activities
     *            The list of activities to filter.
     * @param inCurrentUserAccountId
     *            The currently logged in user's account id.
     */
    public void filter(final List<ActivityDTO> activities, final PersonModelView inCurrentUserAccountId)
    {
        // short-circuit if no work to do.
        if (activities.size() == 0)
        {
            return;
        }

        // collect destination stream ids in hashtables (by type) to eliminate duplication
        Hashtable<String, Boolean> personCommentable = new Hashtable<String, Boolean>();
        Hashtable<String, Boolean> groupCommentable = new Hashtable<String, Boolean>();

        for (ActivityDTO activity : activities)
        {
            switch (activity.getDestinationStream().getType())
            {
            case PERSON:
                personCommentable.put(activity.getDestinationStream().getUniqueIdentifier().trim().toLowerCase(),
                        Boolean.TRUE);
                break;
            case GROUP:
                groupCommentable.put(activity.getDestinationStream().getUniqueIdentifier().trim().toLowerCase(),
                        Boolean.TRUE);
                break;
            default:
                throw new IllegalArgumentException(
                        "Attempted to filter Activity with unsupported destination stream type.");
            }
        }

        // do work of determining if destination streams are commentable.
        setPersonCommentableValues(inCurrentUserAccountId.getAccountId(), personCommentable);
        setGroupCommentableValues(inCurrentUserAccountId.getAccountId(), groupCommentable);

        // loop through activities and set the appropriate values.
        for (ActivityDTO activity : activities)
        {
            switch (activity.getDestinationStream().getType())
            {
            case PERSON:
                activity.setCommentable(personCommentable.get(activity.getDestinationStream().getUniqueIdentifier()
                        .trim().toLowerCase()));
                break;
            case GROUP:
                activity.setCommentable(groupCommentable.get(activity.getDestinationStream().getUniqueIdentifier()
                        .trim().toLowerCase()));
                break;
            default:
                throw new IllegalArgumentException(
                        "Attempted to filter Activity with unsupported destination stream type.");
            }
        }
    }

    /**
     * Determine if current user is allowed to comment on activites with person destinations.
     *
     * @param inCurrentUserAccountId
     *            Current user's account Id.
     * @param inPersonCommentable
     *            Hashtable of person account Ids to set value for.
     */
    private void setPersonCommentableValues(final String inCurrentUserAccountId,
            final Hashtable<String, Boolean> inPersonCommentable)
    {
        // short-circuit here if no work to be done.
        if (inPersonCommentable.size() == 0)
        {
            return;
        }

        // grab all people associated with activity destination streams in one shot.
        List<PersonModelView> destinations = getPersonModelViewsByAccountIdsMapper.execute(new ArrayList(
                inPersonCommentable.keySet()));

        // loop through them and set commentable appropriately
        for (PersonModelView destination : destinations)
        {
            inPersonCommentable.put(destination.getAccountId().trim().toLowerCase(),
                    (destination.isCommentable() || (inCurrentUserAccountId != null && destination.getAccountId()
                            .trim().equalsIgnoreCase(inCurrentUserAccountId))));
        }
    }

    /**
     * Determine if current user is allowed to comment on activites with group destinations.
     *
     * @param inCurrentUserAccountId
     *            Current user's account Id.
     * @param inGroupCommentable
     *            Hashtable of group shortnames to set value for.
     */
    private void setGroupCommentableValues(final String inCurrentUserAccountId,
            final Hashtable<String, Boolean> inGroupCommentable)
    {
        // short-circuit here if no work to be done.
        if (inGroupCommentable.size() == 0)
        {
            return;
        }

        // grab all groups associated with activity destination streams in one shot.
        List<DomainGroupModelView> destinations = groupMapper.execute(new ArrayList(inGroupCommentable.keySet()));

        // Create cache for current user's group coordinator status to attempt to minimize calls to
        // hasGroupCoordinatorAccessRecursively
        Hashtable<String, Boolean> currentUserCoordinatorCache = new Hashtable<String, Boolean>();

        // loop through groups and set commentable appropriately
        for (DomainGroupModelView destination : destinations)
        {
            boolean canComment = destination.isCommentable();

            // if group not commentable, see if current user is coordinator and can override setting.
            if (!canComment)
            {
                // check cache to see if we've determined this before.
                Boolean isCoordinator = currentUserCoordinatorCache
                        .get(destination.getShortName().trim().toLowerCase());

                if (isCoordinator == null && inCurrentUserAccountId != null)
                {
                    // not cached, have to figure it out
                    isCoordinator = groupCoordinators.hasGroupCoordinatorAccessRecursively(inCurrentUserAccountId,
                            destination.getEntityId());

                    // cache it
                    currentUserCoordinatorCache.put(destination.getShortName().trim().toLowerCase(), isCoordinator);
                }

                canComment = isCoordinator == null ? Boolean.FALSE : isCoordinator;
            }

            inGroupCommentable.put(destination.getShortName().trim().toLowerCase(), canComment);
        }
    }

}
