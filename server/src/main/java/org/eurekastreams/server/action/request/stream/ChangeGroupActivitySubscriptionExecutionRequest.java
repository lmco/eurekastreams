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

package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

/**
 * Request for ChangeGroupActivitySubscriptionExecution.
 */
public class ChangeGroupActivitySubscriptionExecutionRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -3057266450773584546L;

    /**
     * The short name of the group.
     */
    private String groupShortName;

    /**
     * Whether the user wants to receive new activity notifications.
     */
    private Boolean receiveNewActivityNotifications;

    /**
     * Constructor.
     * 
     * @param inGroupShortName
     *            The short name of the group.
     * @param inReceiveNewActivityNotifications
     *            Whether the user wants to receive new activity notifications.
     */
    public ChangeGroupActivitySubscriptionExecutionRequest(final String inGroupShortName,
            final Boolean inReceiveNewActivityNotifications)
    {
        super();
        groupShortName = inGroupShortName;
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
    }

    /**
     * @return the groupShortName
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * @param inGroupShortName
     *            the groupShortName to set
     */
    public void setGroupShortName(final String inGroupShortName)
    {
        groupShortName = inGroupShortName;
    }

    /**
     * @return the receiveNewActivityNotifications
     */
    public Boolean getReceiveNewActivityNotifications()
    {
        return receiveNewActivityNotifications;
    }

    /**
     * @param inReceiveNewActivityNotifications
     *            the receiveNewActivityNotifications to set
     */
    public void setReceiveNewActivityNotifications(final Boolean inReceiveNewActivityNotifications)
    {
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
    }

}
