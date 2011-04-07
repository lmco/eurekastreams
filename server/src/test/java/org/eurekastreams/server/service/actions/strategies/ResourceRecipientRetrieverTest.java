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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for ResourceRecipientRetriever.
 * 
 */
@SuppressWarnings("unchecked")
public class ResourceRecipientRetrieverTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private ResourceRecipientRetriever sut;

    /**
     * Root org id DAO.
     */
    private GetRootOrganizationIdAndShortName rootOrgIdDAO = context.mock(GetRootOrganizationIdAndShortName.class);

    /**
     * Mapper to get Org entity (not dto).
     */
    private FindByIdMapper findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Mapper to get Resource stream scope id.
     */
    private DomainMapper<SharedResourceRequest, SharedResource> streamResourceByUniqueKeyMapper = context.mock(
            DomainMapper.class, "streamResourceByUniqueKeyMapper");

    /**
     * Organizaiton mock.
     */
    private Organization org = context.mock(Organization.class);

    /**
     * ActivityDTO.
     */
    private ActivityDTO activityDTOMock = context.mock(ActivityDTO.class);

    /**
     * StreamEntityDTO.
     */
    private StreamEntityDTO streamEntityDTOMock = context.mock(StreamEntityDTO.class);

    /**
     * Shared resource mock.
     */
    private SharedResource sharedResourceMock = context.mock(SharedResource.class);

    /**
     * StreamScope mock.
     */
    private StreamScope streamScopeMock = context.mock(StreamScope.class);

    /**
     * Pre-test setup.
     */
    @Before
    public void setup()
    {
        sut = new ResourceRecipientRetriever(rootOrgIdDAO, findByIdMapper, streamResourceByUniqueKeyMapper);
    }

    /**
     * Test.
     */
    @Test
    public void testGetParentOrganization()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(rootOrgIdDAO).getRootOrganizationId();
                will(returnValue(1L));

                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(org));
            }
        });

        sut.getParentOrganization(activityDTOMock);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testGetStreamScope()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOMock).getDestinationStream();
                will(returnValue(streamEntityDTOMock));

                oneOf(streamEntityDTOMock).getUniqueIdentifier();
                will(returnValue("ui"));

                oneOf(streamResourceByUniqueKeyMapper).execute(with(any(SharedResourceRequest.class)));
                will(returnValue(sharedResourceMock));

                oneOf(sharedResourceMock).getStreamScope();
                will(returnValue(streamScopeMock));
            }
        });

        sut.getStreamScope(activityDTOMock);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testIsDestinationStreamPublic()
    {
        assertTrue(sut.isDestinationStreamPublic(activityDTOMock));
    }
}
