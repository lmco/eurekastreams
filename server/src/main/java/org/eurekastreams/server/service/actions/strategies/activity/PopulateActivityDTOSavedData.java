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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetStarredActivityIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates activity DTOs with personalized saved data.
 */
public class PopulateActivityDTOSavedData implements ActivityFilter
{
    /**
     * Mapper to get starred activities.
     */
    private GetStarredActivityIds starredActivitiesMapper;

    /**
     * Constructor.
     * @param inStarredActivitiesMapper saved mapper.
     */
    public PopulateActivityDTOSavedData(final GetStarredActivityIds inStarredActivitiesMapper)
    {
        starredActivitiesMapper = inStarredActivitiesMapper;
    }

    /**
     * Populates activity DTOs with personalized saved data.
     * 
     * @param activities
     *            the DTOs.
     * @param user
     *            the user.
     */
    public void filter(final List<ActivityDTO> activities, final PersonModelView user)
    {
        List<Long> starred = starredActivitiesMapper.execute(user.getEntityId());

        for (ActivityDTO activity : activities)
        {
            activity.setStarred(starred.contains(activity.getId()));
        }
    }

}
