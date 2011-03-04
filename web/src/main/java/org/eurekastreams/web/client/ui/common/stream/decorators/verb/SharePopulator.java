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
package org.eurekastreams.web.client.ui.common.stream.decorators.verb;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;

/**
 * Populates the dto for share verbs.
 * 
 */
public class SharePopulator implements ActivityDTOPopulatorStrategy
{
    /**
     * The original activity we're sharing.
     */
    private ActivityDTO originalActivity;

    /**
     * The local instance of the comment body for the shared activity.
     */
    private String commentBody;

    /**
     * Default constructor.
     * 
     * @param inOriginalActivity
     *            the activity we are sharing.
     * @param inCommentBody
     *            optional comment on Shared Activity.
     */
    public SharePopulator(final ActivityDTO inOriginalActivity, final String inCommentBody)
    {
        originalActivity = inOriginalActivity;
        commentBody = inCommentBody;
    }

    /**
     * The share verb is special. We're basically copying the original activity into the new shared on. If things like
     * location are to be preserved they too need to be copied in here; however, there is currently no system support
     * for those properties.
     * 
     * @param activity
     *            the dto.
     */
    public void populate(final ActivityDTO activity)
    {
        activity.setVerb(ActivityVerb.SHARE);

        if (originalActivity.getOriginalActor() != null
                && originalActivity.getOriginalActor().getUniqueIdentifier() != null)
        {
            activity.setOriginalActor(originalActivity.getOriginalActor());
        }
        else
        {
            activity.setOriginalActor(originalActivity.getActor());
        }

        activity.setBaseObjectProperties(originalActivity.getBaseObjectProperties());

        if (originalActivity.getBaseObjectProperties().containsKey("originalActivityId")
                && (originalActivity.getBaseObjectProperties().get("originalActivityId") != null))
        {
            activity.getBaseObjectProperties().put("originalActivityId",
                    originalActivity.getBaseObjectProperties().get("originalActivityId"));
        }
        else
        {
            activity.getBaseObjectProperties().put("originalActivityId", String.valueOf(originalActivity.getId()));
        }
        activity.setBaseObjectType(originalActivity.getBaseObjectType());
        if (commentBody != null && commentBody.length() > 0)
        {
            CommentDTO sharedActivityComment = new CommentDTO();
            sharedActivityComment.setBody(commentBody);
            List<CommentDTO> comments = new ArrayList<CommentDTO>();
            comments.add(sharedActivityComment);
            activity.setFirstComment(sharedActivityComment);
            activity.setComments(comments);
            activity.setCommentCount(comments.size());
        }
    }
}
