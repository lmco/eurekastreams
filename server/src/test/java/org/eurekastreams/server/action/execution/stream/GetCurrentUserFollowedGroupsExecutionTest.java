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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetCurrentUserFollowedGroupsExecution class.
 */
public class GetCurrentUserFollowedGroupsExecutionTest
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
     * System under test.
     */
    private GetCurrentUserFollowedGroupsExecution sut;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * ID mapper mock.
     */
    private GetFollowedGroupIds idMapper = context.mock(GetFollowedGroupIds.class);

    /**
     * Group mapper mock.
     */
    private GetDomainGroupsByIds groupMapper = context.mock(GetDomainGroupsByIds.class);

    /**
     * People mapper mock.
     */
    private GetPeopleByIds peopleMapper = context.mock(GetPeopleByIds.class);

    /**
     * Mapper to determine if a user has access to update a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupPermissionsChecker = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Person id.
     */
    private static final Long PERSON_ID = 82377L;

    /**
     * Hidden line index.
     */
    private static final int HIDDEN_LINE_INDEX = 4;

    /**
     * Test person.
     */
    private PersonModelView person;

    /**
     * Test group.
     */
    DomainGroupModelView group1;

    /**
     * Test group.
     */
    DomainGroupModelView group2;

    /**
     * Test group.
     */
    DomainGroupModelView group3;

    /**
     * Set up test.
     */
    @Before
    public final void setUp()
    {
        sut = new GetCurrentUserFollowedGroupsExecution(idMapper, groupMapper, peopleMapper, groupPermissionsChecker);
        group1 = new DomainGroupModelView();
        group2 = new DomainGroupModelView();
        group3 = new DomainGroupModelView();

        group1.setEntityId(1);
        group2.setEntityId(2);
        group3.setEntityId(3);

        group1.setName("Group1");
        group2.setName("Group2");
        group3.setName("Group3");

        group1.setShortName("group1");
        group2.setShortName("group2");
        group3.setShortName("group3");

        group1.setStreamId(1);
        group2.setStreamId(2);
        group3.setStreamId(3);

        person = new PersonModelView();
        person.setGroupStreamHiddenLineIndex(HIDDEN_LINE_INDEX);
    }

    /**
     * Test execute method.
     *
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void testExecute() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(PERSON_ID));

                oneOf(idMapper).execute(PERSON_ID);
                will(returnValue(Arrays.asList(1, 2, 3)));

                oneOf(groupMapper).execute(with(any(ArrayList.class)));
                will(returnValue(Arrays.asList(group1, group2, group3)));

                oneOf(peopleMapper).execute(with(any(List.class)));
                will(returnValue(Arrays.asList(person)));
            }
        });

        GetCurrentUserStreamFiltersResponse response = sut.execute(actionContext);

        assertEquals(3, response.getStreamFilters().size());
        assertEquals(HIDDEN_LINE_INDEX, response.getHiddenLineIndex().intValue());
        context.assertIsSatisfied();
    }
}
