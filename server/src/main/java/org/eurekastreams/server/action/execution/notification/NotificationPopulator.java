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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates a notification with any details not initially provided.
 */
public class NotificationPopulator
{
    /** For getting person info. */
    private DomainMapper<List<Long>, List<PersonModelView>> peopleMapper;

    /** For getting group info. */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupMapper;

    /** For getting org info. */
    private DomainMapper<List<Long>, List<OrganizationModelView>> orgMapper;

    /** For getting activity info. */
    private DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Constructor.
     * 
     * @param inPeopleMapper
     *            For getting person info.
     * @param inGroupMapper
     *            For getting group info.
     * @param inOrgMapper
     *            For getting org info.
     * @param inActivitiesMapper
     *            For getting activity info.
     */
    public NotificationPopulator(final DomainMapper<List<Long>, List<PersonModelView>> inPeopleMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGroupMapper,
            final DomainMapper<List<Long>, List<OrganizationModelView>> inOrgMapper,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper)
    {
        peopleMapper = inPeopleMapper;
        groupMapper = inGroupMapper;
        orgMapper = inOrgMapper;
        activitiesMapper = inActivitiesMapper;
    }

    /**
     * Populates a notification with any details not initially provided.
     * 
     * @param notif
     *            The notification.
     */
    public void populate(final NotificationDTO notif)
    {
        // populate actor
        if (notif.getActorId() > 0 && (isEmpty(notif.getActorAccountId()) || isEmpty(notif.getActorName())))
        {
            List<Long> peopleIds = new ArrayList<Long>();
            peopleIds.add(notif.getActorId());
            PersonModelView actor = peopleMapper.execute(peopleIds).get(0);
            notif.setActorAccountId(actor.getAccountId());
            notif.setActorName(actor.getDisplayName());
        }

        // populate activity
        if (notif.getActivityId() > 0 && notif.getActivityType() == null)
        {
            ActivityDTO activity = activitiesMapper.execute(Arrays.asList(notif.getActivityId())).get(0);
            notif.setActivityType(activity.getBaseObjectType());
        }

        // populate destination
        if (notif.getDestinationId() > 0
                && (isEmpty(notif.getDestinationUniqueId()) || isEmpty(notif.getDestinationName())))
        {
            if (EntityType.PERSON.equals(notif.getDestinationType()))
            {
                List<Long> peopleIds = new ArrayList<Long>();
                peopleIds.add(notif.getDestinationId());
                PersonModelView dest = peopleMapper.execute(peopleIds).get(0);
                notif.setDestinationUniqueId(dest.getAccountId());
                notif.setDestinationName(dest.getDisplayName());
            }
            else if (EntityType.GROUP.equals(notif.getDestinationType()))
            {
                DomainGroupModelView dest = groupMapper.execute(Collections.singletonList(notif.getDestinationId()))
                        .get(0);
                notif.setDestinationUniqueId(dest.getShortName());
                notif.setDestinationName(dest.getName());
            }
            else if (EntityType.ORGANIZATION.equals(notif.getDestinationType()))
            {
                OrganizationModelView dest = orgMapper.execute(Collections.singletonList(notif.getDestinationId()))
                        .get(0);
                notif.setDestinationUniqueId(dest.getShortName());
                notif.setDestinationName(dest.getName());
            }
        }
    }

    /**
     * Convenience routine to test if a string is not present.
     * 
     * @param theString
     *            String to test.
     * @return True if null/empty.
     */
    private boolean isEmpty(final String theString)
    {
        return theString == null || theString.isEmpty();
    }
}
