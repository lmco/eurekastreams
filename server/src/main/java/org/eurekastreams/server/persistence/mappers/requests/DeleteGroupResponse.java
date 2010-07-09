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
import java.util.Set;

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
     * Group streamView id.
     */
    private Long streamViewId;

    /**
     * Group streamScope id.
     */
    private Long streamScopeId;

    /**
     * Set of parent organization ids.
     */
    private Set<Long> parentOrganizationIds;

    /**
     * List of compositeStream ids that contained the group's stream scope.
     */
    private List<Long> compositeStreamsidsContainingGroupStream;

    /**
     * Constructor.
     * 
     * @param inGroupId
     *            Group id.
     * @param inGroupShortName
     *            Group short name.
     * @param inStreamViewId
     *            Group streamView id.
     * @param inStreamScopeId
     *            Group streamScope id.
     * @param inParentOrganizationIds
     *            Set of parent organization ids.
     * @param inCompositeStreamsidsContainingGroupStream
     *            ids of composite streams containing group stream.
     */
    public DeleteGroupResponse(final Long inGroupId, final String inGroupShortName, final Long inStreamViewId,
            final Long inStreamScopeId, final Set<Long> inParentOrganizationIds,
            final List<Long> inCompositeStreamsidsContainingGroupStream)
    {

        groupId = inGroupId;
        groupShortName = inGroupShortName;
        streamViewId = inStreamViewId;
        streamScopeId = inStreamScopeId;
        parentOrganizationIds = inParentOrganizationIds;
        compositeStreamsidsContainingGroupStream = inCompositeStreamsidsContainingGroupStream;
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
     * @return the streamViewId
     */
    public Long getStreamViewId()
    {
        return streamViewId;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @return the parentOrganizationIds
     */
    public Set<Long> getParentOrganizationIds()
    {
        return parentOrganizationIds;
    }

    /**
     * @return the compositeStreamsidsContainingGroupStream
     */
    public List<Long> getCompositeStreamsidsContainingGroupStream()
    {
        return compositeStreamsidsContainingGroupStream;
    }

}
