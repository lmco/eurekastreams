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
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * A notification for display within the application on the alert pull-down.
 */
@Entity
public class ApplicationAlertNotification extends DomainEntity implements Serializable
{
    /**
     * Version id.
     */
    private static final long serialVersionUID = 3541861784436836240L;

    /**
     * Person to receive the notification.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "recipientId")
    private Person recipient = new Person();

    /**
     * Type of notification being sent.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private NotificationType notificationType;

    /**
     * The date the notification was added.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date notificationDate = new Date();

    /* -- actor -- */

    /**
     * The actor/creator name of the event that caused this notification to be sent.
     */
    @Basic(optional = true)
    private String actorName;

    /**
     * The actor/creator accountId of the event that caused this notification to be sent.
     */
    @Basic(optional = true)
    private String actorAccountId;

    /* -- activity -- */

    /**
     * Id of the activity that was acted upon resulting in this notification.
     */
    @Basic(optional = true)
    private Long activityId;

    /**
     * Type of the activity.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    private BaseObjectType activityType;

    /* -- destination -- */

    /**
     * Name of the destination of the action (if applicable).
     */
    @Basic(optional = true)
    private String destinationName;

    /**
     * Short name of the destination of the action (if applicable).
     */
    @Basic(optional = true)
    private String destinationUniqueId;

    /**
     * Type of entity of the destination of the action (if applicable).
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    private EntityType destinationType;

    /* -- auxiliary entity -- */

    /**
     * Name of the auxiliary entity (if applicable).
     */
    @Basic(optional = true)
    private String auxiliaryName;

    /**
     * Short name of the auxiliary entity (if applicable).
     */
    @Basic(optional = true)
    private String auxiliaryUniqueId;

    /**
     * Type of entity of the auxiliary entity (if applicable).
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    private EntityType auxiliaryType;

    /**
     * Flag indicating if the user has marked this alert as read.
     */
    @Basic(optional = false)
    private boolean isRead = false;

    /**
     * Constructor.
     */
    public ApplicationAlertNotification()
    {
    }

    /**
     * Constructor.
     * 
     * @param dto
     *            Notification DTO from which to create the alert.
     * @param inRecipient
     *            Recipient of the notification.
     */
    public ApplicationAlertNotification(final NotificationDTO dto, final Person inRecipient)
    {
        // can't pull the recipient from the DTO, since the DTO has a list
        recipient = inRecipient;

        notificationType = dto.getType();
        actorName = dto.getActorName();
        actorAccountId = dto.getActorAccountId();
        activityId = dto.getActivityId();
        activityType = dto.getActivityType();
        destinationName = dto.getDestinationName();
        destinationUniqueId = dto.getDestinationUniqueId();
        destinationType = dto.getDestinationType();
        auxiliaryName = dto.getAuxiliaryName();
        auxiliaryUniqueId = dto.getAuxiliaryUniqueId();
        auxiliaryType = dto.getAuxiliaryType();
    }

    /**
     * @return Person to receive the notification.
     */
    public Person getRecipient()
    {
        return recipient;
    }

    /**
     * @param inRecipient
     *            Person to receive the notification.
     */
    public void setRecipient(final Person inRecipient)
    {
        recipient = inRecipient;
    }

    /**
     * @return The actor name.
     */
    public String getActorName()
    {
        return actorName;
    }

    /**
     * @param inActorName
     *            The name of the actor.
     */
    public void setActorName(final String inActorName)
    {
        actorName = inActorName;
    }

    /**
     * @return The actor accountId.
     */
    public String getActorAccountId()
    {
        return actorAccountId;
    }

    /**
     * @param inActorAccountId
     *            The accountId of the actor.
     */
    public void setActorAccountId(final String inActorAccountId)
    {
        actorAccountId = inActorAccountId;
    }

    /**
     * @return the notificiationType
     */
    public NotificationType getNotificiationType()
    {
        return notificationType;
    }

    /**
     * @param inNotificationType
     *            the notificationType to set
     */
    public void setNotificiationType(final NotificationType inNotificationType)
    {
        notificationType = inNotificationType;
    }

    /**
     * @return the activityId
     */
    public Long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId
     *            the activityId to set
     */
    public void setActivityId(final Long inActivityId)
    {
        activityId = inActivityId;
    }

    /**
     * @return the aux entity name.
     */
    public String getAuxiliaryName()
    {
        return auxiliaryName;
    }

    /**
     * @param inAuxiliaryName
     *            the aux entity name.
     */
    public void setAuxiliaryName(final String inAuxiliaryName)
    {
        auxiliaryName = inAuxiliaryName;
    }

    /**
     * @param inNotificationDate
     *            the notificationDate to set
     */
    public void setNotificationDate(final Date inNotificationDate)
    {
        notificationDate = inNotificationDate;
    }

    /**
     * @return the notificationDate
     */
    public Date getNotificationDate()
    {
        return notificationDate;
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
     * @return the activityType
     */
    public BaseObjectType getActivityType()
    {
        return activityType;
    }

    /**
     * @param inIsRead
     *            the isRead to set
     */
    public void setRead(final boolean inIsRead)
    {
        isRead = inIsRead;
    }

    /**
     * @return the isRead
     */
    public boolean isRead()
    {
        return isRead;
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
     * @return the destinationName
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * @param inDestinationUniqueId
     *            the destination unique id
     */
    public void setDestinationUniqueId(final String inDestinationUniqueId)
    {
        destinationUniqueId = inDestinationUniqueId;
    }

    /**
     * @return the destination unique id
     */
    public String getDestinationUniqueId()
    {
        return destinationUniqueId;
    }

    /**
     * @return Type of entity of the destination of the activity.
     */
    public EntityType getDestinationType()
    {
        return destinationType;
    }

    /**
     * @param inDestinationType
     *            Type of entity of the destination of the activity.
     */
    public void setDestinationType(final EntityType inDestinationType)
    {
        destinationType = inDestinationType;
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
}
