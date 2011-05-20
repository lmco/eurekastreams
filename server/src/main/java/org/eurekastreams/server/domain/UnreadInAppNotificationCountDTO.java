/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

/**
 * Holds the counts of unread in-app notifications.
 */
public class UnreadInAppNotificationCountDTO implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = -2683548270794164340L;

    /** Number of high priority alerts. */
    private int highPriority;

    /** Number of normal alerts. */
    private int normalPriority;

    /**
     * Constructor for serialization.
     */
    private UnreadInAppNotificationCountDTO()
    {
    }

    /**
     * Constructor.
     * 
     * @param inHighPriority
     *            Number of high priority alerts.
     * @param inNormalPriority
     *            Number of normal alerts.
     */
    public UnreadInAppNotificationCountDTO(final int inHighPriority, final int inNormalPriority)
    {
        highPriority = inHighPriority;
        normalPriority = inNormalPriority;
    }

    /**
     * @return the highPriority
     */
    public int getHighPriority()
    {
        return highPriority;
    }

    /**
     * @param inHighPriority
     *            the highPriority to set
     */
    public void setHighPriority(final int inHighPriority)
    {
        highPriority = inHighPriority;
    }

    /**
     * @return the normalPriority
     */
    public int getNormalPriority()
    {
        return normalPriority;
    }

    /**
     * @param inNormalPriority
     *            the normalPriority to set
     */
    public void setNormalPriority(final int inNormalPriority)
    {
        normalPriority = inNormalPriority;
    }
}
