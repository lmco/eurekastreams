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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates activity DTOs with personalized Like data.
 */
public class PopulateActivityDTOLikeData implements ActivityFilter
{
    /**
     * Get activities that a person has liked.
     */
    private DomainMapper<List<Long>, List<List<Long>>> getLikedActivityIdsByUserIdsMapper;

    /**
     * Get who liked an activity.
     */
    private DomainMapper<List<Long>, List<List<Long>>> getPeopleWhoLikedActivityMapper;

    /**
     * Mapper to get person info.
     */
    private GetPeopleByIds peopleMapper;

    /**
     * Max number of likers to return.
     */
    private int likerLimit;

    /**
     * Constructor.
     * 
     * @param inGetLikedActivityIdsByUserIdsMapper
     *            Get liked by activity IDs mapper.
     * @param inGetPeopleWhoLikedActivityMapper
     *            Get who liked activities.
     * @param inPeopleMapper
     *            people mapper.
     * @param inLikerLimit
     *            max likers to return.
     */
    public PopulateActivityDTOLikeData(
            final DomainMapper<List<Long>, List<List<Long>>> inGetLikedActivityIdsByUserIdsMapper,
            final DomainMapper<List<Long>, List<List<Long>>> inGetPeopleWhoLikedActivityMapper,
            final GetPeopleByIds inPeopleMapper, final int inLikerLimit)
    {
        getLikedActivityIdsByUserIdsMapper = inGetLikedActivityIdsByUserIdsMapper;
        getPeopleWhoLikedActivityMapper = inGetPeopleWhoLikedActivityMapper;
        peopleMapper = inPeopleMapper;
        likerLimit = inLikerLimit;
    }

    /**
     * Populates activity DTOs with personalized Like data.
     * 
     * @param activitiesCollection
     *            the DTOs.
     * @param user
     *            the user.
     */
    public void filter(final List<ActivityDTO> activitiesCollection, final PersonModelView user)
    {
        List<ActivityDTO> activities = new LinkedList<ActivityDTO>();
        List<Long> activityIds = new LinkedList<Long>();

        // Need a List to preserve order.
        for (ActivityDTO activity : activitiesCollection)
        {
            activityIds.add(activity.getId());
            activities.add(activity);
        }

        List<List<Long>> likedCollection = getLikedActivityIdsByUserIdsMapper
                .execute(Arrays.asList(user.getEntityId()));

        List<Long> liked = null;

        if (likedCollection != null && likedCollection.size() > 0)
        {
            liked = likedCollection.iterator().next();
        }
        else
        {
            return;
        }

        List<List<Long>> likersCollection = getPeopleWhoLikedActivityMapper.execute(activityIds);
        List<Long> allLikerIds = new LinkedList<Long>();

        // Build list of all needed likers
        for (List<Long> likerList : likersCollection)
        {
            if (likerList.size() > likerLimit - 1)
            {
                allLikerIds.addAll(likerList.subList(0, likerLimit));
            }
            else
            {
                allLikerIds.addAll(likerList);
            }
        }

        List<PersonModelView> allLikersList = peopleMapper.execute(allLikerIds);

        Map<Long, PersonModelView> allLikersMap = new HashMap<Long, PersonModelView>();

        for (PersonModelView person : allLikersList)
        {
            allLikersMap.put(person.getId(), person);
        }

        for (int i = 0; i < activities.size(); i++)
        {
            ActivityDTO activity = activities.get(i);

            List<Long> likers = likersCollection.get(i);

            activity.setLikeCount(likers.size());

            List<PersonModelView> likersModels = new LinkedList<PersonModelView>();

            for (int j = 0; j < likers.size() && j < likerLimit - 1; j++)
            {
                likersModels.add(allLikersMap.get(likers.get(j)));
            }

            activity.setLikers(likersModels);
            activity.setLiked(liked.contains(activity.getId()));
        }
    }
}
