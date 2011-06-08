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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.DeleteCacheKeys;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for DeleteFeaturedStreamExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class DeleteFeaturedStreamExecutionTest
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
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerConext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Delete mapper.
     */
    private DomainMapper<Long, Void> deleteMapper = context.mock(DomainMapper.class, "deleteMapper");

    /**
     * Mapper for deleting cache keys.
     */
    private DeleteCacheKeys deleteCacheKeyDAO = context.mock(DeleteCacheKeys.class, "deleteCacheKeyDAO");

    /**
     * FeaturedStream id.
     */
    private Long featuredStreamId = 5L;

    /**
     * System under test.
     */
    private DeleteFeaturedStreamExecution sut = new DeleteFeaturedStreamExecution(deleteMapper, deleteCacheKeyDAO);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerConext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(featuredStreamId));

                allowing(deleteMapper).execute(featuredStreamId);

                oneOf(deleteCacheKeyDAO).execute(with(any(Set.class)));

                allowing(taskHandlerConext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        sut.execute(taskHandlerConext);

        // check to make sure the right cache keys will be cleared
        Set<String> keysToDelete = (Set<String>) (requests.get(0)).getParams();
        Assert.assertEquals(1, keysToDelete.size());
        Assert.assertTrue(keysToDelete.contains(CacheKeys.FEATURED_STREAMS));

        context.assertIsSatisfied();
    }
}
