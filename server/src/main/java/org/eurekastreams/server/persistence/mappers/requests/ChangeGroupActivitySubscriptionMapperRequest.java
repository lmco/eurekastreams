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

package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Mapper request to change a group activity subscription preference for a user's followed group.
 */
public class ChangeGroupActivitySubscriptionMapperRequest
{
    /**
     * The id of the person.
     */
    private long personId;

    /**
     * The id of the group.
     */
    private long groupId;

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            the person id
     * @param inGroupId
     *            the group id
     * @param inReceiveNewActivityNotifications
     *            whether to receive notifications
     */
    public ChangeGroupActivitySubscriptionMapperRequest(final long inPersonId, final long inGroupId,
            final boolean inReceiveNewActivityNotifications)
    {
        super();
        personId = inPersonId;
        groupId = inGroupId;
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
    }

    /**
     * Whether the user should receive notifications on new activity to this group.
     */
    private boolean receiveNewActivityNotifications;

    /**
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * @return the groupId
     */
    public long getGroupId()
    {
        return groupId;
    }

    /**
     * @param inGroupId
     *            the groupId to set
     */
    public void setGroupId(final long inGroupId)
    {
        groupId = inGroupId;
    }

    /**
     * @return the receiveNewActivityNotifications
     */
    public boolean getReceiveNewActivityNotifications()
    {
        return receiveNewActivityNotifications;
    }

    /**
     * @param inReceiveNewActivityNotifications
     *            the receiveNewActivityNotifications to set
     */
    public void setReceiveNewActivityNotifications(final boolean inReceiveNewActivityNotifications)
    {
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
    }

}
