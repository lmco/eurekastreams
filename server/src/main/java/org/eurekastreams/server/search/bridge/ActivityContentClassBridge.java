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
package org.eurekastreams.server.search.bridge;

import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.ActivityContentExtractor;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to extract the content out of an Activity.
 */
public class ActivityContentClassBridge implements StringBridge
{
    /**
     * Content extractor.
     */
    private ActivityContentExtractor contentExtractor = new ActivityContentExtractor();

    /**
     * DAO for finding comment ids.
     */
    private static DomainMapper<Long, List<Long>> commentIdsByActivityIdDAO;

    /**
     * DAO for finding comments.
     */
    private static DomainMapper<List<Long>, List<CommentDTO>> commentsByIdDAO;

    /**
     * Setter for the DAOs for finding comment information.
     * 
     * @param inCommentIdsByActivityIdDAO
     *            the DAO.
     * @param inCommentsByIdDAO
     *            the DAO.
     */
    public static void setCommentDAOs(final DomainMapper<Long, List<Long>> inCommentIdsByActivityIdDAO,
            final DomainMapper<List<Long>, List<CommentDTO>> inCommentsByIdDAO)
    {
        ActivityContentClassBridge.commentIdsByActivityIdDAO = inCommentIdsByActivityIdDAO;
        ActivityContentClassBridge.commentsByIdDAO = inCommentsByIdDAO;
    }

    /**
     * Extract the content out of an Activity/Message.
     * 
     * @param activityObject
     *            the message
     * @return a string containing the title and body of the input Message
     */
    @Override
    public String objectToString(final Object activityObject)
    {
        Activity activity = (Activity) activityObject;

        List<CommentDTO> commentList = commentsByIdDAO.execute(commentIdsByActivityIdDAO.execute(activity.getId()));

        final StringBuilder contentAsString = new StringBuilder();

        String content = contentExtractor.extractContent(activity.getBaseObjectType(), activity.getBaseObject());

        if (null != content)
        {
            contentAsString.append(content);
        }

        for (CommentDTO comment : commentList)
        {
            // Add a space between each comment.
            contentAsString.append(" ");
            contentAsString.append(comment.getBody());
        }

        // add the random constant
        contentAsString.append(" ");
        contentAsString.append(Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT);

        return contentAsString.toString();
    }
}
