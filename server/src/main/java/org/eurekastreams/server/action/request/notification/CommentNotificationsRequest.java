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
package org.eurekastreams.server.action.request.notification;

/**
 * Request to generate notifications for an action on an activity comment.
 */
public class CommentNotificationsRequest extends ActivityNotificationsRequest
{
    /** Fingerprint. */
    private static final long serialVersionUID = 7153700033574364680L;

    /** ID of the comment. */
    private final long commentId;

    /**
     * Constructor.
     *
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inStreamEntityId
     *            ID of the entity (person or group) who owns the stream containing the activity.
     * @param inActivityId
     *            ID of the activity containing the comment.
     * @param inCommentId
     *            ID of the comment the event pertained to.
     */
    public CommentNotificationsRequest(final RequestType inType, final long inActorId, final long inStreamEntityId,
            final long inActivityId, final long inCommentId)
    {
        super(inType, inActorId, inStreamEntityId, inActivityId);
        commentId = inCommentId;
    }

    /**
     * @return the commentId
     */
    public long getCommentId()
    {
        return commentId;
    }
}
