/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for FollowedActivityIdsLoaderImpl class.
 *
 */
public class FollowedActivityIdsLoaderImplTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private FollowedActivityIdsLoaderImpl sut;    
    
    /**
     * User id from dataset.xml.
     */
    private static final long USER_ID = 99L;  
    
    /**
     * Test getFollowedActivityIds method.  
     */
    @Test
    public void testGetFollowedActivityIdsWithPersonGroupActivities()
    {        
        List<Long> results = sut.getFollowedActivityIds(USER_ID, 9);
        
        //assert correct number of results.
        assertEquals(2, results.size());
        
        //assert list is sorted correctly.
        assertTrue(results.get(0) > results.get(1));
    }
    
    /**
     * Test getFollowedActivityIds method.
     */
    @Test
    public void testGetFollowedActivityIdsWithPersonGroupActivitiesTrimList()
    {
        List<Long> results = sut.getFollowedActivityIds(USER_ID, 2);
        
        //assert correct number of results.
        assertEquals(2, results.size());
        
        //assert list is sorted correctly.
        assertTrue(results.get(0) > results.get(1));
    }
    
    /**
     * Get the Hibernate session from the EntityManager.
     * 
     * @return the Hibernate session from the EntityManager.
     */
    protected Session getHibernateSession()
    {
        return (Session) getEntityManager().getDelegate();
    }

}

