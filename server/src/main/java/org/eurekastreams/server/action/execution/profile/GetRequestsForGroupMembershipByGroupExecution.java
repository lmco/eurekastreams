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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.db.GetRequestsForGroupMembershipByGroup;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Gets the list of people requesting membership in a given group.
 */
public class GetRequestsForGroupMembershipByGroupExecution implements ExecutionStrategy<ActionContext>
{
    /** Mapper for people. */
    private GetPeopleByIds peopleMapper;

    /** Mapper for list of people. */
    private GetRequestsForGroupMembershipByGroup requestMapper;


    /**
     * Constructor.
     *
     * @param inRequestMapper
     *            Mapper for list of people.
     * @param inPeopleMapper
     *            Mapper for people.
     */
    public GetRequestsForGroupMembershipByGroupExecution(final GetRequestsForGroupMembershipByGroup inRequestMapper,
            final GetPeopleByIds inPeopleMapper)
    {
        requestMapper = inRequestMapper;
        peopleMapper = inPeopleMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        GetRequestForGroupMembershipRequest request = (GetRequestForGroupMembershipRequest) inActionContext.getParams();

        PagedSet<Long> idsPagedSet = requestMapper.execute(request);
        List<PersonModelView> list;
        if (idsPagedSet.getPagedSet().isEmpty())
        {
            list = new ArrayList<PersonModelView>();
        }
        else
        {
            list = peopleMapper.execute(idsPagedSet.getPagedSet());
        }

        return new PagedSet<PersonModelView>(idsPagedSet.getFromIndex(), idsPagedSet.getToIndex(), idsPagedSet
                .getTotal(), list);
    }
}
