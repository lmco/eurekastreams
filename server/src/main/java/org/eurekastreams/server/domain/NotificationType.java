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
package org.eurekastreams.server.domain;

/**
 * Type of notification to be sent to a user.
 */
public enum NotificationType
{
    /** Someone liked an activity. */
    LIKE_ACTIVITY,

    /** Someone posted to the user's stream. */
    POST_TO_PERSONAL_STREAM,

    /** Someone commented on a post in the user's stream. */
    COMMENT_TO_PERSONAL_STREAM,

    /** Someone commented on one of the user's posts. */
    COMMENT_TO_PERSONAL_POST,

    /** Someone commented on a post on which the user also commented. */
    COMMENT_TO_COMMENTED_POST,

    /** Someone commented on a post on which the user saved. */
    COMMENT_TO_SAVED_POST,

    /** Someone started following the user. */
    FOLLOW_PERSON,

    /** Someone posted to a group's stream for a group the user is coordinating. */
    POST_TO_GROUP_STREAM,

    /** Someone posted to a group's stream for a group the user is a member of. */
    POST_TO_JOINED_GROUP,

    /** Someone commented on a post in the group's stream. */
    COMMENT_TO_GROUP_STREAM,

    /** Someone started following the group. */
    FOLLOW_GROUP,

    /** Someone flagged an activity. */
    FLAG_PERSONAL_ACTIVITY,

    /** Someone flagged an activity. */
    FLAG_GROUP_ACTIVITY,

    /** Pending group creation request. */
    REQUEST_NEW_GROUP,

    /** Pending group creation response: approved. */
    REQUEST_NEW_GROUP_APPROVED,

    /** Pending group creation response: denied. */
    REQUEST_NEW_GROUP_DENIED,

    /** Someone requested access to a private group. */
    REQUEST_GROUP_ACCESS,

    /** A coordinator approved access to a private group. */
    REQUEST_GROUP_ACCESS_APPROVED,

    /** A coordinator denied access to a private group. */
    REQUEST_GROUP_ACCESS_DENIED
}
