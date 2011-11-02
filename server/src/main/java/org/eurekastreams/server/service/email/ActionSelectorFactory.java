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
package org.eurekastreams.server.service.email;

import java.util.Map;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.stream.PostSplitActivityAndCommentsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Determines which action to execute based on the token data and email content.
 */
public class ActionSelectorFactory
{
    /**
     * Selects the actions and builds the parameters.
     *
     * @param tokenData
     *            Data from token.
     * @param content
     *            Content from message.
     * @param person
     *            User who sent message.
     * @return Action selection with parameters.
     */
    public UserActionRequest select(final Map<String, Long> tokenData, final String content,
            final PersonModelView person)
    {
        // Activity ID -> Comment to activity
        if (tokenData.containsKey(TokenContentFormatter.META_KEY_ACTIVITY))
        {
            CommentDTO comment = new CommentDTO();
            comment.setBody(content);
            comment.setActivityId(tokenData.get(TokenContentFormatter.META_KEY_ACTIVITY));

            return new UserActionRequest("postSplitActivityCommentsAction", null, comment);
        }
        // ID of person -> Post to personal stream
        else if (tokenData.containsKey(TokenContentFormatter.META_KEY_PERSON_STREAM))
        {
            return new UserActionRequest("postSplitActivityAndCommentsAction", null,
                    new PostSplitActivityAndCommentsRequest(EntityType.PERSON,
                            tokenData.get(TokenContentFormatter.META_KEY_PERSON_STREAM), content));
        }
        // ID of group -> Post to group stream
        else if (tokenData.containsKey(TokenContentFormatter.META_KEY_GROUP_STREAM))
        {
            return new UserActionRequest("postSplitActivityAndCommentsAction", null,
                    new PostSplitActivityAndCommentsRequest(EntityType.GROUP,
                            tokenData.get(TokenContentFormatter.META_KEY_GROUP_STREAM), content));
        }
        else
        {
            throw new ExecutionException("Cannot determine action to execute.");
        }
    }
}
