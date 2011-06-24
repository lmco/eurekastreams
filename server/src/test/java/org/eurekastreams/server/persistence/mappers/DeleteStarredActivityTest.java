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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteStarredActivity.
 *
 */
public class DeleteStarredActivityTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteStarredActivity deleteStarredActivity;
    
    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        ((SimpleMemoryCache) deleteStarredActivity.getCache()).clear();
    }

    /**
     * Test execute method with entry that's not there.
     */
    @Test
    public void testExecute()
    {
        //get initial count of entries
        Query q = getEntityManager().createQuery(
        "FROM StarredActivity");
        int initialSize = q.getResultList().size();
        
        //delete entry that's not there.
        assertTrue(deleteStarredActivity.execute(new StarredActivity(5L, 5L))); 
        
        //verify count hasn't changed. 
        assertTrue(q.getResultList().size() == initialSize);
    }
    
    /**
     * Test execute method with entry already in db.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteAlreadyPresent()
    {
        final long personId = 99L;
        final long activityId = 6789L;
        final long activityId2 = 54321L;
        
        Query q = getEntityManager().createQuery(
        "FROM StarredActivity where personId=:personId and activityId=:activityId");
        q.setParameter("personId", personId);
        q.setParameter("activityId", activityId);
        
        List<StarredActivity> results = q.getResultList();
        
        //verify that entry is present.
        assertTrue(results.size() == 1);
        
        String key = CacheKeys.STARRED_BY_PERSON_ID + personId;
        //verify that cached list is not empty
        List<Long> ids = new ArrayList<Long>();
        ids.add(activityId);
        ids.add(activityId2);
        deleteStarredActivity.getCache().setList(key, ids);
        assertTrue(deleteStarredActivity.getCache().getList(key) != null);
        
        //delete it.
        assertTrue(deleteStarredActivity.execute(new StarredActivity(personId, activityId))); 
        
        //verify it's gone.
        assertTrue(q.getResultList().size() == 0);
        
        List<Long> cachedList = deleteStarredActivity.getCache().getList(key);
        assertFalse(cachedList.contains(activityId));
        assertTrue(cachedList.contains(activityId2));
    }
}
