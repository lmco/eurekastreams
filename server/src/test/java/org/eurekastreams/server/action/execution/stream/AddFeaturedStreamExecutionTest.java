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
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.FeaturedStream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.DeleteCacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for AddFeaturedStreamExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class AddFeaturedStreamExecutionTest
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
     * {@link FeaturedStreamDTO}.
     */
    private FeaturedStreamDTO fsdto = context.mock(FeaturedStreamDTO.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerConext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * StreamScope mapper.
     */
    DomainMapper<Long, StreamScope> streamScopeProxyMapper = context.mock(DomainMapper.class, "streamScopeProxyMapper");

    /**
     * Insert mapper.
     */
    DomainMapper<PersistenceRequest<FeaturedStream>, Long> insertMapper = context.mock(DomainMapper.class,
            "insertMapper");

    /**
     * Mapper for deleting cache keys.
     */
    private DeleteCacheKeys deleteCacheKeyDAO = context.mock(DeleteCacheKeys.class, "deleteCacheKeyDAO");

    /**
     * Description.
     */
    private String description = "description";

    /**
     * Stream id.
     */
    private Long streamId = 5L;

    /**
     * StreamScope.
     */
    private StreamScope streamScope = context.mock(StreamScope.class);

    /**
     * System under test.
     */
    private AddFeaturedStreamExecution sut = new AddFeaturedStreamExecution(streamScopeProxyMapper, insertMapper,
            deleteCacheKeyDAO);

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
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

                allowing(fsdto).getStreamId();
                will(returnValue(streamId));

                allowing(streamScopeProxyMapper).execute(streamId);
                will(returnValue(streamScope));

                allowing(insertMapper).execute(with(any(PersistenceRequest.class)));
                will(returnValue(9L));

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
