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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.Bannerable;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test all Bannerable Templates.
 * 
 */
public class BannerableMapperTest extends MapperTest
{

    /**
     * Domain Group Template.
     */
    @Autowired
    private DomainGroupBannerMapper domainGroupBannerMapper;

    /**
     * Group Test.
     */
    @Test
    public void updateGroupBannerableTest()
    {
        Bannerable bannerDTO;
        boolean success;

        // check to make sure it isn't set
        bannerDTO = domainGroupBannerMapper.getBannerableDTO(1L);
        assertEquals(bannerDTO.getBannerId(), null);
        assertEquals((long) bannerDTO.getBannerEntityId(), 1L);

        // set it to John
        success = domainGroupBannerMapper.updateBannerId(1L, "John");
        assertTrue(success);

        // Check again
        bannerDTO = domainGroupBannerMapper.getBannerableDTO(1L);
        assertEquals(bannerDTO.getBannerId(), "John");

        // update using id
        success = domainGroupBannerMapper.updateBannerId(1L, "JohnsDad");
        assertTrue(success);

        // Check again
        bannerDTO = domainGroupBannerMapper.getBannerableDTO(1L);
        assertEquals(bannerDTO.getBannerId(), "JohnsDad");

        // fail at updating
        success = domainGroupBannerMapper.updateBannerId(9L, "fail");
        assertFalse(success);
    }

}
