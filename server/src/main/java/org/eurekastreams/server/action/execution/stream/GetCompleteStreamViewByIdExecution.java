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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Get {@link StreamView} by Id with initialized scopes containing display names.
 * 
 */
public class GetCompleteStreamViewByIdExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * FindByIDMapper for {@link StreamView}s.
     */
    private FindByIdMapper<StreamView> streamViewDAO;

    /**
     * Mapper to get domain groups by short name.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortName;

    /**
     * Mapper to get people by account id.
     */
    private GetPeopleByAccountIds getPeopleByAccountIds;

    /**
     * Constructor.
     * 
     * @param inStreamViewDAO
     *            FindByIDMapper for {@link StreamView}s.
     * @param inGetDomainGroupsByShortName
     *            mapper to get domain groups by short name
     * @param inGetPeopleByAccountIds
     *            mapper to get people by account id
     */
    public GetCompleteStreamViewByIdExecution(final FindByIdMapper<StreamView> inStreamViewDAO,
            final GetDomainGroupsByShortNames inGetDomainGroupsByShortName,
            final GetPeopleByAccountIds inGetPeopleByAccountIds)
    {
        streamViewDAO = inStreamViewDAO;
        getDomainGroupsByShortName = inGetDomainGroupsByShortName;
        getPeopleByAccountIds = inGetPeopleByAccountIds;
    }

    /**
     * Returns initialized {@link StreamView}.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return initialized {@link StreamView}.
     */
    @Override
    public StreamView execute(final ActionContext inActionContext)
    {
        StreamView result = streamViewDAO.execute(new FindByIdRequest(StreamView.getDomainEntityName(),
                (Long) inActionContext.getParams()));

        // set DisplayNames for Person and Group scopes.
        for (StreamScope scope : result.getIncludedScopes())
        {
            String displayName = null;
            switch (scope.getScopeType())
            {
            case PERSON:
                displayName = getPeopleByAccountIds.fetchUniqueResult(scope.getUniqueKey()).getDisplayName();
                break;
            case GROUP:
                displayName = getDomainGroupsByShortName.fetchUniqueResult(scope.getUniqueKey()).getName();
                break;
            default:
                break;
            }
            scope.setDisplayName(displayName);
        }

        return result;
    }

}
