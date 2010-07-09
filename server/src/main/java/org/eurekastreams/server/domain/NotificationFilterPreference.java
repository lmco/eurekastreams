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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents a kind of notification that a user wishes not to see via a given transport.
 */
@Entity
public class NotificationFilterPreference extends DomainEntity
{
    /** Version. */
    private static final long serialVersionUID = 7192155314637780614L;

    /**
     * Category of nofications to suppress.
     */
    public enum Category
    {
        /** Someone posted to the user's stream. */
        POST_TO_PERSONAL_STREAM,

        /** Comment to activity in the user's stream, that the user posted, or that the user commented on. */
        COMMENT,

        /** Someone started following the user. */
        FOLLOW_PERSON,

        /** Someone posted to a group stream for which the user is a coordinator. . */
        POST_TO_GROUP_STREAM,

        /** Someone commented on an activity in a group stream for which the user is a coordinator. */
        COMMENT_IN_GROUP_STREAM,

        /** Someone started following a group of which the user is a coordinator. */
        FOLLOW_GROUP,

        /** Someone flagged an activity. */
        FLAG_ACTIVITY,

        /** Pending group creation request. */
        REQUEST_NEW_GROUP,

        /** Request access to a private group. */
        REQUEST_GROUP_ACCESS
    }

    /** Person whose preference it is. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /** Which notification method should be suppressed for the notification. */
    @Basic(optional = false)
    private String notifierType;

    /** Category of notifications to suppress. */
    @Enumerated(EnumType.STRING)
    private Category notificationCategory;

    /**
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * @param inPerson
     *            the person to set
     */
    public void setPerson(final Person inPerson)
    {
        person = inPerson;
    }

    /**
     * @return the notifierType
     */
    public String getNotifierType()
    {
        return notifierType;
    }

    /**
     * @param inNotifierType
     *            the notifierType to set
     */
    public void setNotifierType(final String inNotifierType)
    {
        notifierType = inNotifierType;
    }

    /**
     * @return the notificationCategory
     */
    public Category getNotificationCategory()
    {
        return notificationCategory;
    }

    /**
     * @param inNotificationCategory
     *            the notificationCategory to set
     */
    public void setNotificationCategory(final Category inNotificationCategory)
    {
        notificationCategory = inNotificationCategory;
    }



}
