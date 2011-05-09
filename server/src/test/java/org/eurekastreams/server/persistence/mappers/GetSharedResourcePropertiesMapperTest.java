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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
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
     * Mapper to get a stream scope id from type and key.
     */
    private DomainMapper<String, StreamScope> getResourceStreamScopeByKeyMapper = context.mock(DomainMapper.class,
            "getStreamScopeIdFromTypeAndKeyMapper");

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
     * System under test.
     */
    private GetSharedResourcePropertiesMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetSharedResourcePropertiesMapper(getResourceStreamScopeByKeyMapper,
                getPeopleThatSharedResourceMapper, getPeopleThatLikedResourceMapper);
    }

    /**
     * Test execute when the shared resource doesn't exist in the database.
     */
    @Test
    public void testExecuteWhenSharedResourceDNE()
    {
        final String uniqueKey = "http://foo.com";
        final StreamScope sharedResourceStreamScope = null;
        final SharedResourceRequest request = new SharedResourceRequest(uniqueKey, null);

        context.checking(new Expectations()
        {
            {
                oneOf(getResourceStreamScopeByKeyMapper).execute(with(uniqueKey));
                will(returnValue(sharedResourceStreamScope));
            }
        });

        SharedResourceDTO result = sut.execute(request);
        assertNull(result.getStreamScopeId());
        assertEquals(0, result.getLikeCount());
        assertEquals(0, result.getShareCount());
        assertNull(result.getLikersSample());
        assertNull(result.getSharersSample());
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
        final StreamScope sharedResourceStreamScope = context.mock(StreamScope.class);
        final SharedResourceRequest request = new SharedResourceRequest(uniqueKey, null);
        final List<Long> sharerIds = new ArrayList<Long>();
        final List<Long> likerIds = new ArrayList<Long>();
        final List<PersonModelView> people = new ArrayList<PersonModelView>();

        final Long streamScopeId = 282834L;
        final Long sharedResourceId = 83348L;

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

        context.checking(new Expectations()
        {
            {
                oneOf(sharedResourceStreamScope).getId();
                will(returnValue(streamScopeId));

                oneOf(sharedResourceStreamScope).getDestinationEntityId();
                will(returnValue(sharedResourceId));

                oneOf(getResourceStreamScopeByKeyMapper).execute(with(uniqueKey));
                will(returnValue(sharedResourceStreamScope));

                oneOf(getPeopleThatSharedResourceMapper).execute(with(request));
                will(returnValue(sharerIds));

                oneOf(getPeopleThatLikedResourceMapper).execute(with(request));
                will(returnValue(likerIds));
            }
        });

        // execute sut
        SharedResourceDTO result = sut.execute(request);

        // make sure the request was updated with the shared resource id
        assertEquals(sharedResourceId, request.getSharedResourceId());

        // make sure the top-level properties look right
        assertEquals(streamScopeId, result.getStreamScopeId());
        assertEquals(5, result.getLikeCount());
        assertEquals(6, result.getShareCount());
        assertNull(result.getLikersSample());
        assertNull(result.getSharersSample());
        assertEquals(uniqueKey, result.getKey());

        context.assertIsSatisfied();
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
