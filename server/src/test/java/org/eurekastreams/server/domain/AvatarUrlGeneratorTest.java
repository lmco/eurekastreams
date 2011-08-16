/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for AvatarUrlGenerator.
 */
public class AvatarUrlGeneratorTest
{
    /**
     * An entity id to use for testing. Arbitrary.
     */
    private Long id = 5L;
    /**
     * An avatar id to use for testing.
     */
    private String avatarId = "burrito";

    /**
     * An avatar id to use for testing.
     */
    private String bannerId = "burrito";

    /**
     * Subject under test - person.
     */
    private AvatarUrlGenerator personSut = new AvatarUrlGenerator(EntityType.PERSON);

    /**
     * Subject under test - group.
     */
    private AvatarUrlGenerator groupSut = new AvatarUrlGenerator(EntityType.GROUP);

    /**
     * Test that the avatar url is built correctly.
     */
    @Test
    public void getOriginalAvatarUrl()
    {
        String expected = "/eurekastreams/photos?img=oburrito";
        String returned = personSut.getOriginalAvatarUrl(avatarId);
        assertEquals(expected, returned);
        returned = groupSut.getOriginalAvatarUrl(avatarId);
        assertEquals(expected, returned);
    }

    /**
     * Test that the normal avatar URL is built correctly.
     */
    @Test
    public void getNormalAvatarUrl()
    {
        String expected = "/eurekastreams/photos?img=nburrito";
        String returned = personSut.getNormalAvatarUrl(avatarId);
        assertEquals(expected, returned);
        returned = groupSut.getNormalAvatarUrl(avatarId);
        assertEquals(expected, returned);
    }

    /**
     * Test that the small avatar URL is built correctly.
     */
    @Test
    public void getSmallAvatarUrl()
    {
        String expected = "/eurekastreams/photos?img=sburrito";
        String returned = personSut.getSmallAvatarUrl(avatarId);
        assertEquals(expected, returned);
        returned = groupSut.getSmallAvatarUrl(avatarId);
        assertEquals(expected, returned);
    }

    /**
     * Test that the banner URL is built correctly.
     */
    @Test
    public void getBannerUrlWithNonNullBannerId()
    {
        String expected = "/eurekastreams/photos?img=nburrito";
        String returned = personSut.getBannerUrl(bannerId);
        assertEquals(expected, returned);
        returned = groupSut.getBannerUrl(bannerId);
        assertEquals(expected, returned);
    }

    /**
     * Test that the banner URL is built correctly when passed in bannerid = null.
     */
    @Test
    public void getBannerUrlWithNullBannerId()
    {
        String expected = "/style/images/profile-banner-default.png";
        String returned = personSut.getBannerUrl(null);
        assertEquals(expected, returned);
        returned = groupSut.getBannerUrl(null);
        assertEquals(expected, returned);
    }
}
