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
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Strategy for returning DTO from params array containing activity id.
 *
 */
public class ActivityDTOFromParamsStrategyById implements ActivityDTOFromParamsStrategy<Long>
{
    /**
     * ActivtyDTO DAO for looking up activityDAO by Id.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activityDAO;

    /**
     * Constructor.
     *
     * @param inActivityDAO
     *            ActivtyDTO DAO for looking up activityDAO by Id.
     */
    public ActivityDTOFromParamsStrategyById(final DomainMapper<List<Long>, List<ActivityDTO>>  inActivityDAO)
    {
        activityDAO = inActivityDAO;
    }

    /**
     * Return ActivityDTO based on inParamsContent.
     *
     * @param inUser
     *            user details
     * @param inActivityId
     *            The activity id
     * @return ActivityDTO.
     */
    public ActivityDTO execute(final Principal inUser, final Long inActivityId)
    {
        // Get the ActivityDTO based on activityID passed in.
        List<Long> activityDTOIds = new ArrayList<Long>(1);
        activityDTOIds.add(inActivityId);
        
        List<ActivityDTO> activities = activityDAO.execute(activityDTOIds);
        
        return activities.size() > 0 ? activities.get(0) : null;
    }
}
