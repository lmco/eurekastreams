/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.*;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class tests the functionality of the {@link AddPrivateGroupIdToCachedCoordinatorAccessList} cache mapper.
 *
 */
public class AddPrivateGroupIdToCachedCoordinatorAccessListTest extends CachedMapperTest
{
    /**
     * Instance of the {@link GetAllPersonIdsWhoHaveGroupCoordinatorAccess} mapper.
     */
    @Autowired
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorAccessMapper; 
    
    /**
     * System under test.
     */
    private AddPrivateGroupIdToCachedCoordinatorAccessList sut;
    
    /**
     * Test root org coordinator.
     */
    private static final Long ROOT_ORG_COORD_1 = 42L;
    
    /**
     * Test child org coordinator.
     */
    private static final Long CHILD_ORG_COORD_1 = 99L;
    
    /**
     * Test child org coordinator.
     */
    private static final Long CHILD_ORG_COORD_2 = 142L;
    
    /**
     * Test group id.
     */
    private static final Long GROUP_ID = 1L;
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new AddPrivateGroupIdToCachedCoordinatorAccessList(groupCoordinatorAccessMapper);
        sut.setCache(getCache());
        sut.setEntityManager(getEntityManager());
    }
    
    /**
     * Test the execution of the sut.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        Set<Long> origRootOrgCoordList = 
            (Set<Long>) getCache().get(
                    CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + ROOT_ORG_COORD_1);
        assertNull(origRootOrgCoordList);
        
        sut.execute(GROUP_ID);
        
        Set<Long> updatedRootOrgCoordList = 
            (Set<Long>) getCache().get(
                    CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + ROOT_ORG_COORD_1);
        assertNotNull(updatedRootOrgCoordList);
        assertEquals(1, updatedRootOrgCoordList.size());
        assertTrue(updatedRootOrgCoordList.contains(GROUP_ID));
    }
}
