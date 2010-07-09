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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashSet;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for GetCompleteStreamViewByIdExecution class.
 *
 */
public class GetCompleteStreamViewByIdExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * FindById DAO mock.
     */
    @SuppressWarnings("unchecked")
    private FindByIdMapper findByIdDAO = context.mock(FindByIdMapper.class);

    /**
     * UserDetails mock.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * GetDomainGroupsByShortNames mock.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortNamesMock = context
            .mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get people by account id.
     */
    private GetPeopleByAccountIds getPeopleByAccountIds = context.mock(GetPeopleByAccountIds.class);

    /**
     * {@link ActionContext} mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * System under test.
     */
    GetCompleteStreamViewByIdExecution sut;

    /**
     * Set up.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setup()
    {
        sut = new GetCompleteStreamViewByIdExecution(findByIdDAO, getDomainGroupsByShortNamesMock,
                getPeopleByAccountIds);
    }

    /**
     * Test perform action.
     */
    @Test
    public void testPerformAction()
    {
        final StreamView streamView = context.mock(StreamView.class);
        final HashSet<StreamScope> scopes = new HashSet<StreamScope>();

        StreamScope personScope = new StreamScope(ScopeType.PERSON, "accountId", 1L);
        StreamScope groupScope = new StreamScope(ScopeType.GROUP, "shortName", 2L);

        scopes.add(personScope);
        scopes.add(groupScope);

        final DomainGroupModelView dgmv = new DomainGroupModelView();
        dgmv.setName("Group Display Name");

        final PersonModelView pmv = new PersonModelView();
        pmv.setDisplayName("Person Display Name");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(1L));

                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamView));

                oneOf(streamView).getIncludedScopes();
                will(returnValue(scopes));

                oneOf(getPeopleByAccountIds).fetchUniqueResult("accountid");
                will(returnValue(pmv));

                oneOf(getDomainGroupsByShortNamesMock).fetchUniqueResult("shortname");
                will(returnValue(dgmv));
            }
        });

        assertSame(streamView, sut.execute(actionContext));
        assertEquals("Group Display Name", groupScope.getDisplayName());
        assertEquals("Person Display Name", personScope.getDisplayName());

        context.assertIsSatisfied();
    }

}
