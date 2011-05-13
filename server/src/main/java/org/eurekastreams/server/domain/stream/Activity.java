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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.HashTagTextStemmerIndexingAnalyzer;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.bridge.ActivityAuthorClassBridge;
import org.eurekastreams.server.search.bridge.ActivityContentClassBridge;
import org.eurekastreams.server.search.bridge.ActivityInterestingClassBridge;
import org.eurekastreams.server.search.bridge.ActivityLastCommentIdClassBridge;
import org.eurekastreams.server.search.bridge.ActivityLikesClassBridge;
import org.eurekastreams.server.search.bridge.ActivityRecipientClassBridge;
import org.eurekastreams.server.search.bridge.ActivitySourceClassBridge;
import org.eurekastreams.server.search.bridge.IsActivityPublicClassBridge;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.ClassBridges;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * This class represents an instance of an activity.
 */
@Entity
@Indexed
@ClassBridges(value = {
        @ClassBridge(analyzer = @Analyzer(impl = HashTagTextStemmerIndexingAnalyzer.class),
        // new line
        name = "content", store = Store.NO, impl = ActivityContentClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "recipient", store = Store.NO, impl = ActivityRecipientClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "author", store = Store.NO, impl = ActivityAuthorClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "appId", store = Store.NO, impl = ActivitySourceClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "interesting", store = Store.YES, impl = ActivityInterestingClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "likes", store = Store.YES, impl = ActivityLikesClassBridge.class),
        @ClassBridge(index = Index.UN_TOKENIZED,
        // new line
        name = "commentdate", store = Store.NO, impl = ActivityLastCommentIdClassBridge.class),
        @ClassBridge(index = Index.TOKENIZED, name = "isPublic", store = Store.NO,
        // new line
        impl = IsActivityPublicClassBridge.class) })
public class Activity extends DomainEntity implements Serializable, Cloneable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -6112350966831240411L;

    /**
     * This (random) value is search-indexed in every activity so that we can allow the user to make NOT queries.
     */
    public static final String CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT = "e5qgryyme4elshgvkkmq";

    /**
     * App type.
     */
    @Enumerated(EnumType.STRING)
    @Basic
    private EntityType appType;

    /**
     * App name.
     */
    @Basic
    private String appName;

    /**
     * App id.
     */
    @Basic
    private Long appId;

    /**
     * App source.
     */
    @Basic
    private String appSource;

    /**
     * Activity verb.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private ActivityVerb verb;

    /**
     * Actor unique id for this activity (consider this author).
     */
    @Basic(optional = false)
    private String actorId;

    /**
     * Type of actor.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private EntityType actorType;

    /**
     * Original actor unique id. This comes into play with shared activities. When user creates activity with verb
     * share, the "sharer" is the actor, but we want to capture the original actor (author) of the activity being
     * shared,
     */
    @Basic(optional = true)
    private String originalActorId;

    /**
     * Type of original actor.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    private EntityType originalActorType;

    /**
     * The Open social ID of the activity.
     */
    @Basic(optional = false)
    private String openSocialId;

    /**
     * The recipient StreamScope for this activity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streamScopeId", nullable = false)
    private StreamScope recipientStreamScope;

    /**
     * Whether the activity's destination stream is public.
     */
    @Basic(optional = false)
    private Boolean isDestinationStreamPublic;

    /**
     * The recipient's parent organization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipientParentOrgId", nullable = true)
    private Organization recipientParentOrg;

    /**
     * The updated date.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * The creation date.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Field(index = Index.UN_TOKENIZED, name = "date", store = Store.NO)
    private Date postedTime;

    /**
     * The base object key-value properties.
     */
    @Basic(optional = true)
    private HashMap<String, String> baseObject;

    /**
     * The base object type for this activity.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private BaseObjectType baseObjectType;

    /**
     * Location.
     */
    @Basic(optional = true)
    private String location;

    /**
     * Annotation.
     */
    @Basic(optional = true)
    private String annotation;

    /**
     * Mood.
     */
    @Basic(optional = true)
    private String mood;

    /**
     * Original Activity Id.
     */
    @Basic(optional = true)
    private Long originalActivityId;

    /**
     * The shared link resource, if included.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linkSharedResourceId")
    private SharedResource sharedLink;

    /**
     * Whether the activity is to be displayed in stream (everyone, personal, etc), or just resource streams.
     */
    @Basic(optional = false)
    private Boolean showInStream = true;

    /**
     * Only used for query reference, don't load this.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "StarredActivity",
    // join columns
    joinColumns = { @JoinColumn(table = "Activity", name = "activityId") },
    // inverse joincolumns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "personId") })
    private List<Person> usersWhoStarred;

    /**
     * If the activity has been flagged for inappropriate content. NOTE: UpdateActivityFlag (mapper) directly updates
     * the database, bypassing Lucene. If this field is ever to be indexed for search, UpdateActivityFlag needs to be
     * changed.
     */
    @Basic(optional = false)
    private boolean flagged;

    /**
     * sets the posted date.
     */
    @PrePersist
    protected void onCreate()
    {
        postedTime = new Date();
        updated = new Date();

        // if not set externally give it one.
        if (openSocialId == null)
        {
            openSocialId = UUID.randomUUID().toString();
        }
    }

    /**
     * sets the updated date.
     */
    @PreUpdate
    protected void onUpdate()
    {
        updated = new Date();
    }

    /**
     * The open social id of the activity.
     * 
     * @param inOpenSocialId
     *            the activity open social id to set
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        this.openSocialId = inOpenSocialId;
    }

    /**
     * Get the open social id of the activity.
     * 
     * @return the open social id of the activity
     */
    public String getOpenSocialId()
    {
        return this.openSocialId;
    }

    /**
     * The date updated setter.
     * 
     * @param inUpdated
     *            the date updated to set
     */
    public void setUpdated(final Date inUpdated)
    {
        this.updated = inUpdated;
    }

    /**
     * Get the date updated.
     * 
     * @return the date updated
     */
    public Date getUpdated()
    {
        return this.updated;
    }

    /**
     * The time posted setter.
     * 
     * @param inPostedTime
     *            the time posted to set
     */
    public void setPostedTime(final Date inPostedTime)
    {
        this.postedTime = inPostedTime;
    }

    /**
     * Get the time posted.
     * 
     * @return the time posted
     */
    public Date getPostedTime()
    {
        return this.postedTime;
    }

    /**
     * The base object setter.
     * 
     * @param inBaseObject
     *            the template params to set
     */
    public void setBaseObject(final HashMap<String, String> inBaseObject)
    {
        this.baseObject = inBaseObject;
    }

    /**
     * The base object getter.
     * 
     * @return base object hashmap.
     */
    public HashMap<String, String> getBaseObject()
    {
        return this.baseObject;
    }

    /**
     * @param inBaseObjectType
     *            the baseObjectType to set
     */
    public void setBaseObjectType(final BaseObjectType inBaseObjectType)
    {
        this.baseObjectType = inBaseObjectType;
    }

    /**
     * @return the baseObjectType
     */
    public BaseObjectType getBaseObjectType()
    {
        return baseObjectType;
    }

    /**
     * @return the actorId
     */
    public String getActorId()
    {
        return actorId;
    }

    /**
     * @param inActorId
     *            the actorId to set
     */
    public void setActorId(final String inActorId)
    {
        this.actorId = inActorId;
    }

    /**
     * @return the actorType
     */
    public EntityType getActorType()
    {
        return actorType;
    }

    /**
     * @param inActorType
     *            the actorType to set
     */
    public void setActorType(final EntityType inActorType)
    {
        this.actorType = inActorType;
    }

    /**
     * @return the originalActorId
     */
    public String getOriginalActorId()
    {
        return originalActorId;
    }

    /**
     * @param inOriginalActorId
     *            the originalActorId to set
     */
    public void setOriginalActorId(final String inOriginalActorId)
    {
        this.originalActorId = inOriginalActorId;
    }

    /**
     * @return the originalActorType
     */
    public EntityType getOriginalActorType()
    {
        return originalActorType;
    }

    /**
     * @param inOriginalActorType
     *            the originalActorType to set
     */
    public void setOriginalActorType(final EntityType inOriginalActorType)
    {
        this.originalActorType = inOriginalActorType;
    }

    /**
     * @return the recipientStreamScope
     */
    public StreamScope getRecipientStreamScope()
    {
        return recipientStreamScope;
    }

    /**
     * @param inRecipientStreamScope
     *            the recipientStreamScope to set
     */
    public void setRecipientStreamScope(final StreamScope inRecipientStreamScope)
    {
        this.recipientStreamScope = inRecipientStreamScope;
    }

    /**
     * @return the recipientParentOrg
     */
    public Organization getRecipientParentOrg()
    {
        return recipientParentOrg;
    }

    /**
     * @param inRecipientParentOrg
     *            the recipientParentOrg to set
     */
    public void setRecipientParentOrg(final Organization inRecipientParentOrg)
    {
        this.recipientParentOrg = inRecipientParentOrg;
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
        this.verb = inVerb;
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
        this.location = inLocation;
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
        this.annotation = inAnnotation;
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
        this.mood = inMood;
    }

    /**
     * @return the originalActivityId.
     */
    public long getOriginalActivityId()
    {
        return originalActivityId;
    }

    /**
     * @param inOriginalActivityId
     *            - the original activity id to set.
     */
    public void setOriginalActivityId(final long inOriginalActivityId)
    {
        this.originalActivityId = inOriginalActivityId;
    }

    /**
     * Get whether the activity's destination stream is public.
     * 
     * @return whether the activity's destination stream is public
     */
    public Boolean getIsDestinationStreamPublic()
    {
        return isDestinationStreamPublic;
    }

    /**
     * Set whether the activity's destination stream is public.
     * 
     * @param inIsDestinationStreamPublic
     *            whether the activity's destination stream is public
     */
    public void setIsDestinationStreamPublic(final Boolean inIsDestinationStreamPublic)
    {
        this.isDestinationStreamPublic = inIsDestinationStreamPublic;
    }

    /**
     * @return the flagged status.
     */
    public boolean isFlagged()
    {
        return flagged;
    }

    /**
     * @param inFlagged
     *            the flagged status to set.
     */
    public void setFlagged(final boolean inFlagged)
    {
        flagged = inFlagged;
    }

    /**
     * Clone support for creating bulk activities from this one.
     * 
     * @return the cloned object.
     */
    @Override
    public Object clone()
    {
        Activity clone = new Activity();
        clone.annotation = this.annotation;
        clone.baseObject = this.baseObject;
        clone.baseObjectType = this.baseObjectType;
        clone.isDestinationStreamPublic = this.isDestinationStreamPublic;
        clone.actorId = this.actorId;
        clone.actorType = this.actorType;
        clone.location = this.location;
        clone.mood = this.mood;
        clone.openSocialId = this.openSocialId;
        clone.originalActivityId = this.originalActivityId;
        clone.originalActorId = this.originalActorId;
        clone.originalActorType = this.originalActorType;
        clone.postedTime = this.postedTime;
        clone.recipientParentOrg = this.recipientParentOrg;
        clone.recipientStreamScope = this.recipientStreamScope;
        clone.updated = this.updated;
        clone.usersWhoStarred = this.usersWhoStarred;
        clone.verb = this.verb;
        clone.appId = this.appId;
        clone.appName = this.appName;
        clone.appSource = this.appSource;
        clone.appType = this.appType;
        return clone;
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
     * @param inAppId
     *            the appId to set
     */
    public void setAppId(final Long inAppId)
    {
        appId = inAppId;
    }

    /**
     * @return the appId
     */
    public Long getAppId()
    {
        return appId;
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
     * @return the sharedLink
     */
    public SharedResource getSharedLink()
    {
        return sharedLink;
    }

    /**
     * @param inSharedLink
     *            the sharedLink to set
     */
    public void setSharedLink(final SharedResource inSharedLink)
    {
        sharedLink = inSharedLink;
    }

    /**
     * @return the showInStream
     */
    public Boolean getShowInStream()
    {
        return showInStream;
    }

    /**
     * @param inShowInStream
     *            the showInStream to set
     */
    public void setShowInStream(final Boolean inShowInStream)
    {
        showInStream = inShowInStream;
    }
}
