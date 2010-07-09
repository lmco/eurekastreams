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

import org.eurekastreams.server.action.request.PageableRequest;

/**
 * Request for GetPendingGroups.
 */
public class GetPendingGroupsRequest implements Serializable, PageableRequest
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -7937312321476475458L;

    /**
     * The organization short name.
     */
    private String organizationShortName;

    /**
     * The start index for the pending groups in the organization.
     */
    private Integer startIndex;

    /**
     * The end index for the pending groups in the organization.
     */
    private Integer endIndex;

    /**
     * Empty constructor for serialization.
     */
    public GetPendingGroupsRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inOrganizationShortName
     *            the short name to get pending groups
     * @param inStartIndex
     *            the starting index of the pending groups from the org
     * @param inEndIndex
     *            the ending index of the pending groups from the org
     */
    public GetPendingGroupsRequest(final String inOrganizationShortName, final Integer inStartIndex,
            final Integer inEndIndex)
    {
        super();
        organizationShortName = inOrganizationShortName;
        startIndex = inStartIndex;
        endIndex = inEndIndex;
    }

    /**
     * Get the organization short name.
     *
     * @return the organization short name to get pending groups for.
     */
    public String getOrganizationShortName()
    {
        return organizationShortName;
    }

    /**
     * Set the organization short name.
     *
     * @param inOrganizationShortName
     *            the short name of the org to fetch
     */
    public void setOrganizationShortName(final String inOrganizationShortName)
    {
        organizationShortName = inOrganizationShortName;
    }

    /**
     * Get the start index for the pending groups in the organization.
     *
     * @return the start index for the pending groups in the organization
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * Set the start index for the pending groups in the organization.
     *
     * @param inStartIndex
     *            the start index for the pending groups in the organization.
     */
    public void setStartIndex(final Integer inStartIndex)
    {
        startIndex = inStartIndex;
    }

    /**
     * Get the end index for the pending groups in the organization.
     *
     * @return the end index for the pending groups in the organization.
     */
    public Integer getEndIndex()
    {
        return endIndex;
    }

    /**
     * Set the end index for the pending groups in the organization.
     *
     * @param inEndIndex
     *            the end index for the pending groups in the organization.
     */
    public void setEndIndex(final Integer inEndIndex)
    {
        endIndex = inEndIndex;
    }

}
