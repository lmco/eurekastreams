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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.Collections;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;

/**
 * Return OrganizationModelView by short name.
 */
public class GetOrganizationModelViewByShortNameExecution implements ExecutionStrategy<ActionContext>
{

    /**
     * {@link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames mapper;

    /**
     * {@link GetRootOrganizationIdAndShortName}.
     */
    private GetRootOrganizationIdAndShortName rootOrgNameMapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            {@link GetOrganizationsByShortNames}.
     * @param inRootOrgNameMapper
     *            {@link GetRootOrganizationIdAndShortName}.
     */
    public GetOrganizationModelViewByShortNameExecution(final GetOrganizationsByShortNames inMapper,
            final GetRootOrganizationIdAndShortName inRootOrgNameMapper)
    {
        mapper = inMapper;
        rootOrgNameMapper = inRootOrgNameMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        String orgShortName = (String) inActionContext.getParams();

        if (orgShortName == null || orgShortName.equals(""))
        {
            orgShortName = rootOrgNameMapper.getRootOrganizationShortName();
        }
        return mapper.execute(Collections.singletonList(orgShortName)).get(0);
    }

}
