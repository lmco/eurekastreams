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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.InAppNotificationDTO;

import com.google.gwt.user.client.ui.Label;

/**
 * Identifies a source of notifications.
 */
class Source
{
    /**
     * Filter for displaying notifications.
     */
    public interface Filter
    {
        /**
         * Determines if a notification should be displayed.
         *
         * @param item
         *            Notification.
         * @return If notification should be displayed.
         */
        boolean shouldDisplay(InAppNotificationDTO item);
    }

    /** The type of the source. */
    private final EntityType entityType;

    /** The string-based unique identifier of the source. */
    private final String uniqueId;

    /** The name to display for the source. */
    private String displayName;

    /** Total count. */
    private int totalCount;

    /** Unread count. */
    private int unreadCount;

    /** Parent "source" for cascading count decrements. */
    private final Source parent;

    /** Widget to display the source. */
    private Label widget;

    /** Filter to use with this source. */
    private Filter filter;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            The type of the source.
     * @param inUniqueId
     *            The string-based unique identifier of the source.
     * @param inDisplayName
     *            The name to display for the source.
     * @param inParent
     *            Parent "source".
     * @param inFilter
     *            Filter to use with this source.
     */
    public Source(final EntityType inEntityType, final String inUniqueId, final String inDisplayName,
            final Source inParent, final Filter inFilter)
    {
        entityType = inEntityType;
        uniqueId = inUniqueId;
        displayName = inDisplayName;
        parent = inParent;
        filter = inFilter;
    }

    /**
     * Builds a formatted string for displaying the notification source.
     *
     * @return Display string.
     */
    public String getDisplayString()
    {
        return unreadCount > 0 ? displayName + " (" + unreadCount + ")" : displayName;
    }

    /**
     * increment Unread Count.
     */
    public void incrementUnreadCount()
    {
        unreadCount++;
    }

    /**
     * decrement Unread Count.
     */
    public void decrementUnreadCount()
    {
        unreadCount--;
    }

    /**
     * increment total Count.
     */
    public void incrementTotalCount()
    {
        totalCount++;
    }

    /**
     * decrement total Count.
     */
    public void decrementTotalCount()
    {
        totalCount--;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId()
    {
        return uniqueId;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @return the total Count
     */
    public int getTotalCount()
    {
        return totalCount;
    }

    /**
     * @param inCount
     *            the total Count to set
     */
    public void setTotalCount(final int inCount)
    {
        totalCount = inCount;
    }

    /**
     * @return the unreadCount
     */
    public int getUnreadCount()
    {
        return unreadCount;
    }

    /**
     * @param inUnreadCount
     *            the unreadCount to set
     */
    public void setUnreadCount(final int inUnreadCount)
    {
        unreadCount = inUnreadCount;
    }

    /**
     * @return the parent
     */
    public Source getParent()
    {
        return parent;
    }

    /**
     * @return the widget
     */
    public Label getWidget()
    {
        return widget;
    }

    /**
     * @param inWidget
     *            the widget to set
     */
    public void setWidget(final Label inWidget)
    {
        widget = inWidget;
    }

    /**
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }

    /**
     * @return the filter
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     * @param inFilter
     *            the filter to set
     */
    public void setFilter(final Filter inFilter)
    {
        filter = inFilter;
    }

    /**
     * @return If this source represents a general category instead of a specific source.
     */
    public boolean isCategorySource()
    {
        return entityType == null;
    }
}