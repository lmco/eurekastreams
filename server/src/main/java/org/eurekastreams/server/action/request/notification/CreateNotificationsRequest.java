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
package org.eurekastreams.server.action.request.notification;

import java.io.Serializable;

/**
 * Request data for the action to generate notifications for an event.
 */
public class CreateNotificationsRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -5690584323772486383L;

    /**
     * Types of events for which a notification may be requested.
     */
    public enum RequestType
    {
        /**
         * Someone posted a comment. Actor=person id of who commented, Destination=id of person whose stream was posted
         * to, Activity=id of comment.
         */
        COMMENT,

        /**
         * Someone posted to a stream. Actor=person id of who posted, Destination=id of person whose stream was posted
         * to, Activity=id of the new post.
         */
        STREAM_POST,

        /**
         * Someone began following. Actor=person id who began following, Destination=id of person being followed,
         * Activity=(not used).
         */
        FOLLOWER,

        /**
         * Someone posted a comment to an activity on a group stream. Actor=person id of who commented, Destination=id
         * of group whose stream was posted to, Activity=id of comment.
         */
        GROUP_COMMENT,

        /**
         * Someone posted to a group stream. Actor=person id of who posted, Destination=id of group whose stream was
         * posted to, Activity=id of the new post.
         */
        GROUP_STREAM_POST,

        /**
         * Someone began following a group. Actor=person id who began following, Destination=id of group being followed,
         * Activity=(not used).
         */
        GROUP_FOLLOWER,

        /**
         * Someone flagged an activity (as inappropriate). Actor=person id who flagged, Destination=id of person or
         * group whose stream contained the post, Activity=id of the post.
         */
        FLAG_ACTIVITY,

        /**
         * Pending group creation request. Actor=person who requested group, Destination=org id of requested new group's
         * parent org, Activity=id of the new group (looser use of "activity")
         */
        REQUEST_NEW_GROUP,

        /**
         * Someone requested access to a private group. Actor=person id who requested access, Destination=id of group,
         * Activity=(not used).
         */
        REQUEST_GROUP_ACCESS
    }

    /** Type of event that occurred. */
    private RequestType type;

    /** ID of the entity (person) who performed the action which the notification is about. */
    private long actorId;

    /** ID of the entity (person or group) upon whom or upon whose stream that the action acted. */
    private long destinationId;

    /** ID of the "activity" the event pertained to. See comment on getter for more detail. */
    private long activityId;

    /**
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inDestinationId
     *            ID of the entity (person or stream) that the action acted upon.
     * @param inActivityId
     *            ID of the activity the event pertained to.
     */
    public CreateNotificationsRequest(final RequestType inType, final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        type = inType;
        actorId = inActorId;
        destinationId = inDestinationId;
        activityId = inActivityId;
    }

    /**
     * @return Type of event that occurred.
     */
    public RequestType getType()
    {
        return type;
    }

    /**
     * @param inType
     *            Type of event that occurred.
     */
    public void setType(final RequestType inType)
    {
        type = inType;
    }

    /**
     * @return ID of the entity (person) who performed the action which the notification is about.
     */
    public long getActorId()
    {
        return actorId;
    }

    /**
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     */
    public void setActorId(final long inActorId)
    {
        actorId = inActorId;
    }

    /**
     * @return ID of the entity (person or stream) that the action acted upon.
     */
    public long getDestinationId()
    {
        return destinationId;
    }

    /**
     * @param inDestinationId
     *            ID of the entity (person or stream) that the action acted upon.
     */
    public void setDestinationId(final long inDestinationId)
    {
        destinationId = inDestinationId;
    }

    /**
     * @return ID of the "activity" the event pertained to. In the case of requesting creation of a new group,
     *         "activity" is used in a loose sense, namely the object being acted on. Using a linguistic analogy, this
     *         is the direct object of the sentence. For commenting, this is the ID of the comment (not of the activity
     *         which was commented on).
     */
    public long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId
     *            ID of the activity the event pertained to.
     */
    public void setActivityId(final long inActivityId)
    {
        activityId = inActivityId;
    }
}
