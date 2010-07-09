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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamSearchesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.persistence.mappers.stream.UserCompositeStreamSearchIdsMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetCurrentUserCompositeStreamSearchesExecutionStrategy.
 */
public class GetCurrentUserCompositeStreamSearchesExecutionStrategyTest
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
     * The person id.
     */
    private final Long personId = 387271L;

    /**
     * Person's parent org's name.
     */
    private final String parentOrgName = "sdlkfjsdl sldf sljf";

    /**
     * System under test.
     */
    private GetCurrentUserCompositeStreamSearchesExecutionStrategy sut;

    /**
     * ID mapper mock.
     */
    private UserCompositeStreamSearchIdsMapper idMapper = context.mock(UserCompositeStreamSearchIdsMapper.class);

    /**
     * bulk mapper mock.
     */
    private BulkCompositeStreamSearchesMapper bulkMapper = context.mock(BulkCompositeStreamSearchesMapper.class);

    /**
     * people mapper mock.
     */
    private GetPeopleByIds peopleMapper = context.mock(GetPeopleByIds.class);
    
    /**
     * Organizations mapper mock.
     */
    private GetOrganizationsByShortNames orgsMapper = context.mock(GetOrganizationsByShortNames.class);
    
    /**
     * Groups mapper mock.
     */
    private GetDomainGroupsByShortNames groupsMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * PrincipalActionContext mock.
     */
    private PrincipalActionContext principalActionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new GetCurrentUserCompositeStreamSearchesExecutionStrategy(idMapper, bulkMapper, peopleMapper,
                orgsMapper, groupsMapper);
    }

    /**
     * Execute test.
     *
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void executeTest() throws Exception
    {
        final String streamSearch1Name = "sldkfjsdlj ldkjf";

        // setup current user PersonModelView
        final PersonModelView currentUser = new PersonModelView();
        currentUser.setEntityId(personId);
        currentUser.setParentOrganizationName(parentOrgName);

        // setup composite stream search ids
        final ArrayList<Long> compositeStreamSearchIds = new ArrayList<Long>();
        compositeStreamSearchIds.add(8L);

        // setup composite stream searches
        final ArrayList<StreamSearch> compositeStreamSearches = new ArrayList<StreamSearch>();
        Set<String> keywords = new HashSet<String>();
        StreamView compositeStream = new StreamView();
        compositeStream.setName("streamname");
        final StreamSearch streamSearch1 = new StreamSearch(streamSearch1Name, compositeStream, keywords);
        compositeStreamSearches.add(streamSearch1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalActionContext).getPrincipal();
                will(returnValue(userPrincipal));

                oneOf(userPrincipal).getId();
                will(returnValue(personId));

                oneOf(peopleMapper).execute(personId);
                will(returnValue(currentUser));

                oneOf(idMapper).execute(personId);
                will(returnValue(compositeStreamSearchIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(compositeStreamSearches));

            }
        });

        GetCurrentUserStreamFiltersResponse results = (GetCurrentUserStreamFiltersResponse) sut
                .execute(principalActionContext);

        context.assertIsSatisfied();
        assertSame(compositeStreamSearches, results.getStreamFilters());

    }

    /**
     * Execute test with parent org.
     *
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void executeOnParentOrgStreamTest() throws Exception
    {
        // setup current user PersonModelView
        final PersonModelView currentUser = new PersonModelView();
        currentUser.setEntityId(personId);
        currentUser.setParentOrganizationName(parentOrgName);

        // setup composite stream search ids
        final ArrayList<Long> compositeStreamSearchIds = new ArrayList<Long>();
        compositeStreamSearchIds.add(8L);

        // setup composite stream searches
        final ArrayList<StreamSearch> compositeStreamSearches = new ArrayList<StreamSearch>();
        Set<String> keywords = new HashSet<String>();
        StreamView compositeStream1 = new StreamView();
        compositeStream1.setName("streamname");
        final StreamSearch streamSearch1 = new StreamSearch("foo", compositeStream1, keywords);
        compositeStreamSearches.add(streamSearch1);

        StreamView parentOrgStreamView = new StreamView();
        parentOrgStreamView.setName(StreamView.PARENT_ORG_TAG);
        final StreamSearch parentOrgStreamSearch = new StreamSearch("bar", parentOrgStreamView, keywords);
        compositeStreamSearches.add(parentOrgStreamSearch);

        context.checking(new Expectations()
        {
            {
                oneOf(principalActionContext).getPrincipal();
                will(returnValue(userPrincipal));

                oneOf(userPrincipal).getId();
                will(returnValue(personId));

                oneOf(peopleMapper).execute(personId);
                will(returnValue(currentUser));

                oneOf(idMapper).execute(personId);
                will(returnValue(compositeStreamSearchIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(compositeStreamSearches));

            }
        });

        GetCurrentUserStreamFiltersResponse results = (GetCurrentUserStreamFiltersResponse) sut
                .execute(principalActionContext);

        context.assertIsSatisfied();
        assertSame(compositeStreamSearches, results.getStreamFilters());
        assertEquals("Parent Org Stream should be renamed to the person's parent org", parentOrgName,
                parentOrgStreamSearch.getStreamView().getName());
    }

}
