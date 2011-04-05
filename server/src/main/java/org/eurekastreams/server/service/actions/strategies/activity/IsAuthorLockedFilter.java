/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Marks activities with whether the author is currently a locked user.
 */
public class IsAuthorLockedFilter implements ActivityFilter
{
    /** Mapper to get PersonModelViews. */
    private final DomainMapper<List<Long>, List<PersonModelView>> getPeopleMapper;

    /**
     * Constructor.
     *
     * @param inGetPeopleMapper
     *            Mapper for getting authors.
     */
    public IsAuthorLockedFilter(final DomainMapper<List<Long>, List<PersonModelView>> inGetPeopleMapper)
    {
        getPeopleMapper = inGetPeopleMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(final List<ActivityDTO> inActivities, final PersonModelView inCurrentUserAccount)
    {
        // short-circuit if no work to do.
        if (inActivities.isEmpty())
        {
            return;
        }

        Set<Long> ids = new HashSet<Long>();
        for (ActivityDTO activity : inActivities)
        {
            if (activity.getActor().getType() == EntityType.PERSON)
            {
                ids.add(activity.getActor().getId());
            }
        }

        List<PersonModelView> peopleList = getPeopleMapper.execute(new ArrayList<Long>(ids));
        HashMap<Long, Boolean> peopleIndex = new HashMap<Long, Boolean>();
        for (PersonModelView person : peopleList)
        {
            peopleIndex.put(person.getId(), person.isAccountLocked());
        }

        for (ActivityDTO activity : inActivities)
        {
            if (activity.getActor().getType() == EntityType.PERSON)
            {
                activity.setLockedAuthor(peopleIndex.get(activity.getActor().getId()));
            }
        }
    }
}
