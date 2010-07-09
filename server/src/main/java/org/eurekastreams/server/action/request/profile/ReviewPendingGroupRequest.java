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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

/**
 * Request for ReviewGroups.
 */
public class ReviewPendingGroupRequest implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = -4992480608793270011L;

    /**
     * The short name of the group to review.
     */
    private String groupShortName;

    /**
     * Whether to mark the group as approved.
     */
    private Boolean approved;

    /**
     * Empty constructor.
     */
    public ReviewPendingGroupRequest()
    {
    }

    /**
     * Request object for ReviewGroup.
     *
     * @param inGroupShortName
     *            The short name of the group to review.
     * @param inApproved
     *            Whether to mark the group as approved.
     */
    public ReviewPendingGroupRequest(final String inGroupShortName, final Boolean inApproved)
    {
        groupShortName = inGroupShortName;
        approved = inApproved;
    }

    /**
     * Get the short name of the group to review.
     *
     * @return the short name of the group to review.
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * Set the short name of the group to review.
     *
     * @param inGroupShortName
     *            the short name of the group to review.
     */
    public void setGroupShortName(final String inGroupShortName)
    {
        groupShortName = inGroupShortName;
    }

    /**
     * Get whether to mark the group as approved.
     *
     * @return whether to mark the group as approved.
     */
    public Boolean getApproved()
    {
        return approved;
    }

    /**
     * Set whether to mark the group as approved.
     *
     * @param inApproved
     *            whether to mark the group as approved.
     */
    public void setApproved(final Boolean inApproved)
    {
        approved = inApproved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return super.toString() + " group short name = " + groupShortName + ", approved = " + approved;
    }
}
