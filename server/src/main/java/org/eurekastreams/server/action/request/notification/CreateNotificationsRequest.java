/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
    /** Fingerprint. */
    private static final long serialVersionUID = -6688115503640212460L;

    /** Action used to process this request. */
    public static final String ACTION_NAME = "createNotificationsAction";

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
         * Someone liked an activity. Actor=person id of who liked the activity, Destination=not used, Activity=id of
         * the new post. (Data that should be in this request: Author id, destination stream id & type)
         */
        LIKE,

        /**
         * Someone posted to a stream. Actor=person id of who posted, Destination=id of person whose stream was posted
         * to, Activity=id of the new post.
         */
        POST_PERSON_STREAM,

        /**
         * Someone began following a person. Actor=person id who began following, Destination=id of person being
         * followed, Activity=(not used).
         */
        FOLLOW_PERSON,

        /**
         * Someone posted a comment to an activity on a group stream. Actor=person id of who commented, Destination=id
         * of group whose stream was posted to, Activity=id of comment.
         */
        GROUP_COMMENT,

        /**
         * Someone posted to a group stream. Actor=person id of who posted, Destination=id of group whose stream was
         * posted to, Activity=id of the new post.
         */
        POST_GROUP_STREAM,

        /**
         * Someone began following a group. Actor=person id who began following, Destination=id of group being followed,
         * Activity=(not used).
         */
        FOLLOW_GROUP,

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
         * Group creation request approved. Uses GroupActionNotificationsRequest with unset actor ID.
         */
        REQUEST_NEW_GROUP_APPROVED,

        /**
         * Group creation request denied. Uses GroupRemovedNotificationsRequest with unset actor ID.
         */
        REQUEST_NEW_GROUP_DENIED,

        /**
         * Someone requested access to a private group. Actor=person id who requested access, Destination=id of group,
         * Activity=(not used).
         */
        REQUEST_GROUP_ACCESS,

        /**
         * A coordinator approved access to a private group. Actor=coordinator who granted access; Destination=id of
         * group; Activity=person id who requested access (need some field to store it in).
         */
        REQUEST_GROUP_ACCESS_APPROVED,

        /**
         * A coordinator denied access to a private group. Actor=coordinator who denied access; Destination=id of group;
         * Activity=person id who requested access (need some field to store it in).
         */
        REQUEST_GROUP_ACCESS_DENIED,

        /**
         * A notification requested by an external system.
         */
        EXTERNAL_PRE_BUILT
    }

    /** Type of event that occurred. */
    private final RequestType type;

    /** ID of the entity (person) who performed the action which the notification is about. */
    private final long actorId;

    /**
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     */
    public CreateNotificationsRequest(final RequestType inType, final long inActorId)
    {
        type = inType;
        actorId = inActorId;
    }

    /**
     * @return Type of event that occurred.
     */
    public RequestType getType()
    {
        return type;
    }

    /**
     * @return ID of the entity (person) who performed the action which the notification is about.
     */
    public long getActorId()
    {
        return actorId;
    }
}
