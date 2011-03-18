/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

package org.eurekastreams.server.domain.stream;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * A view of an Activity containing everything needed for display.
 */
public class ActivityDTO extends ModelView implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = 4875694663319758375L;

    /**
     * Constructor.
     */
    public ActivityDTO()
    {
        actor = new StreamEntityDTO();
        originalActor = new StreamEntityDTO();
        proxyActor = new StreamEntityDTO();
        destinationStream = new StreamEntityDTO();
    }

    /**
     * id of the activity.
     */
    private long id;

    /**
     * Verb for the activity (what happened to caused this activity to appear on a stream).
     */
    private ActivityVerb verb;

    /**
     * Type of activity.
     */
    private BaseObjectType baseObjectType;

    /**
     * Map of parameter names and values for this activity object.
     */
    private HashMap<String, String> baseObjectProperties;

    /**
     * The actor/creator of the activity.
     */
    private StreamEntityDTO actor;

    /**
     * The original creator of the activity - used during sharing.
     */
    private StreamEntityDTO originalActor;

    /**
     * The actor inside an application that created the activity.
     */
    private StreamEntityDTO proxyActor;

    /**
     * Information regarding the stream that this activity was posted to.
     */
    private StreamEntityDTO destinationStream;

    /**
     * Location for the activity.
     */
    private String location;

    /**
     * Mood of the activity.
     */
    private String mood;

    /**
     * Annotation for the activity.
     */
    private String annotation;

    /**
     * Was the activity starred.
     */
    private Boolean starred;

    /**
     * Date/time for creation of the activity.
     */
    private Date postedTime;

    /**
     * First comment for this activity.
     */
    private CommentDTO firstComment;

    /**
     * Last comment for this activity.
     */
    private CommentDTO lastComment;

    /**
     * boolean representing the ultimate state if an activity is commentable. Not set by default. has to be set by a
     * filter.
     */
    private boolean commentable = false;

    /**
     * Total number of comments for this activity.
     */
    private int commentCount = 0;

    /**
     * Full list of comments.
     */
    private List<CommentDTO> comments;

    /**
     * boolean representing if the comment is able to be deleted by user.
     */
    private Boolean deletable = false;

    /**
     * Whether the destination stream is public.
     */
    private Boolean isDestinationStreamPublic;

    /**
     * boolean representing if the activity is shareable. Not updated by default. has to be set by a filter.
     */
    private Boolean shareable = false;

    /**
     * App type, plugin or gadget.
     */
    private EntityType appType;

    /**
     * App name (i.e. Google Reader).
     */
    private String appName;

    /**
     * App id in DB.
     */
    private Long appId;

    /**
     * App source.
     */
    private String appSource;

    /**
     * If the activity is liked by the current user.
     */
    private Boolean liked = false;

    /**
     * The number of times the activity has been liked.
     */
    private Integer likeCount = 0;

    /**
     * ID of organization "owning" the entity whose stream the activity is posted to.
     */
    private long recipientParentOrgId = UNINITIALIZED_LONG_VALUE;

    /**
     * List of people who have liked the activity.
     */
    private List<PersonModelView> likers;

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @Override
    @SuppressWarnings("unchecked")
    public void loadProperties(final Map<String, Object> properties)
    {
        super.loadProperties(properties);

        if (properties.containsKey("id"))
        {
            setId((Long) properties.get("id"));
        }
        if (properties.containsKey("verb"))
        {
            setVerb((ActivityVerb) properties.get("verb"));
        }
        if (properties.containsKey("baseObjectType"))
        {
            setBaseObjectType((BaseObjectType) properties.get("baseObjectType"));
        }
        if (properties.containsKey("baseObjectProperties"))
        {
            setBaseObjectProperties((HashMap<String, String>) properties.get("baseObjectProperties"));
        }
        if (properties.containsKey("location"))
        {
            setLocation((String) properties.get("location"));
        }
        if (properties.containsKey("mood"))
        {
            setMood((String) properties.get("mood"));
        }
        if (properties.containsKey("annotation"))
        {
            setAnnotation((String) properties.get("Annotation"));
        }
        if (properties.containsKey("postedTime"))
        {
            setPostedTime((Date) properties.get("postedTime"));
        }
        if (properties.containsKey("starred"))
        {
            setStarred((Boolean) properties.get("starred"));
        }
        if (properties.containsKey("firstComment"))
        {
            setFirstComment((CommentDTO) properties.get("firstComment"));
        }
        if (properties.containsKey("lastComment"))
        {
            setLastComment((CommentDTO) properties.get("lastComment"));
        }
        if (properties.containsKey("commentCount"))
        {
            setCommentCount((Integer) properties.get("commentCount"));
        }
        if (properties.containsKey("destinationStreamScopeId"))
        {
            getDestinationStream().setId((Long) properties.get("destinationStreamScopeId"));
        }
        if (properties.containsKey("destinationStreamEntityId"))
        {
            getDestinationStream().setDestinationEntityId((Long) properties.get("destinationStreamEntityId"));
        }
        if (properties.containsKey("destinationStreamScopeType"))
        {
            ScopeType scopeType = (ScopeType) properties.get("destinationStreamScopeType");
            if (scopeType == ScopeType.PERSON)
            {
                getDestinationStream().setType(EntityType.PERSON);
            }
            else if (scopeType == ScopeType.GROUP)
            {
                getDestinationStream().setType(EntityType.GROUP);
            }
        }
        if (properties.containsKey("destinationStreamUniqueKey"))
        {
            getDestinationStream().setUniqueIdentifier((String) properties.get("destinationStreamUniqueKey"));
        }
        if (properties.containsKey("actorType"))
        {
            getActor().setType((EntityType) properties.get("actorType"));
        }
        if (properties.containsKey("actorUniqueIdentifier"))
        {
            getActor().setUniqueIdentifier((String) properties.get("actorUniqueIdentifier"));
        }
        if (properties.containsKey("originalActorType"))
        {
            getOriginalActor().setType((EntityType) properties.get("originalActorType"));
        }
        if (properties.containsKey("originalActorUniqueIdentifier"))
        {
            getOriginalActor().setUniqueIdentifier((String) properties.get("originalActorUniqueIdentifier"));
        }
        if (properties.containsKey("comments"))
        {
            setComments((List<CommentDTO>) properties.get("comments"));
        }
        if (properties.containsKey("deletable"))
        {
            setDeletable((Boolean) properties.get("deletable"));
        }
        if (properties.containsKey("isDestinationStreamPublic"))
        {
            setIsDestinationStreamPublic((Boolean) properties.get("isDestinationStreamPublic"));
        }
        if (properties.containsKey("appType"))
        {
            setAppType((EntityType) properties.get("appType"));
        }
        if (properties.containsKey("appId"))
        {
            setAppId((Long) properties.get("appId"));
        }
        if (properties.containsKey("appName"))
        {
            setAppName((String) properties.get("appName"));
        }
        if (properties.containsKey("appSource"))
        {
            setAppSource((String) properties.get("appSource"));
        }
        if (properties.containsKey("recipientParentOrgId"))
        {
            setRecipientParentOrgId((Long) properties.get("recipientParentOrgId"));
        }
        if (properties.containsKey("likeCount"))
        {
            setLikeCount((Integer) properties.get("likeCount"));
        }

    }

    /**
     * @param inShareable
     *            Set if the activity is sharable (currently only private groups are not shareable).
     */
    public void setShareable(final Boolean inShareable)
    {
        shareable = inShareable;
    }

    /**
     * @return if the item is shareable.
     */
    public Boolean isShareable()
    {
        return shareable;
    }

    /**
     * @return check to see if this activity is commentable.
     */
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @param inCommentable
     *            Set if this activity is accepting additional comments.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the verb
     */
    public ActivityVerb getVerb()
    {
        return verb;
    }

    /**
     * @param inVerb
     *            the verb to set
     */
    public void setVerb(final ActivityVerb inVerb)
    {
        verb = inVerb;
    }

    /**
     * @return the baseObjectType
     */
    public BaseObjectType getBaseObjectType()
    {
        return baseObjectType;
    }

    /**
     * @param inBaseObjectType
     *            the baseObjectType to set
     */
    public void setBaseObjectType(final BaseObjectType inBaseObjectType)
    {
        baseObjectType = inBaseObjectType;
    }

    /**
     * @return the baseObjectProperties
     */
    public HashMap<String, String> getBaseObjectProperties()
    {
        return baseObjectProperties;
    }

    /**
     * @param inBaseObjectProperties
     *            the baseObjectProperties to set
     */
    public void setBaseObjectProperties(final HashMap<String, String> inBaseObjectProperties)
    {
        baseObjectProperties = inBaseObjectProperties;
    }

    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param inLocation
     *            the location to set
     */
    public void setLocation(final String inLocation)
    {
        location = inLocation;
    }

    /**
     * @return the mood
     */
    public String getMood()
    {
        return mood;
    }

    /**
     * @param inMood
     *            the mood to set
     */
    public void setMood(final String inMood)
    {
        mood = inMood;
    }

    /**
     * @return the annotation
     */
    public String getAnnotation()
    {
        return annotation;
    }

    /**
     * @param inAnnotation
     *            the annotation to set
     */
    public void setAnnotation(final String inAnnotation)
    {
        annotation = inAnnotation;
    }

    /**
     * @return the starred
     */
    public Boolean isStarred()
    {
        return starred;
    }

    /**
     * @param inStarred
     *            the starred to set
     */
    public void setStarred(final Boolean inStarred)
    {
        starred = inStarred;
    }

    /**
     * @param inPostedTime
     *            the postedTime to set
     */
    public void setPostedTime(final Date inPostedTime)
    {
        postedTime = inPostedTime;
    }

    /**
     * @return the postedTime
     */
    public Date getPostedTime()
    {
        return postedTime;
    }

    /**
     * Gets the name of the entity backing this model view.
     *
     * @return the entity name;
     */
    @Override
    protected String getEntityName()
    {
        return "Activity";
    }

    /**
     * @return the firstComment
     */
    public CommentDTO getFirstComment()
    {
        return firstComment;
    }

    /**
     * @param inFirstComment
     *            the firstComment to set
     */
    public void setFirstComment(final CommentDTO inFirstComment)
    {
        firstComment = inFirstComment;
    }

    /**
     * @return the lastComment
     */
    public CommentDTO getLastComment()
    {
        return lastComment;
    }

    /**
     * @param inLastComment
     *            the lastComment to set
     */
    public void setLastComment(final CommentDTO inLastComment)
    {
        lastComment = inLastComment;
    }

    /**
     * @return the commentCount
     */
    public int getCommentCount()
    {
        return commentCount;
    }

    /**
     * @param inCommentCount
     *            the commentCount to set
     */
    public void setCommentCount(final int inCommentCount)
    {
        commentCount = inCommentCount;
    }

    /**
     * @return the actor
     */
    public StreamEntityDTO getActor()
    {
        return actor;
    }

    /**
     * @param inActor
     *            the actor to set
     */
    public void setActor(final StreamEntityDTO inActor)
    {
        actor = inActor;
    }

    /**
     * @return the originalActor
     */
    public StreamEntityDTO getOriginalActor()
    {
        return originalActor;
    }

    /**
     * @param inOriginalActor
     *            the originalActor to set
     */
    public void setOriginalActor(final StreamEntityDTO inOriginalActor)
    {
        originalActor = inOriginalActor;
    }

    /**
     * @return the proxyActor
     */
    public StreamEntityDTO getProxyActor()
    {
        return proxyActor;
    }

    /**
     * @param inProxyActor
     *            the proxyActor to set
     */
    public void setProxyActor(final StreamEntityDTO inProxyActor)
    {
        proxyActor = inProxyActor;
    }

    /**
     * @return the destinationStream
     */
    public StreamEntityDTO getDestinationStream()
    {
        return destinationStream;
    }

    /**
     * @param inDestinationStream
     *            the destinationStream to set
     */
    public void setDestinationStream(final StreamEntityDTO inDestinationStream)
    {
        destinationStream = inDestinationStream;
    }


    /**
     * @return the comments
     */
    public List<CommentDTO> getComments()
    {
        return comments;
    }

    /**
     * @param inComments
     *            the comments to set.
     */
    public void setComments(final List<CommentDTO> inComments)
    {
        comments = inComments;
    }

    /**
     * @return the deletable.
     */
    public Boolean isDeletable()
    {
        return deletable;
    }

    /**
     * @param inDeletable
     *            set if the activity is deleteable by the user.
     */
    public void setDeletable(final Boolean inDeletable)
    {
        deletable = inDeletable;
    }

    /**
     * Get whether the destination stream is a public.
     *
     * @return whether the destination stream is public.
     */
    public Boolean getIsDestinationStreamPublic()
    {
        return isDestinationStreamPublic;
    }

    /**
     * Set whether the destination stream is public.
     *
     * @param inIsDestinationStreamPublic
     *            whether the destination stream is public
     */
    public void setIsDestinationStreamPublic(final Boolean inIsDestinationStreamPublic)
    {
        isDestinationStreamPublic = inIsDestinationStreamPublic;
    }

    /**
     * @param inAppType
     *            the appType to set
     */
    public void setAppType(final EntityType inAppType)
    {
        appType = inAppType;
    }

    /**
     * @return the appType
     */
    public EntityType getAppType()
    {
        return appType;
    }

    /**
     * @param inAppName
     *            the appName to set
     */
    public void setAppName(final String inAppName)
    {
        appName = inAppName;
    }

    /**
     * @return the appName
     */
    public String getAppName()
    {
        return appName;
    }

    /**
     * @return the appId
     */
    public Long getAppId()
    {
        return appId;
    }

    /**
     * @param inAppId
     *            the appId to set
     */
    public void setAppId(final Long inAppId)
    {
        appId = inAppId;
    }

    /**
     * @param inAppSource
     *            the appSource to set
     */
    public void setAppSource(final String inAppSource)
    {
        appSource = inAppSource;
    }

    /**
     * @return the appSource
     */
    public String getAppSource()
    {
        return appSource;
    }

    /**
     * @return ID of organization "owning" the entity whose stream the activity is posted to.
     */
    public long getRecipientParentOrgId()
    {
        return recipientParentOrgId;
    }

    /**
     * @param inRecipientParentOrgId
     *            ID of organization "owning" the entity whose stream the activity is posted to.
     */
    public void setRecipientParentOrgId(final long inRecipientParentOrgId)
    {
        recipientParentOrgId = inRecipientParentOrgId;
    }

    /**
     * @param isLiked
     *            the activity as liked.
     */
    public void setLiked(final Boolean isLiked)
    {
        liked = isLiked;
    }

    /**
     * @return if the user liked the activity.
     */
    public Boolean isLiked()
    {
        return liked;
    }

    /**
     * @param inLikeCount
     *            set the like count.
     */
    public void setLikeCount(final Integer inLikeCount)
    {
        likeCount = inLikeCount;
    }

    /**
     * @return the number of times the activity has been liked.
     */
    public Integer getLikeCount()
    {
        return likeCount;
    }

    /**
     * Set the likers.
     *
     * @param inLikers
     *            the likers.
     */
    public void setLikers(final List<PersonModelView> inLikers)
    {
        likers = inLikers;
    }

    /**
     * Get the likers.
     *
     * @return the likers.
     */
    public List<PersonModelView> getLikers()
    {
        return likers;
    }
}
