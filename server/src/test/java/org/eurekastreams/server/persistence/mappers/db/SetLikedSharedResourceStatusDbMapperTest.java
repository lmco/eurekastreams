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

import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.LikedSharedResource;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SetSharedResourceLikeMapperRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SetLikedSharedResourceStatusDbMapper.
 */
public class SetLikedSharedResourceStatusDbMapperTest extends MapperTest
{
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
    private final long followedLinkId = 5L;

    /**
     * ID of the shared resource link.
     */
    private final long unfollowedLinkId = 6L;

    /**
     * The link that the person is already following.
     */
    private final String followedLink = "http://foo.com/foo.html";

    /**
     * The link that the person isn't yet following.
     */
    private final String unfollowedLink = "http://foo.foo.com/foo.html";

    /**
     * Link that doesn't exist as a shared resource.
     */
    private final String linkThatDoesntExist = "http://fooooooooo.com";

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new SetLikedSharedResourceStatusDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test unliking a shared resource when the person liked it.
     */
    @Test
    public void testUnlikeWhenLiked()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, followedLink, BaseObjectType.BOOKMARK, false));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

        assertEquals(0, likedResources.size());
    }

    /**
     * Test unliking a shared resource when the person liked it, with the resource being the wrong case.
     */
    @Test
    public void testUnlikeWhenLikedWrongCase()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, followedLink.toUpperCase(),
                BaseObjectType.BOOKMARK, false));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

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
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

        assertEquals(0, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, unfollowedLink, BaseObjectType.BOOKMARK, false));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

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
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

        assertEquals(0, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, unfollowedLink, BaseObjectType.BOOKMARK, true));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

        assertEquals(1, likedResources.size());
    }

    /**
     * Test liking a resource when not previously liked, with the resource being the wrong case.
     */
    @Test
    public void testLikeWhenNotLikedWrongCase()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

        assertEquals(0, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, unfollowedLink.toUpperCase(),
                BaseObjectType.BOOKMARK, true));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", unfollowedLinkId).getResultList();

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
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, followedLink, BaseObjectType.BOOKMARK, true));

        likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId AND pk.sharedResourceId = :sharedResourceId")
                .setParameter("personId", personId).setParameter("sharedResourceId", followedLinkId).getResultList();

        assertEquals(1, likedResources.size());
    }

    /**
     * Test liking a resource that doesn't exist.
     */
    @Test
    public void testLikingResourceThatDoesntExist()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId").setParameter("personId", personId)
                .getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut
                .execute(new SetSharedResourceLikeMapperRequest(personId, linkThatDoesntExist, BaseObjectType.BOOKMARK,
                        true));

        likedResources = getEntityManager().createQuery("FROM LikedSharedResource where pk.personId = :personId")
                .setParameter("personId", personId).getResultList();
        assertEquals(1, likedResources.size());
    }

    /**
     * Test liking a resource that doesn't exist.
     */
    @Test
    public void testUnlikingResourceThatDoesntExist()
    {
        List<LikedSharedResource> likedResources = getEntityManager().createQuery(
                "FROM LikedSharedResource where pk.personId = :personId").setParameter("personId", personId)
                .getResultList();

        assertEquals(1, likedResources.size());
        getEntityManager().clear();

        sut.execute(new SetSharedResourceLikeMapperRequest(personId, linkThatDoesntExist, BaseObjectType.BOOKMARK,
                false));

        likedResources = getEntityManager().createQuery("FROM LikedSharedResource where pk.personId = :personId")
                .setParameter("personId", personId).getResultList();
        assertEquals(1, likedResources.size());

    }
}
