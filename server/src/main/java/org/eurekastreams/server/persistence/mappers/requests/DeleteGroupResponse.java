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
package org.eurekastreams.server.persistence.mappers.requests;

import java.util.List;

/**
 * Respose object for DeleteGroup mapper.
 *
 */
public class DeleteGroupResponse
{
    /**
     * Group id.
     */
    private Long groupId;

    /**
     * Group short name.
     */
    private String groupShortName;

    /**
     * Group streamScope id.
     */
    private Long streamScopeId;

    /**
     * List of parent organization short names.
     */
    private List<String> parentOrganizationShortNames;

    /**
     * Constructor.
     *
     * @param inGroupId
     *            Group id.
     * @param inGroupShortName
     *            Group short name.
     * @param inStreamScopeId
     *            Group streamScope id.
     * @param inParentOrganizationShortNames
     *            List of parent organization short names.
     */
    public DeleteGroupResponse(final Long inGroupId, final String inGroupShortName, final Long inStreamScopeId,
            final List<String> inParentOrganizationShortNames)
    {

        groupId = inGroupId;
        groupShortName = inGroupShortName;
        streamScopeId = inStreamScopeId;
        parentOrganizationShortNames = inParentOrganizationShortNames;
    }

    /**
     * @return the groupId
     */
    public Long getGroupId()
    {
        return groupId;
    }

    /**
     * @return the groupShortName
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @return the parentOrganizationShortNames
     */
    public List<String> getParentOrganizationShortNames()
    {
        return parentOrganizationShortNames;
    }

}
