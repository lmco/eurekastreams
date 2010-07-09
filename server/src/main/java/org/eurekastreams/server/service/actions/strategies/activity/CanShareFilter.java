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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Strategy for setting Shareable property on a List of Activities.
 * 
 */
public class CanShareFilter implements ActivityFilter
{
    /**
     * How we lookup a group object.
     */
    GetDomainGroupsByShortNames groupCacheMapper;

    /**
     * Constructor.
     * 
     * @param inGroupCacheMapper
     *            A group Mapper to check if the group is private.
     */
    CanShareFilter(final GetDomainGroupsByShortNames inGroupCacheMapper)
    {
        groupCacheMapper = inGroupCacheMapper;
    }

    /**
     * apply the filter.
     * 
     * @param activities
     *            The list of activities to filter.
     * @param inCurrentUserAccountId
     *            Not used for share filter.
     * @return List of modified activities.
     */
    public List<ActivityDTO> filter(final List<ActivityDTO> activities, final String inCurrentUserAccountId)
    {
        // short-circuit if no work to do.
        if (activities.size() == 0)
        {
            return activities;
        }

        // collect group destination stream ids in hashtables to eliminate duplication
        Hashtable<String, Boolean> groupShareable = new Hashtable<String, Boolean>();

        for (ActivityDTO activity : activities)
        {
            if (activity.getDestinationStream().getType() == EntityType.GROUP)
            {
                groupShareable.put(activity.getDestinationStream().getUniqueIdentifier().trim(),
                        Boolean.FALSE);
            }
        }

        // if group destinations, set the shareable value for group appropriately.
        if (!groupShareable.isEmpty())
        {
            setGroupShareableValues(groupShareable);
        }

        // loop through activities and set shareable flag appropriately.
        for (ActivityDTO activity : activities)
        {
            switch (activity.getDestinationStream().getType())
            {
            case PERSON:
                activity.setShareable(true);
                break;
            case GROUP:
                activity.setShareable(groupShareable.get(activity.getDestinationStream().getUniqueIdentifier().trim()));
                break;
            default:
                throw new IllegalArgumentException(
                        "Attempted to filter Activity with unsupported destination stream type.");
            }
        }
        return activities;
    }

    /**
     * Sets the shareable value for groups.
     * 
     * @param inGroupShareable
     *            Map of groups and share values.
     */
    private void setGroupShareableValues(final Hashtable<String, Boolean> inGroupShareable)
    {
        // grab all domainGroupDTOs for destination groups.
        List<DomainGroupModelView> destinations = groupCacheMapper.execute(new ArrayList(inGroupShareable.keySet()));

        for (DomainGroupModelView destination : destinations)
        {
            inGroupShareable.put(destination.getShortName().trim(), destination.isPublic());
        }
    }
}
