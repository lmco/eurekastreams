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

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for InsertStarredActivity.
 *
 */
public class InsertStarredActivityTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private InsertStarredActivity insertStarredActivity;
    
    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        ((SimpleMemoryCache) insertStarredActivity.getCache()).clear();
    }

    /**
     * Test execute method with new entry.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final long activityId = 6790L;
        final long personId = 99L;
        
        //verify that entry is not present.
        Query q = getEntityManager().createQuery(
        "FROM StarredActivity where personId=:personId and activityId=:activityId").setParameter("personId",
                personId).setParameter("activityId", activityId);
        List<StarredActivity> results = q.getResultList();
        assertTrue(results.size() == 0);
        
        
        String key = CacheKeys.STARRED_BY_PERSON_ID + personId;
        //verify that cached list is empty
        assertTrue(insertStarredActivity.getCache().getList(key) == null);
        
        //insert it.
        assertTrue(insertStarredActivity.execute(new StarredActivity(personId, activityId))); 
        
        //verify it's there now.
        assertTrue(q.getResultList().size() == 1);
        
        assertTrue(insertStarredActivity.getCache().getList(key) != null);
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
        
        Query q = getEntityManager().createQuery(
        "FROM StarredActivity where personId=:personId and activityId=:activityId");
        q.setParameter("personId", personId);
        q.setParameter("activityId", activityId);
        
        List<StarredActivity> results = q.getResultList();
        
        //verify that entry is present.
        assertTrue(results.size() == 1);
        
        //insert.
        assertTrue(insertStarredActivity.execute(new StarredActivity(personId, activityId))); 
        
      //verify it's still there and not duplicated.
        assertTrue(q.getResultList().size() == 1);
    }

}
