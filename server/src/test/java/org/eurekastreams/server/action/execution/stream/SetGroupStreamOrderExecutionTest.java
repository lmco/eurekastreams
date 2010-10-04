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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.ReorderFollowedGroupIds;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetGroupStreamsViewOrderExecution} class.
 * 
 */
public class SetGroupStreamOrderExecutionTest
{
    /**
     * System under test.
     */
    private SetGroupStreamsOrderExecution sut;

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
     * Mapper to get the list of group ids that the user follows.
     */
    private GetFollowedGroupIds groupIdsMapper = context.mock(GetFollowedGroupIds.class);

    /**
     * Mapper that persists the reordered list to the db.
     */
    private ReorderFollowedGroupIds reorderMapper = context.mock(ReorderFollowedGroupIds.class);

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager = context.mock(EntityManager.class);
    
    /**
     * Cache instance to use for clearing the updated person's cache entry.
     */
    private Cache cache = context.mock(Cache.class);
    
    /**
     * Mocked instance of the Principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetGroupStreamsOrderExecution(groupIdsMapper, reorderMapper, cache);
        sut.setEntityManager(entityManager);
    }

    /**
     * Test.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void performAction()
    {
        SetStreamOrderRequest request = new SetStreamOrderRequest(0L, 1, 1);

        final Query query = context.mock(Query.class);

        final Long personId = 12L;
        
        final List<Long> groupIdList = new ArrayList<Long>();
        groupIdList.add(0L);
        groupIdList.add(5L);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(personId));

                oneOf(groupIdsMapper).execute(with(personId));
                will(returnValue(groupIdList));

                oneOf(reorderMapper).execute(with(any(Long.class)), with(any(List.class)));
                
                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));
                
                allowing(query).setParameter(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(query));
                
                oneOf(query).executeUpdate();
                
                oneOf(cache).delete(CacheKeys.PERSON_BY_ID + personId);
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.execute(currentContext);
        
        context.assertIsSatisfied();
        
        // make sure list is reordered
        assertEquals(new Long(5), groupIdList.get(0));
    }
}
