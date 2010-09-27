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
package org.eurekastreams.server.search.modelview;

import java.util.Date;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Followable;

/**
 * ModelView for DomainGroup.
 */
public class DomainGroupModelView extends ModelView implements Followable, ActivityRestrictionEntity
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 2996495113622674437L;

    /**
     * form Key.
     */
    public static final String ID_KEY = "id";
    /**
     * form Key.
     */
    public static final String NAME_KEY = "name";
    /**
     * form Key.
     */
    public static final String SHORT_NAME_KEY = "shortName";
    /**
     * form Key.
     */
    public static final String ORG_PARENT_KEY = "orgParent";
    /**
     * form Key.
     */
    public static final String COORDINATORS_KEY = "coordinators";
    /**
     * form Key.
     */
    public static final String PRIVACY_KEY = "publicGroup";
    /**
     * form Key.
     */
    public static final String DESCRIPTION_KEY = "description";
    /**
     * form Key.
     */
    public static final String OVERVIEW_KEY = "overview";
    /**
     * form Key.
     */
    public static final String KEYWORDS_KEY = "keywords";
    /**
     * form Key.
     */
    public static final String URL_KEY = "url";
    /**
     * form Key.
     */
    public static final String STREAM_POSTABLE_KEY = "streamPostable";
    /**
     * form Key.
     */
    public static final String STREAM_COMMENTABLE_KEY = "commentable";

    /** Form key. */
    public static final String SUPPRESS_POST_NOTIF_TO_MEMBER_KEY = "suppressPostNotifToMember";

    /** Form key. */
    public static final String SUPPRESS_POST_NOTIF_TO_COORDINATOR_KEY = "suppressPostNotifToCoordinator";

    /**
     * form error message.
     */
    public static final String KEYWORD_MESSAGE = "Keywords must be no more than "
            + BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + " characters each.";

    /**
     * The number of people following this group.
     */
    private int followersCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The name of this group.
     */
    private String name = UNINITIALIZED_STRING_VALUE;

    /**
     * The short name of this group.
     */
    private String shortName = UNINITIALIZED_STRING_VALUE;

    /**
     * The description of this group.
     */
    private String description = UNINITIALIZED_STRING_VALUE;

    /**
     * The date this group was added to the system.
     */
    private Date dateAdded = UNINITIALIZED_DATE_VALUE;

    /**
     * The id of the parent organization.
     */
    private long parentOrganizationId = UNINITIALIZED_LONG_VALUE;

    /**
     * The short name of the parent organization.
     */
    private String parentOrganizationShortName = UNINITIALIZED_STRING_VALUE;

    /**
     * The name of the parent organization.
     */
    private String parentOrganizationName = UNINITIALIZED_STRING_VALUE;

    /**
     * The number of updates this group has.
     */
    private int updatesCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The group's avatar id.
     */
    private String avatarId = UNINITIALIZED_STRING_VALUE;

    /**
     * If the group is public.
     */
    private Boolean isPublic = false;

    /**
     * The account id of the person that created this group.
     */
    private String personCreatedByAccountId = UNINITIALIZED_STRING_VALUE;

    /**
     * The ID of the person that created this group - used internally, not sent to client. "transient" omits it from
     * serialization.
     */
    private transient Long personCreatedById = UNINITIALIZED_LONG_VALUE;

    /**
     * The display name of the person that created this group.
     */
    private String personCreatedByDisplayName = UNINITIALIZED_STRING_VALUE;

    /**
     * Stream id for this group.
     */
    private long streamId = UNINITIALIZED_LONG_VALUE;

    /**
     * Flag to determine if group's activities can be commented on.
     */
    private boolean commentable = true;

    /**
     * Flag to determine if group's stream can be posted on.
     */
    private boolean streamPostable = true;

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        // let the parent class get its properties first
        super.loadProperties(properties);

        if (properties.containsKey("dateAdded"))
        {
            setDateAdded((Date) properties.get("dateAdded"));
        }
        if (properties.containsKey("avatarId"))
        {
            setAvatarId((String) properties.get("avatarId"));
        }
        if (properties.containsKey("parentOrganizationId"))
        {
            parentOrganizationId = (Long) properties.get("parentOrganizationId");
        }
        if (properties.containsKey("parentOrganizationShortName"))
        {
            setParentOrganizationShortName((String) properties.get("parentOrganizationShortName"));
        }
        if (properties.containsKey("parentOrganizationName"))
        {
            setParentOrganizationName((String) properties.get("parentOrganizationName"));
        }
        if (properties.containsKey("followersCount"))
        {
            setFollowersCount((Integer) properties.get("followersCount"));
        }
        if (properties.containsKey("updatesCount"))
        {
            setUpdatesCount((Integer) properties.get("updatesCount"));
        }
        if (properties.containsKey("name"))
        {
            setName((String) properties.get("name"));
        }
        if (properties.containsKey("description"))
        {
            setDescription((String) properties.get("description"));
        }
        if (properties.containsKey("shortName"))
        {
            setShortName(((String) properties.get("shortName")));
        }
        if (properties.containsKey("isPublic"))
        {
            setIsPublic((Boolean) properties.get("isPublic"));
        }
        if (properties.containsKey("personCreatedByAccountId"))
        {
            setPersonCreatedByAccountId((String) properties.get("personCreatedByAccountId"));
        }
        if (properties.containsKey("personCreatedByDisplayName"))
        {
            setPersonCreatedByDisplayName((String) properties.get("personCreatedByDisplayName"));
        }
        if (properties.containsKey("personCreatedById"))
        {
            personCreatedById = (Long) properties.get("personCreatedById");
        }
        if (properties.containsKey("streamId"))
        {
            setStreamId((Long) properties.get("streamId"));
        }
        if (properties.containsKey("commentable"))
        {
            setCommentable((Boolean) properties.get("commentable"));
        }
        if (properties.containsKey("streamPostable"))
        {
            setStreamPostable((Boolean) properties.get("streamPostable"));
        }
    }

    /**
     * Sets is public.
     *
     * @param inIsPublic
     *            sets isPublic.
     */
    public void setIsPublic(final Boolean inIsPublic)
    {
        isPublic = inIsPublic;
    }

    /**
     * @return isPublic.
     */
    public Boolean isPublic()
    {
        return isPublic;
    }

    /**
     * Get the entity name.
     *
     * @return the entity name
     */
    @Override
    protected String getEntityName()
    {
        return "DomainGroup";
    }

    /**
     * Get the group's avatar id.
     *
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * Set the group's avatar id.
     *
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        this.avatarId = inAvatarId;
    }

    /**
     * Get the number of people following this group.
     *
     * @return the followersCount
     */
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * Set the number of people following this group.
     *
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        this.followersCount = inFollowersCount;
    }

    /**
     * The the number of updates for this group.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this group.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        this.updatesCount = inUpdatesCount;
    }

    /**
     * Get the shortname.
     *
     * @return the shortname.
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * Set the shortname.
     *
     * @param inShortName
     *            the shortname.
     */
    public void setShortName(final String inShortName)
    {
        this.shortName = inShortName;
    }

    /**
     * Get the name of the group.
     *
     * @return the name of the group
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the group.
     *
     * @param inName
     *            the name to set
     */
    public void setName(final String inName)
    {
        this.name = inName;
    }

    /**
     * Get the description of the group.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description of the group.
     *
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        this.description = inDescription;
    }

    /**
     * Get the date the group was added to the system.
     *
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * Set the date the group was added to the system.
     *
     * @param inDateAdded
     *            the dateAdded to set
     */
    public void setDateAdded(final Date inDateAdded)
    {
        this.dateAdded = inDateAdded;
    }

    /**
     * @return The group's parent organization's id.
     */
    public long getParentOrganizationId()
    {
        return parentOrganizationId;
    }

    /**
     * @param inParentOrganizationId
     *            The group's parent organization's id.
     */
    public void setParentOrganizationId(final long inParentOrganizationId)
    {
        parentOrganizationId = inParentOrganizationId;
    }

    /**
     * Get the group's parent organization's short name.
     *
     * @return the parentOrganizationShortName
     */
    public String getParentOrganizationShortName()
    {
        return parentOrganizationShortName;
    }

    /**
     * Set the group's parent organization's short name.
     *
     * @param inParentOrganizationShortName
     *            the parentOrganizationShortName to set
     */
    public void setParentOrganizationShortName(final String inParentOrganizationShortName)
    {
        this.parentOrganizationShortName = inParentOrganizationShortName;
    }

    /**
     * Get the group's parent organization's name.
     *
     * @return the parentOrganizationName
     */
    public String getParentOrganizationName()
    {
        return parentOrganizationName;
    }

    /**
     * Set the group's parent organization's name.
     *
     * @param inParentOrganizationName
     *            the parentOrganizationName to set
     */
    public void setParentOrganizationName(final String inParentOrganizationName)
    {
        this.parentOrganizationName = inParentOrganizationName;
    }

    /**
     *
     * @return the person created by display name.
     */
    public String getPersonCreatedByDisplayName()
    {
        return personCreatedByDisplayName;
    }

    /**
     * @param inPersonCreatedByDisplayName
     *            The created by display name to set.
     */
    public void setPersonCreatedByDisplayName(final String inPersonCreatedByDisplayName)
    {
        personCreatedByDisplayName = inPersonCreatedByDisplayName;
    }

    /**
     * @return The person created by account ID.
     */
    public String getPersonCreatedByAccountId()
    {
        return personCreatedByAccountId;
    }

    /**
     * @return The Person created by Id.
     */
    public Long getPersonCreatedById()
    {
        return personCreatedById;
    }

    /**
     * This is only available on the server and is not serialized and sent over the wire.
     *
     * @param inPersonCreatedById
     *            The person created by id.
     */
    public void setPersonCreatedById(final Long inPersonCreatedById)
    {
        personCreatedById = inPersonCreatedById;
    }

    /**
     * @param inPersonCreatedByAccountId
     *            The person created my account ID.
     */
    public void setPersonCreatedByAccountId(final String inPersonCreatedByAccountId)
    {
        personCreatedByAccountId = inPersonCreatedByAccountId;
    }

    /**
     * Set the entity id.
     *
     * @param inEntityId
     *            the entity id of the domain group.
     */
    // TODO: pull this out - this is temporary
    @Override
    public void setEntityId(final long inEntityId)
    {
        super.setEntityId(inEntityId);
    }

    /**
     * @return the streamId
     */
    public long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final long inStreamId)
    {
        this.streamId = inStreamId;
    }

    /**
     * @param inCommentable
     *            The commentable flag value to set.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return The commentable flag.
     */
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @return The streamPostable flag.
     */
    public boolean isStreamPostable()
    {
        return streamPostable;
    }

    /**
     * @param inStreamPostable
     *            The streamPostable flag value to set.
     */
    public void setStreamPostable(final boolean inStreamPostable)
    {
        streamPostable = inStreamPostable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId()
    {
        return super.getEntityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId()
    {
        return shortName;
    }
}
