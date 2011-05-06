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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSharedResourcePropertiesExecution.
 */
public class GetSharedResourcePropertiesExecutionTest
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
     * The mapper to get the shared resource dto by unique key - includes everything except the sampling of people that
     * liked the shared resource - those need to be filled in at runtime to avoid the headaches of keeping display names
     * and avatars in cache in yet another place.
     */
    private final DomainMapper<SharedResourceRequest, SharedResourceDTO> mapper = context.mock(DomainMapper.class,
            "mapper");

    /**
     * Mapper to get skeleton person model views by ids - only info needed for avatars.
     */
    private final FakeGetPeopleModelViewsByIdsMapper getPeopleModelViewsByIdsMapper = // 
    new FakeGetPeopleModelViewsByIdsMapper();

    /**
     * System under test.
     */
    private GetSharedResourcePropertiesExecution sut = new GetSharedResourcePropertiesExecution(mapper,
            getPeopleModelViewsByIdsMapper);

    /**
     * Principal action context.
     */
    final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * User's principal.
     */
    final Principal userPrincipal = context.mock(Principal.class);

    /**
     * User's person id.
     */
    final Long personId = 2L;

    /**
     * Request.
     */
    final SharedResourceRequest request = new SharedResourceRequest("FOO", 3L);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                oneOf(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                oneOf(userPrincipal).getId();
                will(returnValue(personId));
            }
        });

        getPeopleModelViewsByIdsMapper.setRequest(null);
        getPeopleModelViewsByIdsMapper.setResponse(null);
    }

    /**
     * Test execute when the SharedResource is found, but with no likers or sharers, and dto had nulls for liked ids.
     */
    @Test
    public void testExecuteWhenFoundAndNoSharersOrLikersWithNullDtoIdLists()
    {
        final SharedResourceDTO dto = new SharedResourceDTO();
        dto.setLikerPersonIds(null);
        dto.setSharerPersonIds(null);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(request);
                will(returnValue(dto));
            }
        });

        sut.execute(actionContext);

        Assert.assertEquals(0, dto.getLikerPersonIds().size());
        Assert.assertEquals(0, dto.getSharerPersonIds().size());
        Assert.assertEquals(0, dto.getSharersSample().size());
        Assert.assertEquals(0, dto.getSharersSample().size());
        Assert.assertFalse(dto.isLiked());
        Assert.assertEquals(0, dto.getLikeCount());
        Assert.assertEquals(0, dto.getShareCount());

        context.assertIsSatisfied();
    }

    /**
     * Test execute when the SharedResource is found, but with no likers or sharers, and dto had empty lists for liked
     * ids.
     */
    @Test
    public void testExecuteWhenFoundAndNoSharersOrLikersWithNonNullDtoIdLists()
    {
        final SharedResourceDTO dto = new SharedResourceDTO();
        dto.setLikerPersonIds(new ArrayList<Long>());
        dto.setSharerPersonIds(new ArrayList<Long>());

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(request);
                will(returnValue(dto));
            }
        });

        sut.execute(actionContext);

        Assert.assertEquals(0, dto.getLikerPersonIds().size());
        Assert.assertEquals(0, dto.getSharerPersonIds().size());
        Assert.assertEquals(0, dto.getSharersSample().size());
        Assert.assertEquals(0, dto.getSharersSample().size());
        Assert.assertFalse(dto.isLiked());
        Assert.assertEquals(0, dto.getLikeCount());
        Assert.assertEquals(0, dto.getShareCount());

        context.assertIsSatisfied();
    }

    /**
     * Test execute when the SharedResource is NOT found.
     */
    @Test
    public void testExecuteWhenNotFound()
    {
        final SharedResourceDTO dto = new SharedResourceDTO();
        dto.setLikerPersonIds(new ArrayList<Long>());
        dto.setSharerPersonIds(new ArrayList<Long>());

        dto.getLikerPersonIds().add(1L);
        dto.getLikerPersonIds().add(2L);
        dto.getLikerPersonIds().add(3L);
        dto.getLikerPersonIds().add(4L);
        dto.getLikerPersonIds().add(5L);
        dto.getLikerPersonIds().add(8L);

        dto.getSharerPersonIds().add(1L);
        dto.getSharerPersonIds().add(3L);
        dto.getSharerPersonIds().add(5L);
        dto.getSharerPersonIds().add(6L);

        final List<PersonModelView> people = new ArrayList<PersonModelView>();

        PersonModelView mv1 = new PersonModelView();
        mv1.setEntityId(1L);
        people.add(mv1);

        PersonModelView mv2 = new PersonModelView();
        mv2.setEntityId(2L);
        people.add(mv2);

        PersonModelView mv3 = new PersonModelView();
        mv3.setEntityId(3L);
        people.add(mv3);

        PersonModelView mv4 = new PersonModelView();
        mv4.setEntityId(4L);
        people.add(mv4);

        PersonModelView mv5 = new PersonModelView();
        mv5.setEntityId(5L);
        people.add(mv5);

        PersonModelView mv6 = new PersonModelView();
        mv6.setEntityId(6L);
        people.add(mv6);

        final List<Long> peopleIds = new ArrayList<Long>();
        peopleIds.add(1L);
        peopleIds.add(3L);
        peopleIds.add(5L);
        peopleIds.add(6L);
        peopleIds.add(2L);
        peopleIds.add(4L);

        getPeopleModelViewsByIdsMapper.setResponse(people);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(request);
                will(returnValue(dto));
            }
        });

        sut.execute(actionContext);

        // make sure the right people ids were passed into the getpeoplebyids
        Assert.assertEquals(6, getPeopleModelViewsByIdsMapper.getRequest().size());
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(1L));
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(2L));
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(5L));
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(6L));
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(2L));
        Assert.assertTrue(getPeopleModelViewsByIdsMapper.getRequest().contains(4L));

        Assert.assertEquals(6, dto.getLikerPersonIds().size());
        Assert.assertEquals(4, dto.getSharerPersonIds().size());

        Assert.assertEquals(4, dto.getLikersSample().size());
        Assert.assertEquals(4, dto.getSharersSample().size());

        Assert.assertTrue(dto.getLikersSample().contains(mv1));
        Assert.assertTrue(dto.getLikersSample().contains(mv2));
        Assert.assertTrue(dto.getLikersSample().contains(mv3));
        Assert.assertTrue(dto.getLikersSample().contains(mv4));

        Assert.assertTrue(dto.getSharersSample().contains(mv1));
        Assert.assertTrue(dto.getSharersSample().contains(mv3));
        Assert.assertTrue(dto.getSharersSample().contains(mv5));
        Assert.assertTrue(dto.getSharersSample().contains(mv6));

        Assert.assertTrue(dto.isLiked());

        context.assertIsSatisfied();
    }

    /**
     * Fake mapper to test the params to getPeopleModelViewsByIds.
     */
    private class FakeGetPeopleModelViewsByIdsMapper implements DomainMapper<List<Long>, List<PersonModelView>>
    {
        /**
         * The request.
         */
        private List<Long> request;

        /**
         * The canned response.
         */
        private List<PersonModelView> response;

        /**
         * Return the canned response, and store the request.
         * 
         * @param inRequest
         *            the request
         * @return the canned response
         */
        @Override
        public List<PersonModelView> execute(final List<Long> inRequest)
        {
            request = inRequest;
            return response;
        }

        /**
         * @return the request
         */
        public List<Long> getRequest()
        {
            return request;
        }

        /**
         * @param inRequest
         *            the request to set
         */
        public void setRequest(final List<Long> inRequest)
        {
            request = inRequest;
        }

        /**
         * @return the response
         */
        public List<PersonModelView> getResponse()
        {
            return response;
        }

        /**
         * @param inResponse
         *            the response to set
         */
        public void setResponse(final List<PersonModelView> inResponse)
        {
            response = inResponse;
        }

    }
}
