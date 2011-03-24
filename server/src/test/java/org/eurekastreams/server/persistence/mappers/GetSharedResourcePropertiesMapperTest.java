/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.requests.SharedResourceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSharedResourcePropertiesMapper.
 */
public class GetSharedResourcePropertiesMapperTest
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
     * Mapper that gets the ids of people that liked a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatSharedResourceMapper = context.mock(
            DomainMapper.class, "getPeopleThatSharedResourceMapper");

    /**
     * Mapper that gets the ids of people that shared a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatLikedResourceMapper = context.mock(
            DomainMapper.class, "getPeopleThatLikedResourceMapper");

    /**
     * Mapper to get person model views by ids.
     */
    private GetPeopleByIdsFake getPeopleModelViewsByIdsMapper = new GetPeopleByIdsFake();

    /**
     * System under test.
     */
    private GetSharedResourcePropertiesMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetSharedResourcePropertiesMapper(getPeopleThatSharedResourceMapper,
                getPeopleThatLikedResourceMapper, getPeopleModelViewsByIdsMapper);
        getPeopleModelViewsByIdsMapper.setCannedResponse(null);
    }

    /**
     * Test execute when the shared resource doesn't exist in the database.
     */
    @Test
    public void testExecuteWhenSharedResourceDNE()
    {
        final String uniqueKey = "http://foo.com";
        final SharedResourceRequest request = new SharedResourceRequest(BaseObjectType.BOOKMARK, uniqueKey);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleThatSharedResourceMapper).execute(with(request));
                will(returnValue(new ArrayList<Long>()));

                oneOf(getPeopleThatLikedResourceMapper).execute(with(request));
                will(returnValue(new ArrayList<Long>()));
            }
        });

        SharedResourceDTO result = sut.execute(request);
        assertEquals(0, result.getLikeCount());
        assertEquals(0, result.getShareCount());
        assertEquals(0, result.getLikersSample().size());
        assertEquals(0, result.getSharersSample().size());
        assertEquals(uniqueKey, result.getKey());

        context.assertIsSatisfied();
    }

    /**
     * Test execute when the shared resource doesn't exist in the database.
     */
    @Test
    public void testExecuteWhenSharedExists()
    {
        final String uniqueKey = "http://foo.com";
        final SharedResourceRequest request = new SharedResourceRequest(BaseObjectType.BOOKMARK, uniqueKey);
        final List<Long> sharerIds = new ArrayList<Long>();
        final List<Long> likerIds = new ArrayList<Long>();
        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        people.add(buildPersonModelView(1L));
        people.add(buildPersonModelView(2L));
        people.add(buildPersonModelView(3L));
        people.add(buildPersonModelView(4L));
        people.add(buildPersonModelView(5L));
        people.add(buildPersonModelView(6L));
        people.add(buildPersonModelView(7L));

        // 6 share
        sharerIds.add(1L); // will be included
        sharerIds.add(2L); // will be included
        sharerIds.add(3L); // will be included
        sharerIds.add(4L); // will be included
        sharerIds.add(5L);
        sharerIds.add(6L);

        // 5 likes
        likerIds.add(4L); // will be included
        likerIds.add(5L); // will be included
        likerIds.add(6L); // will be included
        likerIds.add(7L); // will be included
        likerIds.add(8L);

        getPeopleModelViewsByIdsMapper.setCannedResponse(people);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleThatSharedResourceMapper).execute(with(request));
                will(returnValue(sharerIds));

                oneOf(getPeopleThatLikedResourceMapper).execute(with(request));
                will(returnValue(likerIds));
            }
        });

        // execute sut
        SharedResourceDTO result = sut.execute(request);

        // make sure the people mapper was given the right list of people
        assertEquals(7, getPeopleModelViewsByIdsMapper.getStoredRequest().size());
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(1L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(2L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(3L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(4L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(5L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(6L));
        assertTrue(getPeopleModelViewsByIdsMapper.getStoredRequest().contains(7L));

        // make sure the top-level properties look right
        assertEquals(5, result.getLikeCount());
        assertEquals(6, result.getShareCount());
        assertEquals(4, result.getLikersSample().size());
        assertEquals(4, result.getSharersSample().size());
        assertEquals(uniqueKey, result.getKey());

        // make sure each person is in the right list
        checkForPersonModelView(result.getSharersSample(), 1L);
        checkForPersonModelView(result.getSharersSample(), 2L);
        checkForPersonModelView(result.getSharersSample(), 3L);
        checkForPersonModelView(result.getSharersSample(), 4L);

        checkForPersonModelView(result.getSharersSample(), 4L);
        checkForPersonModelView(result.getSharersSample(), 5L);
        checkForPersonModelView(result.getSharersSample(), 6L);
        checkForPersonModelView(result.getSharersSample(), 7L);

        context.assertIsSatisfied();
    }

    /**
     * Make sure a person modelview is in the input list, correctly trimmed.
     * 
     * @param inPeople
     *            the list to search
     * @param id
     *            the id to search for
     */
    private void checkForPersonModelView(final List<PersonModelView> inPeople, final long id)
    {
        for (PersonModelView p : inPeople)
        {
            if (p.getEntityId() == id)
            {
                assertEquals("accountid" + id, p.getAccountId());
                assertEquals("display" + id, p.getDisplayName());
                assertEquals("avatar" + id, p.getAvatarId());
                assertEquals(id, p.getId());
                assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, p.getLastName());
                return;
            }
        }
    }

    /**
     * Build a PersonModelView for the input id.
     * 
     * @param id
     *            the person's id
     * @return a PersonModelView for testing
     */
    private PersonModelView buildPersonModelView(final long id)
    {
        PersonModelView p = new PersonModelView();
        p.setEntityId(id);
        p.setAccountId("accountid" + id);
        p.setDisplayName("display" + id);
        p.setAvatarId("avatar" + id);
        p.setLastName("FOO");
        return p;
    }

    /**
     * Fake mapper to get people by ids.
     */
    private class GetPeopleByIdsFake implements DomainMapper<List<Long>, List<PersonModelView>>
    {
        /**
         * The stored request.
         */
        private List<Long> storedRequest;

        /**
         * The canned response.
         */
        private List<PersonModelView> cannedResponse;

        /**
         * @return the storedRequest
         */
        public List<Long> getStoredRequest()
        {
            return storedRequest;
        }

        /**
         * @param inStoredRequest
         *            the storedRequest to set
         */
        public void setStoredRequest(final List<Long> inStoredRequest)
        {
            storedRequest = inStoredRequest;
        }

        /**
         * @return the cannedResponse
         */
        public List<PersonModelView> getCannedResponse()
        {
            return cannedResponse;
        }

        /**
         * @param inCannedResponse
         *            the cannedResponse to set
         */
        public void setCannedResponse(final List<PersonModelView> inCannedResponse)
        {
            cannedResponse = inCannedResponse;
        }

        /**
         * execute, returning the canned response and storing the request.
         * 
         * @param inRequest
         *            the request containing the people ids
         * @return the canned list of person modelviews
         */
        @Override
        public List<PersonModelView> execute(final List<Long> inRequest)
        {
            storedRequest = inRequest;
            return cannedResponse;
        }
    }
}
