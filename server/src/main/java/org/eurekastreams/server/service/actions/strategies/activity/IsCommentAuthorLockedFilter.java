/*
 * Copyright (c) 2012-2012 Lockheed Martin Corporation
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.util.CollectionListAdapter;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Marks activities with whether the author is currently a locked user.
 */
public class IsCommentAuthorLockedFilter implements ActivityFilter
{
    /** Mapper to get PersonModelViews. */
    private final DomainMapper<Collection<Long>, List<PersonModelView>> getPeopleMapper;

    /**
     * Constructor.
     *
     * @param inGetPeopleMapper
     *            Mapper for getting authors.
     */
    public IsCommentAuthorLockedFilter(final DomainMapper<Collection<Long>, List<PersonModelView>> inGetPeopleMapper)
    {
        getPeopleMapper = inGetPeopleMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(final List<ActivityDTO> inActivities, final PersonModelView inCurrentUserAccount)
    {
        Collection<CommentDTO> comments = new ArrayList<CommentDTO>();
        for (ActivityDTO activity : inActivities)
        {
            if (activity.getComments() != null)
            {
                comments.addAll(activity.getComments());
            }
            else if (activity.getFirstComment() != null)
            {
                comments.add(activity.getFirstComment());
                if (activity.getLastComment() != null)
                {
                    comments.add(activity.getLastComment());
                }
            }
        }

        // short-circuit if no work to do.
        if (comments.isEmpty())
        {
            return;
        }

        Set<Long> ids = new HashSet<Long>();
        for (CommentDTO comment : comments)
        {
            ids.add(comment.getAuthorId());
        }

        List<PersonModelView> peopleList = getPeopleMapper.execute(new CollectionListAdapter<Long>(ids));
        HashMap<Long, Boolean> peopleIndex = new HashMap<Long, Boolean>();
        for (PersonModelView person : peopleList)
        {
            peopleIndex.put(person.getId(), person.isAccountLocked());
        }

        for (CommentDTO comment : comments)
        {
            comment.setAuthorActive(!peopleIndex.get(comment.getAuthorId()));
        }
    }
}
