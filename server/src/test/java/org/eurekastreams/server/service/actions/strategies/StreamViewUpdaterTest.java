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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.FindUserStreamViewById;
import org.eurekastreams.server.persistence.mappers.GetStreamScopeProxyById;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedActivityToListByStreamScope;
import org.eurekastreams.server.persistence.mappers.cache.GetCompositeStreamById;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedActivitiesFromListByStreamScope;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateCachedCompositeStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for StreamViewUpdater.
 *
 */
public class StreamViewUpdaterTest
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
     * Action Submitter mock.
     */
    private UpdateCachedCompositeStream updateCachedCompositeStreamMock = context
            .mock(UpdateCachedCompositeStream.class);

    /**
     * FindById DAO.
     */
    private FindUserStreamViewById findByIdDAO = context.mock(FindUserStreamViewById.class);

    /**
     * GetStreamScopeProxyById DAO.
     */
    private GetStreamScopeProxyById getStreamScopeProxyByIdDAO = context.mock(GetStreamScopeProxyById.class);

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * AddCachedActivityToListByStreamScope mock.
     */
    private AddCachedActivityToListByStreamScope addCachedActivityMapper = context
            .mock(AddCachedActivityToListByStreamScope.class);

    /**
     * RemoveCachedActivitiesFromListByStreamScope mock.
     */
    private RemoveCachedActivitiesFromListByStreamScope removeCachedActivityMapper = context
            .mock(RemoveCachedActivitiesFromListByStreamScope.class);

    /**
     * AsyncActionSubmitter mock.
     */
    private TaskHandler actionSubmitterMock = context.mock(TaskHandler.class);

    /**
     * GetCompositeStreamById mock.
     */
    private GetCompositeStreamById streamMapper = context.mock(GetCompositeStreamById.class);

    /**
     * System under test.
     */
    private StreamViewUpdater sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new StreamViewUpdater(updateCachedCompositeStreamMock, findByIdDAO, getStreamScopeProxyByIdDAO,
                streamMapper, addCachedActivityMapper, removeCachedActivityMapper);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));
            }
        });
    }

    /**
     * Test get() method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGet()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        ArrayList<StreamScope> scopes = new ArrayList<StreamScope>(1);
        scopes.add(new StreamScope(ScopeType.PERSON, "accountId"));
        map.put("scopes", scopes);
        map.put("name", "nameGoesHere");
        map.put("id", 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(principal).getId();
                will(returnValue(5L));

                oneOf(findByIdDAO).execute(with(any(FindUserStreamFilterByIdRequest.class)));
            }
        });

        sut.get(taskHandlerActionContext, map);
        context.assertIsSatisfied();
    }

    /**
     * Test persist method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    public void testPersist() throws Exception
    {
        final StreamScope streamScopeMock1 = context.mock(StreamScope.class, "streamScope1");
        final StreamScope streamScopeMock2 = context.mock(StreamScope.class, "streamScope2");
        final StreamScope streamScopeMock3 = context.mock(StreamScope.class, "streamScope3");
        final StreamScope streamScopeMock4 = context.mock(StreamScope.class, "streamScope4");

        final Set<StreamScope> currentStreamScopes = new HashSet<StreamScope>();
        currentStreamScopes.add(streamScopeMock1);
        currentStreamScopes.add(streamScopeMock2);
        currentStreamScopes.add(streamScopeMock3);

        final Set<StreamScope> targetStreamScopes = new HashSet<StreamScope>();
        targetStreamScopes.add(streamScopeMock2);
        targetStreamScopes.add(streamScopeMock3);
        targetStreamScopes.add(streamScopeMock4);

        final StreamView testStreamView = new StreamView();
        testStreamView.setName("testName");
        testStreamView.setIncludedScopes(currentStreamScopes);

        final StreamView targetStreamView = new StreamView();
        targetStreamView.setName("testName");
        targetStreamView.setIncludedScopes(targetStreamScopes);

        context.checking(new Expectations()
        {
            {
                oneOf(principal).getId();
                will(returnValue(5L));

                oneOf(streamMapper).execute(with(any(Long.class)));
                will(returnValue(testStreamView));

                oneOf(findByIdDAO).flush();

                oneOf(updateCachedCompositeStreamMock).execute(with(any(Long.class)));

                allowing(removeCachedActivityMapper).execute(
                        with(any(RemoveCachedActivitiesFromListByStreamScopeRequest.class)));

                allowing(addCachedActivityMapper).execute(with(any(AddCachedActivityToListByStreamScopeRequest.class)));

                allowing(actionSubmitterMock).handleTask(with(any(UserActionRequest.class)));
            }
        });

        assertEquals(0, userActionRequests.size());

        sut.persist(taskHandlerActionContext, null, targetStreamView);
        context.assertIsSatisfied();

        assertEquals(1, userActionRequests.size());
        assertEquals("refreshCachedCustomCompositeStreamAction", userActionRequests.get(0).getActionKey());
        assertEquals(new Long(5L), ((RefreshCachedCompositeStreamRequest) userActionRequests.get(0).getParams())
                .getListOwnerId());
    }

    /**
     * Stubbed out methods.
     */
    @Test
    public void testSetPropertiesName()
    {
        final StreamView sv = context.mock(StreamView.class);
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("name", "nameGoesHere");

        context.checking(new Expectations()
        {
            {
                oneOf(sv).setName("nameGoesHere");
            }
        });

        sut.setProperties(sv, map);
        context.assertIsSatisfied();
    }

    /**
     * Stubbed out methods.
     */
    @Test
    public void testSetPropertiesScope()
    {
        final StreamView sv = context.mock(StreamView.class);
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        final ArrayList<StreamScope> scopes = new ArrayList<StreamScope>(1);
        scopes.add(new StreamScope(ScopeType.PERSON, "accountId", 5L));
        map.put("scopes", scopes);

        context.checking(new Expectations()
        {
            {
                allowing(sv).getIncludedScopes();

                oneOf(getStreamScopeProxyByIdDAO).execute(5L);
            }
        });

        sut.setProperties(sv, map);
        context.assertIsSatisfied();
    }

}
