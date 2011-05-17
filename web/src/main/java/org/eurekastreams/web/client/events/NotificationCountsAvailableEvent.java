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
package org.eurekastreams.web.client.events;

/**
 * Event that the unread in-app notification counts are available.
 */
public class NotificationCountsAvailableEvent
{
    /** Count of unread normal priority in-app notifications. */
    private final int normalCount;

    /** Count of unread high priority in-app notifications. */
    private final int highPriorityCount;

    /**
     * Constructor.
     *
     * @param inNormalCount
     *            Count of unread normal priority in-app notifications.
     * @param inHighPriorityCount
     *            Count of unread high priority in-app notifications.
     */
    public NotificationCountsAvailableEvent(final int inNormalCount, final int inHighPriorityCount)
    {
        normalCount = inNormalCount;
        highPriorityCount = inHighPriorityCount;
    }

    /**
     * @return the normalCount
     */
    public int getNormalCount()
    {
        return normalCount;
    }

    /**
     * @return the highPriorityCount
     */
    public int getHighPriorityCount()
    {
        return highPriorityCount;
    }
}
