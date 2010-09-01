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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.strategies.ActivityDeletePropertyStrategy;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates activity and comment DTOs with deletability.
 */
public class PopulateActivityDTODeletabilityData implements ActivityFilter
{
    /**
     * Strategy used to set deletable property comments included in the activityDTO.
     */
    private CommentDeletePropertyStrategy commentDeletePropertySetter;

    /**
     * Strategy used to set deletable property of an activityDTO.
     */
    private ActivityDeletePropertyStrategy activityDeletePropertySetter;

    /**
     * Constructor.
     * 
     * @param inCommentDeletePropertySetter
     *            comment delete property setter.
     * @param inActivityDeletePropertySetter
     *            activity delete property setter.
     */
    public PopulateActivityDTODeletabilityData(final CommentDeletePropertyStrategy inCommentDeletePropertySetter,
            final ActivityDeletePropertyStrategy inActivityDeletePropertySetter)
    {
        commentDeletePropertySetter = inCommentDeletePropertySetter;
        activityDeletePropertySetter = inActivityDeletePropertySetter;
    }

    /**
     * Populates activity and comment DTOs with deletability.
     * 
     * @param activities
     *            the DTOs.
     * @param user
     *            the user.
     */
    public void filter(final List<ActivityDTO> activities, final PersonModelView user)
    {
        for (ActivityDTO activity : activities)
        {
            activityDeletePropertySetter.execute(user.getAccountId(), activity);
            setCommentDeletable(user.getAccountId(), activity);
        }
    }

    /**
     * If activity has comments, determine if current user can delete them and set deletable property accordingly.
     * 
     * @param userName
     *            The current user's username.
     * @param activity
     *            The activity to examine for comments to set.
     */
    private void setCommentDeletable(final String userName, final ActivityDTO activity)
    {
        if (activity.getFirstComment() == null)
        {
            return;
        }

        List<CommentDTO> comments = new ArrayList<CommentDTO>(2);
        comments.add(activity.getFirstComment());
        if (activity.getLastComment() != null)
        {
            comments.add(activity.getLastComment());
        }

        commentDeletePropertySetter.execute(userName, activity, comments);
    }
}
