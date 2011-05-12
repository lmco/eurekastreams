/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.requests.GetPendingDomainGroupsForOrgRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the get GetPendingDomainGroupsForOrgTest action mapper.
 * 
 */
public class GetPendingDomainGroupsForOrgTest extends MapperTest
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
     * Mapper to get people by IDs, using cache.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> getPeopleByIdsMapperMock = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private GetPendingDomainGroupsForOrg sut;

    /**
     * The mock of the request.
     */
    private final GetPendingDomainGroupsForOrgRequest requestMock = context
            .mock(GetPendingDomainGroupsForOrgRequest.class);

    /**
     * Setup method - initialize the caches.
     */
    @Before
    public void setup()
    {
        sut = new GetPendingDomainGroupsForOrg();
        sut.setEntityManager(getEntityManager());
        sut.setGetPeopleByIdsMapper(getPeopleByIdsMapperMock);
    }

    /**
     * Test the execution of the action.
     */
    @Test
    public void testExecute()
    {
        final String fordAccountId = "lskdjfslsdlf";
        final String fordDisplayName = "lsdkjfsldkjfsdjfsdfjldsf";
        final String saganAccountId = "lksdkdkdkdkkdkkdkdkd";
        final String saganDisplayName = "kkkkkkkkkkkkkkkkkkkkkkkkkkk";

        final String shortName = "sdflkjsdflksdjfdslsdjfsdfkl";
        final long fordpId = 42L;
        final long saganId = 4507L;
        final Set<Long> orgIds = new HashSet<Long>();

        orgIds.add(5L);
        orgIds.add(6L);
        orgIds.add(7L);

        // setup the people ids to get from cache
        final List<Long> expectedPersonIdsToFetch = new ArrayList<Long>();
        expectedPersonIdsToFetch.add(fordpId);
        expectedPersonIdsToFetch.add(saganId);

        // setup the people that were fetched from cache
        PersonModelView fordPMV = new PersonModelView();
        fordPMV.setEntityId(fordpId);
        fordPMV.setAccountId(fordAccountId);
        fordPMV.setDisplayName(fordDisplayName);

        PersonModelView saganPMV = new PersonModelView();
        saganPMV.setEntityId(saganId);
        saganPMV.setAccountId(saganAccountId);
        saganPMV.setDisplayName(saganDisplayName);

        final List<PersonModelView> fetchedPeople = new ArrayList<PersonModelView>();
        fetchedPeople.add(fordPMV);
        fetchedPeople.add(saganPMV);

        context.checking(new Expectations()
        {
            {
                // mock out the request
                atLeast(3).of(requestMock).getPageStart();
                will(returnValue(0));
                oneOf(requestMock).getMaxResults();
                will(returnValue(3));

                allowing(getPeopleByIdsMapperMock).execute(expectedPersonIdsToFetch);
                will(returnValue(fetchedPeople));
            }
        });
        PagedSet<DomainGroupModelView> results = sut.execute(requestMock);

        context.assertIsSatisfied();

        // make sure the groups came back in the right order
        assertEquals(3, results.getPagedSet().size());
        assertEquals(6L, results.getPagedSet().get(0).getEntityId());
        assertEquals(7L, results.getPagedSet().get(1).getEntityId());
        assertEquals(8L, results.getPagedSet().get(2).getEntityId());

        // make sure the creator display names from cache were used
        assertEquals(fordDisplayName, results.getPagedSet().get(0).getPersonCreatedByDisplayName());
        assertEquals(fordDisplayName, results.getPagedSet().get(1).getPersonCreatedByDisplayName());
        assertEquals(saganDisplayName, results.getPagedSet().get(2).getPersonCreatedByDisplayName());

        // make sure the creator account ids from cache were used
        assertEquals(fordAccountId, results.getPagedSet().get(0).getPersonCreatedByAccountId());
        assertEquals(fordAccountId, results.getPagedSet().get(1).getPersonCreatedByAccountId());
        assertEquals(saganAccountId, results.getPagedSet().get(2).getPersonCreatedByAccountId());
    }
}
