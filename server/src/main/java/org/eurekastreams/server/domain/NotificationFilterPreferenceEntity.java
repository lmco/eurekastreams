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
package org.eurekastreams.server.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents a kind of notification that a user wishes not to see via a given transport.
 */
@Entity(name = "NotificationFilterPreference")
public class NotificationFilterPreferenceEntity extends DomainEntity
{
    /** Version. */
    private static final long serialVersionUID = 7192155314637780614L;

    /** Person whose preference it is. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /** Which notification method should be suppressed for the notification. */
    @Basic(optional = false)
    private String notifierType;

    /** Category of notifications to suppress. */
    @Basic(optional = false)
    private String notificationCategory;

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
    public String getNotificationCategory()
    {
        return notificationCategory;
    }

    /**
     * @param inNotificationCategory
     *            the notificationCategory to set
     */
    public void setNotificationCategory(final String inNotificationCategory)
    {
        notificationCategory = inNotificationCategory;
    }
}
