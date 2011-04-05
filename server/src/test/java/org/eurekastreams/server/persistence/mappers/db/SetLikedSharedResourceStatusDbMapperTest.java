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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.stream.LikedSharedResource;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SetSharedResourceLikeMapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SetLikedSharedResourceStatusDbMapper.
 */
public class SetLikedSharedResourceStatusDbMapperTest extends MapperTest
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
    private SetLikedSharedResourceStatusDbMapper sut;

    /**
     * Person id to use.
     */
    private final long personId = 42L;

    /**
     * ID of the shared resource link.
     */
    private final long likedLinkId = 5L;

    /**
     * ID of the shared resource link.
     */
    private final long unlikedLinkId = 6L;

    /**
     * Shared resource mock that's followed by the user.
     */
    private final SharedResource likedSr = context.mock(SharedResource.class, "followed");

    /**
     * Shared resource mock that's not followed by the user.
     */
    private final SharedResource unlikedSr = context.mock(SharedResource.class, "not followed");

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new SetLikedSharedResourceStatusDbMapper();
        sut.setEntityManager(getEntityManager());

        context.checking(new Expectations()
        {
            {
                allowing(likedSr).getId();
                will(returnValue(likedLinkId));

                allowing(unlikedSr).getId();
                will(returnValue(unlikedLinkId));
            }
        });
    }

    /**
     * Test unliking a shared resource when the person liked it.
     */
    @Test
    public void testUnlikeWhenLiked()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", likedLinkId).getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, likedSr, false));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", likedLinkId).getResultList();

        assertEquals(0, likedResources.size());
    }

    /**
     * Test unliking a shared resource when when not previously liked.
     */
    @Test
    public void testUnlikeWhenNotLiked()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unlikedLinkId).getResultList();

        assertEquals(0, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, unlikedSr, false));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unlikedLinkId).getResultList();

        assertEquals(0, likedResources.size());

    }

    /**
     * Test liking a resource when not previously liked.
     */
    @Test
    public void testLikeWhenNotLiked()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unlikedLinkId).getResultList();

        assertEquals(0, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, unlikedSr, true));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unlikedLinkId).getResultList();

        assertEquals(1, likedResources.size());
    }

    /**
     * Test liking a resource that was already liked.
     */
    @Test
    public void testLikeWhenLiked()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", likedLinkId).getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, likedSr, true));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", likedLinkId).getResultList();

        assertEquals(1, likedResources.size());
    }

}
