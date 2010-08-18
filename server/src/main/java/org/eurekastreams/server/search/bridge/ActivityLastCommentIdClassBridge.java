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
package org.eurekastreams.server.search.bridge;

import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.stream.GetOrderedCommentIdsByActivityId;
import org.hibernate.search.bridge.StringBridge;

/**
 * Indexes the ID of the last commented item on an activity. This is used to sort by most recent comment.
 */
public class ActivityLastCommentIdClassBridge implements StringBridge
{
    /**
     * DAO for finding comment ids.
     */
    private static GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO;

    /**
     * Setter for the DAO for finding comments.
     * 
     * @param inCommentIdsByActivityIdDAO
     *            the DAO.
     */
    public static void setCommentIdsByActivityIdDAO(final GetOrderedCommentIdsByActivityId inCommentIdsByActivityIdDAO)
    {
        ActivityLastCommentIdClassBridge.commentIdsByActivityIdDAO = inCommentIdsByActivityIdDAO;
    }

    /**
     * Returns the last comment ID of an activity.
     * 
     * @param msgObject
     *            the activity to process for the index.
     * @return the last comment ID as a string.
     */
    public String objectToString(final Object msgObject)
    {
        Activity activity = (Activity) msgObject;

        Long lastCommentTime = 0L;

        List<Long> commentList = commentIdsByActivityIdDAO.execute(activity.getId());

        if (commentList != null && commentList.size() > 0)
        {
            lastCommentTime = commentList.get(commentList.size() - 1);
        }

        return Long.toString(lastCommentTime);
    }
}
