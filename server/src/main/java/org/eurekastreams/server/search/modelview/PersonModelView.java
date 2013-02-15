/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.HasEmail;
import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.domain.dto.StreamDTO;

/**
 * A lightweight view of a Person containing everything needed for display of a search result of an Person.
 */
public class PersonModelView extends ModelView implements Serializable, HasEmail, Followable,
        ActivityRestrictionEntity, AvatarEntity, Bannerable, Identifiable, StreamDTO
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -9056680427782118919L;

    /**
     * The key for the title.
     */
    public static final String TITILE_KEY = "title";

    /**
     * The key for the nick name/preferred name.
     */
    public static final String PREFERREDNAME_KEY = "preferredName";
    /**
     * key for description.
     */
    public static final String DESCRIPTION_KEY = "jobDescription";
    /**
     * key for skills.
     */
    public static final String SKILLS_KEY = "skills";

    /**
     * key for work phone.
     */
    public static final String WORKPHONE_KEY = "workPhone";
    /**
     * key for cell phone.
     */
    public static final String CELLPHONE_KEY = "cellPhone";
    /**
     * key for fax.
     */
    public static final String FAX_KEY = "fax";
    /**
     * key for email.
     */
    public static final String EMAIL_KEY = "email";

    /**
     * form error message.
     */
    public static final String SKILLS_MESSAGE = "Interests keywords support up to "
            + BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + " characters each.";

    /**
     * Role in the system.
     */
    public enum Role implements Serializable
    {
        /**
         * Represents *any* group coordinator.
         */
        GROUP_COORDINATOR, SYSTEM_ADMIN
    }

    /**
     * Set of roles for the person.
     */
    private Set<Role> roles = new HashSet<Role>();

    /**
     * The date this was added.
     */
    private Date dateAdded = null;

    /**
     * The account id of this person.
     */
    private String accountId = null;

    /**
     * The open social id of this person.
     */
    private String openSocialId = null;

    /**
     * The person's title.
     */
    private String title = null;

    /**
     * The person's description.
     */
    private String description = null;

    /**
     * The number of people following this person.
     */
    private int followersCount = -1;

    /**
     * The number of people following this person.
     */
    private int followingCount = -1;

    /**
     * The number of groups a person is in.
     */
    private int groupsCount = -1;

    /**
     * Line index for composite streams.
     */
    private int compositeStreamHiddenLineIndex = -1;

    /**
     * Line index for group streams.
     */
    private int groupStreamHiddenLineIndex = -1;

    /**
     * The person's display name.
     */
    private String displayName = null;

    /**
     * The person's display name suffix.
     */
    private String displayNameSuffix = null;

    /**
     * The person's avatar id.
     */
    private String avatarId = null;

    /**
     * Avatar crop size.
     */
    private Integer avatarCropSize = null;

    /**
     * Avatar crop value x.
     */
    private Integer avatarCropX = null;

    /**
     * Avatar crop value y.
     */
    private Integer avatarCropY = null;

    /**
     * The number of updates for this person.
     */
    private int updatesCount = -1;

    /**
     * Stream id for this person.
     */
    private long streamId = -1;

    /**
     * Set of videos a user has opted out of seeing.
     */
    private HashSet<Long> optOutVideos = new HashSet<Long>();

    /**
     * ToS acceptance flag.
     */
    private boolean tosAcceptance = false;

    /**
     * Flag to determine if person's activities can be commented on.
     */
    private boolean commentable = true;

    /**
     * Flag to determine if person's stream can be posted on.
     */
    private boolean streamPostable = true;

    /**
     * Authentication type.
     */
    private AuthenticationType authenticationType = AuthenticationType.NOTSET;

    /**
     * Email address for this person.
     */
    private String email = null;

    /**
     * The date the current user last accepted the terms of service.
     */
    private Date lastAcceptedTermsOfService = null;

    /**
     * Map of additional properties.
     */
    private HashMap<String, String> additionalProperties;

    /**
     * If user's account is locked.
     */
    private boolean accountLocked = false;

    /**
     * User's last name.
     */
    private String lastName = null;

    /**
     * User's preferred name.
     */
    private String preferredName = null;

    /**
     * User's job description.
     */
    private String jobDescription = null;

    /**
     * Work phone number.
     */
    private String workPhone = null;

    /**
     * Cell phone number.
     */
    private String cellPhone = null;

    /**
     * Fax number.
     */
    private String fax = null;

    /**
     * A person's interests - null if not set, empty list if none.
     */
    private List<String> interests = null;

    /**
     * Banner id.
     */
    private String bannerId = null;

    /**
     * Banner entity id - transient, may be set to the person's parent org's banner id.
     */
    private Long bannerEntityId = null;

    /**
     * Company name.
     */
    private String companyName;

    /**
     * Follower status.
     */
    private FollowerStatus followerStatus;

    /**
     * Stream Scope Id.
     */
    private Long streamScopeId;

    /**
     * Get the name of this entity.
     * 
     * @return the name of this entity
     */
    @Override
    protected String getEntityName()
    {
        return "Person";
    }

    /**
     * Empty constructor.
     */
    public PersonModelView()
    {
    }

    /**
     * Constructor suitable for StreamDTO.
     * 
     * This is identical to the other constructor, but this one takes an Integer for the followerCount. The reason for
     * this is that the field is an integer type.
     * 
     * @param inId
     *            the person id
     * @param inAccountId
     *            the person account id
     * @param inPreferredName
     *            the person preferred name
     * @param inLastName
     *            the person last name
     * @param inDisplayName
     *            the display name
     * @param inDisplayNameSuffix
     *            the display name suffix to show, or null if none
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date this stream was created
     * @param inStreamScopeId
     *            the stream scope id
     */
    public PersonModelView(final Long inId, final String inAccountId, final String inPreferredName,
            final String inLastName, final String inDisplayName, final String inDisplayNameSuffix,
            final Integer inFollowerCount, final Date inDateAdded, final Long inStreamScopeId)
    {
        this(inId, inAccountId, inPreferredName, inLastName, inDisplayName, inDisplayNameSuffix, new Long(
                inFollowerCount.longValue()), inDateAdded, inStreamScopeId);
    }

    /**
     * Constructor suitable for StreamDTO.
     * 
     * This is identical to the other constructor, but this one takes a Long for the followerCount. The reason for this
     * is that COUNT() queries return long.
     * 
     * @param inId
     *            the person id
     * @param inAccountId
     *            the person account id
     * @param inPreferredName
     *            the person preferred name
     * @param inLastName
     *            the person last name
     * @param inDisplayName
     *            the display name
     * @param inDisplayNameSuffix
     *            the display name suffix to show, or null if none
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date this stream was created
     * @param inStreamScopeId
     *            the stream scope id
     */
    public PersonModelView(final Long inId, final String inAccountId, final String inPreferredName,
            final String inLastName, final String inDisplayName, final String inDisplayNameSuffix,
            final Long inFollowerCount, final Date inDateAdded, final Long inStreamScopeId)
    {
        setEntityId(inId);
        setAccountId(inAccountId);
        setPreferredName(inPreferredName);
        setLastName(inLastName);
        setDisplayName(inDisplayName);
        setDisplayNameSuffix(inDisplayNameSuffix);
        setFollowersCount(inFollowerCount.intValue());
        setDateAdded(inDateAdded);
        setStreamScopeId(inStreamScopeId);
    }

    /**
     * Load this object's properties from the input Map.
     * 
     * @param properties
     *            the Map of the properties to load
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        // let the parent class get its properties first
        super.loadProperties(properties);

        if (properties.containsKey("dateAdded"))
        {
            setDateAdded((Date) properties.get("dateAdded"));
        }
        if (properties.containsKey("accountId"))
        {
            setAccountId((String) properties.get("accountId"));
        }
        if (properties.containsKey("openSocialId"))
        {
            setOpenSocialId((String) properties.get("openSocialId"));
        }
        if (properties.containsKey("avatarId"))
        {
            setAvatarId((String) properties.get("avatarId"));
        }
        if (properties.containsKey("avatarCropSize"))
        {
            setAvatarCropSize((Integer) properties.get("avatarCropSize"));
        }
        if (properties.containsKey("avatarCropX"))
        {
            setAvatarCropX((Integer) properties.get("avatarCropX"));
        }
        if (properties.containsKey("avatarCropY"))
        {
            setAvatarCropY((Integer) properties.get("avatarCropY"));
        }
        if (properties.containsKey("optOutVideoIds"))
        {
            String videoIds = (String) properties.get("optOutVideoIds");
            HashSet<Long> videoIdSet = new HashSet();

            if (videoIds != null && !(videoIds.isEmpty()))
            {
                String[] videoIdsArray = videoIds.split(";");

                for (String vidId : videoIdsArray)
                {
                    // if it tokenized the ending ; then don't set a blank long.
                    if (!vidId.isEmpty())
                    {
                        videoIdSet.add(Long.valueOf(vidId.trim()));
                    }
                }
            }
            setOptOutVideos(videoIdSet);
        }
        if (properties.containsKey("displayName"))
        {
            setDisplayName((String) properties.get("displayName"));
        }
        else
        {
            // build displayName
            if (properties.containsKey("lastName") && properties.containsKey("preferredName"))
            {
                String dName = (String) properties.get("preferredName") + " " + (String) properties.get("lastName");
                // this should be done as a ClassBridge but ClassBridges can't currently be projected
                if (properties.containsKey("displayNameSuffix") && properties.get("displayNameSuffix") != null)
                {
                    dName += (String) properties.get("displayNameSuffix");
                }
                setDisplayName(dName);
            }
        }
        if (properties.containsKey("companyName"))
        {
            setCompanyName((String) properties.get("companyName"));
        }
        if (properties.containsKey("lastName"))
        {
            setLastName((String) properties.get("lastName"));
        }
        if (properties.containsKey(PREFERREDNAME_KEY))
        {
            setPreferredName((String) properties.get(PREFERREDNAME_KEY));
        }
        if (properties.containsKey("title"))
        {
            setTitle((String) properties.get("title"));
        }
        if (properties.containsKey("jobDescription"))
        {
            setDescription((String) properties.get("jobDescription"));
        }
        if (properties.containsKey("followersCount"))
        {
            setFollowersCount((Integer) properties.get("followersCount"));
        }
        if (properties.containsKey("followingCount"))
        {
            setFollowingCount((Integer) properties.get("followingCount"));
        }
        if (properties.containsKey("groupsCount"))
        {
            setGroupsCount((Integer) properties.get("groupsCount"));
        }
        if (properties.containsKey("updatesCount"))
        {
            setUpdatesCount((Integer) properties.get("updatesCount"));
        }
        if (properties.containsKey("streamId"))
        {
            setStreamId((Long) properties.get("streamId"));
        }
        if (properties.containsKey("compositeStreamHiddenLineIndex"))
        {
            setCompositeStreamHiddenLineIndex((Integer) properties.get("compositeStreamHiddenLineIndex"));
        }
        if (properties.containsKey("groupStreamHiddenLineIndex"))
        {
            setGroupStreamHiddenLineIndex((Integer) properties.get("groupStreamHiddenLineIndex"));
        }
        if (properties.containsKey("tosAcceptance"))
        {
            setTosAcceptance((Boolean) properties.get("tosAcceptance"));
        }
        if (properties.containsKey("authenticationType"))
        {
            setAuthenticationType((AuthenticationType) properties.get("authenticationType"));
        }
        if (properties.containsKey("email"))
        {
            setEmail((String) properties.get("email"));
        }
        if (properties.containsKey("commentable"))
        {
            setCommentable((Boolean) properties.get("commentable"));
        }
        if (properties.containsKey("streamPostable"))
        {
            setStreamPostable((Boolean) properties.get("streamPostable"));
        }
        if (properties.containsKey("lastAcceptedTermsOfService"))
        {
            setLastAcceptedTermsOfService((Date) properties.get("lastAcceptedTermsOfService"));
        }
        if (properties.containsKey("additionalProperties"))
        {
            setAdditionalProperties((HashMap<String, String>) properties.get("additionalProperties"));
        }
        if (properties.containsKey("accountLocked"))
        {
            if ((Boolean) properties.get("accountLocked"))
            {
                setAccountLocked(true);
            }
        }
        if (properties.containsKey("accountDeactivated"))
        {
            if ((Boolean) properties.get("accountDeactivated"))
            {
                setAccountLocked(true);
            }
        }
        if (properties.containsKey("jobDescription"))
        {
            setJobDescription((String) properties.get("jobDescription"));
        }
        if (properties.containsKey("workPhone"))
        {
            setWorkPhone((String) properties.get("workPhone"));
        }
        if (properties.containsKey("cellPhone"))
        {
            setCellPhone((String) properties.get("cellPhone"));
        }
        if (properties.containsKey("fax"))
        {
            setFax((String) properties.get("fax"));
        }
        if (properties.containsKey("interests"))
        {
            setInterests((List<String>) properties.get("interests"));
        }
        if (properties.containsKey("bannerId"))
        {
            setBannerId((String) properties.get("bannerId"));
        }
    }

    /**
     * Get the date the person was added to the system.
     * 
     * @return the dateAdded
     */
    @Override
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * Set the date the person was added to the system.
     * 
     * @param inDateAdded
     *            the dateAdded to set
     */
    public void setDateAdded(final Date inDateAdded)
    {
        dateAdded = inDateAdded;
    }

    /**
     * Get the person's account id.
     * 
     * @return the accountId
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * Set the person's account id.
     * 
     * @param inAccountId
     *            the accountId to set
     */
    public void setAccountId(final String inAccountId)
    {
        accountId = inAccountId;
    }

    /**
     * Get the person's OpenSocial id.
     * 
     * @return the openSocialId
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * Set the person's OpenSocial id.
     * 
     * @param inOpenSocialId
     *            the openSocialId to set
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        openSocialId = inOpenSocialId;
    }

    /**
     * Get the person's title.
     * 
     * @return the title
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the person's title.
     * 
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        title = inTitle;
    }

    /**
     * Get the person's description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the person's description.
     * 
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * Get the number of people following this person.
     * 
     * @return the followersCount
     */
    @Override
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * @return the groupsCount
     */
    public int getGroupsCount()
    {
        return groupsCount;
    }

    /**
     * @param inGroupsCount
     *            the groupsCount to set
     */
    public void setGroupsCount(final int inGroupsCount)
    {
        groupsCount = inGroupsCount;
    }

    /**
     * Set the number of people following this person.
     * 
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        followersCount = inFollowersCount;
    }

    /**
     * Get the person's display name.
     * 
     * @return the displayName
     */
    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Set the person's display name.
     * 
     * @param inDisplayName
     *            the displayName to set
     */
    @Override
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }

    /**
     * Get the person's avatar id.
     * 
     * @return the avatarId
     */
    @Override
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * Set the person's avatar id.
     * 
     * @param inAvatarId
     *            the avatarId to set
     */
    @Override
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * Set the entity id.
     * 
     * @param inEntityId
     *            the entity id of the person.
     */
    // TODO: pull this out - this is temporary
    @Override
    public void setEntityId(final long inEntityId)
    {
        super.setEntityId(inEntityId);
    }

    /**
     * The the number of updates for this person.
     * 
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this person.
     * 
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        updatesCount = inUpdatesCount;
    }

    /**
     * @return the streamId
     */
    @Override
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
        streamId = inStreamId;
    }

    /**
     * @return the compositeStreamHiddenLineIndex
     */
    public int getCompositeStreamHiddenLineIndex()
    {
        return compositeStreamHiddenLineIndex;
    }

    /**
     * @param inCompositeStreamHiddenLineIndex
     *            the compositeStreamHiddenLineIndex to set
     */
    public void setCompositeStreamHiddenLineIndex(final int inCompositeStreamHiddenLineIndex)
    {
        compositeStreamHiddenLineIndex = inCompositeStreamHiddenLineIndex;
    }

    /**
     * @return the groupStreamHiddenLineIndex
     */
    public int getGroupStreamHiddenLineIndex()
    {
        return groupStreamHiddenLineIndex;
    }

    /**
     * @param inGroupStreamHiddenLineIndex
     *            the groupStreamHiddenLineIndex to set
     */
    public void setGroupStreamHiddenLineIndex(final int inGroupStreamHiddenLineIndex)
    {
        groupStreamHiddenLineIndex = inGroupStreamHiddenLineIndex;
    }

    /**
     * Set the roles.
     * 
     * @param inRoles
     *            the roles.
     */
    public void setRoles(final HashSet<Role> inRoles)
    {
        roles = inRoles;
    }

    /**
     * Gets the roles.
     * 
     * @return the roles.
     */
    public Set<Role> getRoles()
    {
        return roles;
    }

    /**
     * @return the tosAcceptance
     */
    public boolean getTosAcceptance()
    {
        return tosAcceptance;
    }

    /**
     * @param inToSAcceptance
     *            the tosAcceptance flag to set
     */
    public void setTosAcceptance(final boolean inToSAcceptance)
    {
        tosAcceptance = inToSAcceptance;
    }

    /**
     * @return the authenticationType
     */
    public AuthenticationType getAuthenticationType()
    {
        return authenticationType;
    }

    /**
     * @param inAuthenticationType
     *            the authenticationType to set
     */
    public void setAuthenticationType(final AuthenticationType inAuthenticationType)
    {
        authenticationType = inAuthenticationType;
    }

    /**
     * @return Person's email.
     */
    @Override
    public String getEmail()
    {
        return email;
    }

    /**
     * @param inEmail
     *            Person's email.
     */
    public void setEmail(final String inEmail)
    {
        email = inEmail;
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
    @Override
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @return The streamPostable flag.
     */
    @Override
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
     * 
     * @return videos the person has opted out of.
     */
    public HashSet<Long> getOptOutVideos()
    {
        return optOutVideos;
    }

    /**
     * set the set of videos a person has opted out of.
     * 
     * @param inOptOutVideos
     *            the set of videos.
     * 
     */
    public void setOptOutVideos(final HashSet<Long> inOptOutVideos)
    {
        optOutVideos = inOptOutVideos;
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
        return accountId;
    }

    /**
     * Get the unique key for this stream.
     * 
     * @return the person's account id
     */
    @Override
    public String getStreamUniqueKey()
    {
        return accountId;
    }

    /**
     * Get the last date the person accepted the terms of service.
     * 
     * @return the last date the person accepted the terms of service
     */
    public Date getLastAcceptedTermsOfService()
    {
        return lastAcceptedTermsOfService;
    }

    /**
     * Set the last date the person accepted the terms of service.
     * 
     * @param inLastAcceptedTermsOfService
     *            the last date the person accepted the terms of service.
     */
    public void setLastAcceptedTermsOfService(final Date inLastAcceptedTermsOfService)
    {
        lastAcceptedTermsOfService = inLastAcceptedTermsOfService;
    }

    /**
     * The additionalProperties setter.
     * 
     * @param inAdditionalProperties
     *            the properties hashmap to set
     */
    public void setAdditionalProperties(final HashMap<String, String> inAdditionalProperties)
    {
        additionalProperties = inAdditionalProperties;
    }

    /**
     * The additionalProperties getter.
     * 
     * @return additionalProperties hashmap.
     */
    public HashMap<String, String> getAdditionalProperties()
    {
        return additionalProperties;
    }

    /**
     * @return if the account is locked.
     */
    public boolean isAccountLocked()
    {
        return accountLocked;
    }

    /**
     * @param inAccountLocked
     *            if the account is locked.
     */
    public void setAccountLocked(final boolean inAccountLocked)
    {
        accountLocked = inAccountLocked;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param inLastName
     *            the lastName to set
     */
    public void setLastName(final String inLastName)
    {
        lastName = inLastName;
    }

    /**
     * @return the followingCount
     */
    public int getFollowingCount()
    {
        return followingCount;
    }

    /**
     * @param inFollowingCount
     *            the followingCount to set
     */
    public void setFollowingCount(final int inFollowingCount)
    {
        followingCount = inFollowingCount;
    }

    /**
     * @return the preferredName
     */
    public String getPreferredName()
    {
        return preferredName;
    }

    /**
     * @param inPreferredName
     *            the preferredName to set
     */
    public void setPreferredName(final String inPreferredName)
    {
        preferredName = inPreferredName;
    }

    /**
     * @return the jobDescription
     */
    public String getJobDescription()
    {
        return jobDescription;
    }

    /**
     * @param inJobDescription
     *            the jobDescription to set
     */
    public void setJobDescription(final String inJobDescription)
    {
        jobDescription = inJobDescription;
    }

    /**
     * @return the avatarCropSize
     */
    @Override
    public Integer getAvatarCropSize()
    {
        return avatarCropSize;
    }

    /**
     * @param inAvatarCropSize
     *            the avatarCropSize to set
     */
    @Override
    public void setAvatarCropSize(final Integer inAvatarCropSize)
    {
        avatarCropSize = inAvatarCropSize;
    }

    /**
     * @return the avatarCropX
     */
    @Override
    public Integer getAvatarCropX()
    {
        return avatarCropX;
    }

    /**
     * @param inAvatarCropX
     *            the avatarCropX to set
     */
    @Override
    public void setAvatarCropX(final Integer inAvatarCropX)
    {
        avatarCropX = inAvatarCropX;
    }

    /**
     * @return the avatarCropY
     */
    @Override
    public Integer getAvatarCropY()
    {
        return avatarCropY;
    }

    /**
     * @param inAvatarCropY
     *            the avatarCropY to set
     */
    @Override
    public void setAvatarCropY(final Integer inAvatarCropY)
    {
        avatarCropY = inAvatarCropY;
    }

    /**
     * @return the cellPhone
     */
    public String getCellPhone()
    {
        return cellPhone;
    }

    /**
     * @param inCellPhone
     *            the cellPhone to set
     */
    public void setCellPhone(final String inCellPhone)
    {
        cellPhone = inCellPhone;
    }

    /**
     * @return the fax
     */
    public String getFax()
    {
        return fax;
    }

    /**
     * @param inFax
     *            the fax to set
     */
    public void setFax(final String inFax)
    {
        fax = inFax;
    }

    /**
     * @return the workPhone
     */
    public String getWorkPhone()
    {
        return workPhone;
    }

    /**
     * @param inWorkPhone
     *            the workPhone to set
     */
    public void setWorkPhone(final String inWorkPhone)
    {
        workPhone = inWorkPhone;
    }

    /**
     * @return the interests
     */
    public List<String> getInterests()
    {
        return interests;
    }

    /**
     * @param inInterests
     *            the interests to set
     */
    public void setInterests(final List<String> inInterests)
    {
        interests = inInterests;
    }

    /**
     * @see org.eurekastreams.server.domain.Bannerable#getBannerEntityId()
     * @return the Person id
     */
    @Override
    public Long getBannerEntityId()
    {
        return bannerEntityId;
    }

    /**
     * @see org.eurekastreams.server.domain.Bannerable#getBannerId()
     * @return the banner id
     */
    @Override
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * @see org.eurekastreams.server.domain.Bannerable#setBannerEntityId(java.lang.Long)
     * @param inBannerEntityId
     *            the person id
     */
    @Override
    public void setBannerEntityId(final Long inBannerEntityId)
    {
        bannerEntityId = inBannerEntityId;
    }

    /**
     * @see org.eurekastreams.server.domain.Bannerable#setBannerId(java.lang.String)
     * @param inBannerId
     *            the banner id
     */
    @Override
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }

    /**
     * Get the company name.
     * 
     * @return the company name.
     */
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * Set the company name.
     * 
     * @param inCompanyName
     *            the company name.
     */
    public void setCompanyName(final String inCompanyName)
    {
        companyName = inCompanyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType()
    {
        return EntityType.PERSON;
    }

    @Override
    public FollowerStatus getFollowerStatus()
    {
        return followerStatus;
    }

    @Override
    public void setFollowerStatus(final FollowerStatus inFollowerStatus)
    {
        followerStatus = inFollowerStatus;
    }

    /**
     * @return the streamScopeId
     */
    @Override
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @param inStreamScopeId
     *            the streamScopeId to set
     */
    public void setStreamScopeId(final Long inStreamScopeId)
    {
        streamScopeId = inStreamScopeId;
    }

    /**
     * Get whether this stream is public (always true for Person).
     * 
     * @return true
     */
    @Override
    public Boolean isPublic()
    {
        return true;
    }

    /**
     * Get the display name suffix.
     * 
     * @return the display name suffix.
     */
    public String getDisplayNameSuffix()
    {
        return displayNameSuffix;
    }

    /**
     * Set the display name suffix.
     * 
     * @param inDisplayNameSuffix
     *            the display name suffix to set
     */
    public void setDisplayNameSuffix(final String inDisplayNameSuffix)
    {
        displayNameSuffix = inDisplayNameSuffix;
    }
}
