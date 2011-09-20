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
package org.eurekastreams.server.persistence.mappers.cache;

/**
 * Collection of Cache keys.
 *
 */
public final class CacheKeys
{
    /**
     * Hidden constructor.
     */
    private CacheKeys()
    {
        // no-op
    }

    /**
     * Cache key for buffered activities not yet added to cache or lists.
     */
    public static final String BUFFERED_ACTIVITIES = "BufferedActivites:";

    /**
     * Cache key for ordered comment ids by activity id.
     */
    public static final String COMMENT_IDS_BY_ACTIVITY_ID = "ActCmt:";

    /**
     * Key for CommentDTO by id.
     */
    public static final String COMMENT_BY_ID = "Cmt:";

    /**
     * Key for ActivityDTO by id.
     */
    public static final String ACTIVITY_BY_ID = "Act:";

    /**
     * Key for list of activity ids starred by a user.
     */
    public static final String STARRED_BY_PERSON_ID = "PerStar:";

    /**
     * Key for list of people who liked an activity.
     */
    public static final String LIKERS_BY_ACTIVITY_ID = "LikedByFor:";

    /**
     * Key for list of people who liked a resource.
     */
    public static final String LIKERS_BY_RESOURCE_ID = "LikedByForRs:";

    /**
     * Pointer key for person by opensocial id.
     */
    public static final String PERSON_BY_OPEN_SOCIAL_ID = "PerOS:";

    /**
     * Pointer key for person by account id.
     */
    public static final String PERSON_BY_ACCOUNT_ID = "PerAcct:";

    /**
     * Key for person by id.
     */
    public static final String PERSON_BY_ID = "Per:";

    /**
     * Pointer key for domain group by short name.
     */
    public static final String GROUP_BY_SHORT_NAME = "GrpShort:";

    /**
     * Key for domain group by id.
     */
    public static final String GROUP_BY_ID = "Grp:";

    /**
     * Key for followers of a person including said person.
     */
    public static final String FOLLOWERS_BY_PERSON = "Fwr:";

    /**
     * Key for followers of a person.
     */
    public static final String FOLLOWERS_BY_GROUP = "GrpFwr:";

    /**
     * Key for people a given user has followed.
     */
    public static final String PEOPLE_FOLLOWED_BY_PERSON = "PerFld:";

    /**
     * Key for groups a given user has followed.
     */
    public static final String GROUPS_FOLLOWED_BY_PERSON = "GrpFld:";

    /**
     * Key for coordinator ids by group id.
     */
    public static final String COORDINATOR_PERSON_IDS_BY_GROUP_ID = "GrpCoord:";

    /**
     * Key for org ids directly cooridinated by a person.
     */
    public static final String ORG_IDS_DIRECTLY_COORD_BY_PERSON = "OrgIdsCoordByPer:";

    /**
     * Key for list of activity ids of all users being followed by a person.
     */
    public static final String ACTIVITIES_BY_FOLLOWING = "CmpFwg:";

    /**
     * Key for getting the IDs of the system administrators.
     */
    public static final String SYSTEM_ADMINISTRATOR_IDS = "Admins:";

    /**
     * Key for the set of ids of private groups that a user has view access to as a coordinator (org/group).
     */
    public static final String PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR = "PrvGrpVw:";

    /**
     * Key for the count of unread notifications for a user.
     */
    public static final String UNREAD_APPLICATION_ALERT_COUNT_BY_USER = "UnreadAlerts:";

    /**
     * Key for system settings.
     */
    public static final String SYSTEM_SETTINGS = "SystemSettings";

    /**
     * Key for activity security information.
     */
    public static final String ACTIVITY_SECURITY_BY_ID = "ActSec:";

    /**
     * Activity liked by a person.
     */
    public static final String LIKED_BY_PERSON_ID = "PerLike:";

    /**
     * Popular hashag list by stream type and short name.
     */
    public static final String POPULAR_HASH_TAGS_BY_STREAM_TYPE_AND_SHORT_NAME = "PHT:";

    /**
     * Cache key for the everyone activity list.
     */
    public static final String EVERYONE_ACTIVITY_IDS = "ActAll";

    /**
     * List of activity ids for an entity's activity stream.
     */
    public static final String ENTITY_STREAM_BY_SCOPE_ID = "EntStr:";

    /**
     * Cache key to get the stream scope of a person by their account id.
     */
    public static final String STREAM_SCOPE_ID_BY_PERSON_ACCOUNT_ID = "PerSSID:";

    /**
     * Cache key to get the stream scope of a group by the short name.
     */
    public static final String STREAM_SCOPE_ID_BY_GROUP_SHORT_NAME = "GrpSSID:";

    /**
     * Key for PersonPageProperties by id.
     */
    public static final String PERSON_PAGE_PROPERTIES_BY_ID = "PagePropsById:";

    /**
     * Key for tutorial vids set.
     */
    public static final String TUTORIAL_VIDS = "TutorialVids:";

    /**
     * Key for getting AppData with the suffix: [gadget definition id]_[person id].
     */
    public static final String APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID = "AppData:";

    /**
     * Key for theme css by theme uuid.
     */
    public static final String THEME_CSS_BY_UUID = "ThemeCssByUuid:";

    /**
     * Key for theme css hash by theme uuid.
     */
    public static final String THEME_HASH_BY_UUID = "ThemeHashByUuid:";

    /**
     * Key for getting a SharedResource by unique key (lower-cased).
     */
    public static final String SHARED_RESOURCE_BY_UNIQUE_KEY = "SRBUK:";

    /**
     * Key prefix for stream metrics by stream scope id.
     */
    public static final String STREAM_METRICS_BY_STREAM_SCOPE_ID = "Metrics:";

    /**
     * Key to get the cached discovery page lists.
     */
    public static final String DISCOVERY_PAGE_LISTS = "DiscoveryPageLists";

    /**
     * Key for a person's encryption key.
     */
    public static final String CRYPTO_KEY_BY_PERSON_ID = "PerCrK";
}
