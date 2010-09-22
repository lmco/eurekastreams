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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.MemcachedCache;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.ReorderFollowedGroupIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for SetGroupStreamsOrderExecution class.
 */
public class SetGroupStreamsOrderExecutionTest
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
     * System under test.
     */
    private SetGroupStreamsOrderExecution sut;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * ID mapper mock.
     */
    private GetFollowedGroupIds idMapper = context.mock(GetFollowedGroupIds.class);

    /**
     * Reorder mapper mock.
     */
    private ReorderFollowedGroupIds reorderMapper = context.mock(ReorderFollowedGroupIds.class);

    /**
     * Mock entityManager.
     */
    private EntityManager entityManager = context.mock(EntityManager.class);
    
    /**
     * Mock cache.
     */
    private Cache cache = context.mock(MemcachedCache.class);
    
    /**
     * Mock query.
     */
    private Query query = context.mock(Query.class);

    /**
     * Person id.
     */
    private static final Long PERSON_ID = 82377L;

    /**
     * Hidden line index.
     */
    private static final int NEW_HIDDEN_LINE_INDEX = 2;

    /**
     * Test group.
     */
    DomainGroupModelView group1;

    /**
     * Test group.
     */
    DomainGroupModelView group2;

    /**
     * Test group.
     */
    DomainGroupModelView group3;

    /**
     * Test streamview.
     */
    StreamView streamView;

    /**
     * Group ids list.
     */
    List<Long> groupIds;

    /**
     * Set up test.
     */
    @Before
    public final void setUp()
    {
        sut = new SetGroupStreamsOrderExecution(idMapper, reorderMapper, cache);
        sut.setEntityManager(entityManager);
        
        group1 = new DomainGroupModelView();
        group2 = new DomainGroupModelView();
        group3 = new DomainGroupModelView();

        group1.setEntityId(1);
        group2.setEntityId(2);
        group3.setEntityId(3);

        group1.setName("Group1");
        group2.setName("Group2");
        group3.setName("Group3");

        group1.setShortName("group1");
        group2.setShortName("group2");
        group3.setShortName("group3");

        group1.setCompositeStreamId(1);
        group2.setCompositeStreamId(2);
        group3.setCompositeStreamId(3);

        group1.setStreamId(1);
        group2.setStreamId(2);
        group3.setStreamId(3);

        streamView = new StreamView();

        // Can't use Arrays.asList() here since that creates an AbstractList that can't be deleted from.
        groupIds = new ArrayList<Long>();
        groupIds.add(1L);
        groupIds.add(2L);
        groupIds.add(3L);
    }

    /**
     * Test execute method.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void testExecute() throws Exception
    {
        final SetStreamOrderRequest request = new SetStreamOrderRequest(2L, 0, NEW_HIDDEN_LINE_INDEX);
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(PERSON_ID));

                oneOf(idMapper).execute(PERSON_ID);
                will(returnValue(groupIds));

                oneOf(reorderMapper).execute(with(any(Long.class)), with(any(List.class)));

                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));

                oneOf(query).setParameter("newIndex", request.getHiddenLineIndex());
                will(returnValue(query));
                
                oneOf(query).setParameter("id", PERSON_ID);
                will(returnValue(query));
                
                oneOf(query).executeUpdate();
                
                oneOf(cache).delete(CacheKeys.PERSON_BY_ID + PERSON_ID);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
