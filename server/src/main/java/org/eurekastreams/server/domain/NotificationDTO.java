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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * Contains the information needed to send a user a notification about an event of interest.
 */
public class NotificationDTO implements Serializable
{
    /*
     * The notification contains a couple basic fields (type, recipients) and four items: actor, activity, destination,
     * and auxiliary entity. The four items are not used by all notification types.
     *
     * The following lists the notification types with the items they use and what they use them for:
     */
    /*
     * POST_TO_PERSONAL_STREAM actor=person who posted activity=the post dest=entity owning stream aux=N/A
     */
    /*
     * COMMENT_TO_PERSONAL_STREAM actor=person who commented activity=post commented on dest=entity owning stream
     * aux=N/A
     */
    /*
     * COMMENT_TO_PERSONAL_POST actor=person who commented activity=post commented on dest=entity owning stream aux=N/A
     */
    /*
     * COMMENT_TO_COMMENTED_POST actor=person who commented activity=post commented on dest=entity owning stream
     * aux=author of post
     */
    /*
     * FOLLOW_PERSON actor=person doing following activity=N/A dest=person being followed aux=N/A
     */
    /*
     * POST_TO_GROUP_STREAM actor=person who posted activity=the post dest=entity owning stream aux=N/A
     */
    /*
     * COMMENT_TO_GROUP_STREAM actor=person who commented activity=post commented on dest=entity owning stream aux=N/A
     */
    /*
     * FOLLOW_GROUP actor=person doing following activity=N/A dest=group being followed aux=N/A
     */
    /*
     * FLAG_PERSONAL_ACTIVITY actor=person flagging post activity=flagged post dest=organization containing the person
     * in whose stream the post is aux=N/A Note: The person whose stream contains the flagged post is NOT part of the
     * notification.
     */
    /*
     * FLAG_GROUP_ACTIVITY actor=person flagging post activity=flagged post dest=organization containing the group in
     * whose stream the post is aux=N/A Note: The group whose stream contains the flagged post is NOT part of the
     * notification.
     */
    /*
     * REQUEST_NEW_GROUP actor=person requesting the group activity=N/A dest=organization containing the group aux=the
     * requested group
     */
    /*
     * REQUEST_NEW_GROUP_APPROVED,REQUEST_NEW_GROUP_DENIED actor=(none) activity=N/A dest=(none) aux=the requested group
     * Note: Currently the actor and dest are not used, but the actor could be used for the org coordinator who
     * approved/denied the request, and the dest could be used for the parent org.
     */
    /*
     * REQUEST_GROUP_ACCESS actor=person requesting access, activity=N/A, dest=group, aux=N/A
     */

    /** Fingerprint. */
    private static final long serialVersionUID = -7265395450651936745L;

    /** ID of the person to receive the notification. */
    private List<Long> recipientIds;

    /** Type of notification (event that occurred). */
    private NotificationType type;

    /* -- actor -- */
    /* Future note: If the actor could ever be something other than a person, then add an actorType field. */

    /** ID of the entity (person) who performed the action which the notification is about. */
    private long actorId;

    /** Account ID of the acting entity. */
    private String actorAccountId;

    /** Display name of the acting entity. */
    private String actorName;

    /* -- activity -- */

    /** ID of the activity the event pertained to. */
    private long activityId;

    /** Type of the activity the event pertained to. */
    private BaseObjectType activityType;

    /* -- destination -- */

    /** ID of the entity (person, group, org) upon whom or upon whose stream that the action acted. */
    private long destinationId;

    /** Type (person, group, org) of the destination. */
    private EntityType destinationType;

    /** Unique ID (account id / shortname) of the destination of the action. */
    private String destinationUniqueId;

    /** Name of the destination of the action. */
    private String destinationName;

    /* -- auxiliary entity -- */

    /** Type of the auxiliary entity. */
    private EntityType auxiliaryType;

    /** Unique ID (account id / shortname) of the auxiliary entity. */
    private String auxiliaryUniqueId;

    /** Name of the auxiliary entity. */
    private String auxiliaryName;

    /**
     * Constructor (for GWT serialization).
     */
    public NotificationDTO()
    {
    }

    /**
     * @param inRecipientIds
     *            IDs of the people to receive the notification.
     * @param inType
     *            Type of notification (event that occurred).
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     */
    public NotificationDTO(final List<Long> inRecipientIds, final NotificationType inType, final long inActorId)
    {
        recipientIds = inRecipientIds;
        type = inType;
        actorId = inActorId;
    }

    /**
     * @param inRecipientIds
     *            IDs of the people to receive the notification.
     * @param inType
     *            Type of notification (event that occurred).
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inDestinationId
     *            ID of the entity (person or group) upon whom or upon whose stream that the action acted.
     * @param inDestinationType
     *            Type of the destination (person or group).
     * @param inActivityId
     *            ID of the activity the event pertained to.
     */
    public NotificationDTO(final List<Long> inRecipientIds, final NotificationType inType, final long inActorId,
            final long inDestinationId, final EntityType inDestinationType, final long inActivityId)
    {
        recipientIds = inRecipientIds;
        type = inType;
        actorId = inActorId;
        destinationId = inDestinationId;
        destinationType = inDestinationType;
        activityId = inActivityId;
    }

    /**
     * @return IDs of the people to receive the notification.
     */
    public List<Long> getRecipientIds()
    {
        return recipientIds;
    }

    /**
     * @param inRecipientIds
     *            IDs of the people to receive the notification.
     */
    public void setRecipientIds(final List<Long> inRecipientIds)
    {
        recipientIds = inRecipientIds;
    }

    /**
     * @return Type of notification (event that occurred).
     */
    public NotificationType getType()
    {
        return type;
    }

    /**
     * @param inType
     *            Type of notification (event that occurred).
     */
    public void setType(final NotificationType inType)
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
     * @return ID of the entity (person or group) upon whom or upon whose stream that the action acted.
     */
    public long getDestinationId()
    {
        return destinationId;
    }

    /**
     * @param inDestinationId
     *            ID of the entity (person or group) upon whom or upon whose stream that the action acted.
     */
    public void setDestinationId(final long inDestinationId)
    {
        destinationId = inDestinationId;
    }

    /**
     * @return Type of the entity (person or group) upon whom or upon whose stream that the action acted.
     */
    public EntityType getDestinationType()
    {
        return destinationType;
    }

    /**
     * @param inDestinationType
     *            Type of the entity (person or group) upon whom or upon whose stream that the action acted.
     */
    public void setDestinationType(final EntityType inDestinationType)
    {
        destinationType = inDestinationType;
    }

    /**
     * @return ID of the activity the event pertained to.
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

    /**
     * @return the actorAccountId
     */
    public String getActorAccountId()
    {
        return actorAccountId;
    }

    /**
     * @param inActorAccountId
     *            the actorAccountId to set
     */
    public void setActorAccountId(final String inActorAccountId)
    {
        actorAccountId = inActorAccountId;
    }

    /**
     * @return the actorName
     */
    public String getActorName()
    {
        return actorName;
    }

    /**
     * @param inActorName
     *            the actorName to set
     */
    public void setActorName(final String inActorName)
    {
        actorName = inActorName;
    }

    /**
     * @return the activityType
     */
    public BaseObjectType getActivityType()
    {
        return activityType;
    }

    /**
     * @param inActivityType
     *            the activityType to set
     */
    public void setActivityType(final BaseObjectType inActivityType)
    {
        activityType = inActivityType;
    }

    /**
     * @return the destinationUniqueId
     */
    public String getDestinationUniqueId()
    {
        return destinationUniqueId;
    }

    /**
     * @param inDestinationUniqueId
     *            the destinationUniqueId to set
     */
    public void setDestinationUniqueId(final String inDestinationUniqueId)
    {
        destinationUniqueId = inDestinationUniqueId;
    }

    /**
     * @return the destinationName
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * @param inDestinationName
     *            the destinationName to set
     */
    public void setDestinationName(final String inDestinationName)
    {
        destinationName = inDestinationName;
    }

    /**
     * @return the auxiliaryType
     */
    public EntityType getAuxiliaryType()
    {
        return auxiliaryType;
    }

    /**
     * @param inAuxiliaryType
     *            the auxiliaryType to set
     */
    public void setAuxiliaryType(final EntityType inAuxiliaryType)
    {
        auxiliaryType = inAuxiliaryType;
    }

    /**
     * @return the auxiliaryUniqueId
     */
    public String getAuxiliaryUniqueId()
    {
        return auxiliaryUniqueId;
    }

    /**
     * @param inAuxiliaryUniqueId
     *            the auxiliaryUniqueId to set
     */
    public void setAuxiliaryUniqueId(final String inAuxiliaryUniqueId)
    {
        auxiliaryUniqueId = inAuxiliaryUniqueId;
    }

    /**
     * @return the auxiliaryName
     */
    public String getAuxiliaryName()
    {
        return auxiliaryName;
    }

    /**
     * @param inAuxiliaryName
     *            the auxiliaryName to set
     */
    public void setAuxiliaryName(final String inAuxiliaryName)
    {
        auxiliaryName = inAuxiliaryName;
    }

    /* ---- Bulk setters: set a group of related properties at once. ---- */

    /**
     * Sets the activity.
     *
     * @param inId
     *            Activity id.
     * @param inType
     *            Activity type.
     */
    public void setActivity(final long inId, final BaseObjectType inType)
    {
        activityId = inId;
        activityType = inType;
    }

    /**
     * Sets the destination.
     *
     * @param inId
     *            Entity id.
     * @param inType
     *            Entity type.
     */
    public void setDestination(final long inId, final EntityType inType)
    {
        destinationId = inId;
        destinationType = inType;
    }

    /**
     * Sets the destination.
     *
     * @param inId
     *            Entity id.
     * @param inType
     *            Entity type.
     * @param inUniqueId
     *            Entity's unique id (account id, shortname).
     * @param inName
     *            Display name.
     */
    public void setDestination(final long inId, final EntityType inType, final String inUniqueId, final String inName)
    {
        destinationId = inId;
        destinationType = inType;
        destinationUniqueId = inUniqueId;
        destinationName = inName;
    }

    /**
     * Sets the auxiliary entity.
     *
     * @param inType
     *            Entity type.
     * @param inUniqueId
     *            Entity's unique id (account id, shortname).
     * @param inName
     *            Display name.
     */
    public void setAuxiliary(final EntityType inType, final String inUniqueId, final String inName)
    {
        auxiliaryType = inType;
        auxiliaryUniqueId = inUniqueId;
        auxiliaryName = inName;
    }

}
