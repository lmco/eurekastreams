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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.FindUserStreamSearchById;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateCachedCompositeStreamSearch;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for StreamSearchUpdater.
 *
 */
@SuppressWarnings("unchecked")
public class StreamSearchUpdaterTest
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
     * Update Cached Composite Stream Search Mock.
     */
    private UpdateCachedCompositeStreamSearch updateCachedCompositeStreamSearchMock = context
            .mock(UpdateCachedCompositeStreamSearch.class);

    /**
     * FindById stream view DAO.
     */
    private FindByIdMapper<StreamView> streamViewDAO = context.mock(FindByIdMapper.class, "streamViewDAO");

    /**
     * FindById person DAO.
     */
    private FindByIdMapper<Person> personDAO = context.mock(FindByIdMapper.class, "personDAO");

    /**
     * FindUserStreamSearchById DAO.
     */
    private FindUserStreamSearchById userStreamSearchByIdDAO = context.mock(FindUserStreamSearchById.class);

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * StreamView mock.
     */
    private StreamView streamView = context.mock(StreamView.class);

    /**
     * Person mock.
     */
    private Person currentUser = context.mock(Person.class);

    /**
     * Stream Search mock.
     */
    private StreamSearch streamSearchMock = context.mock(StreamSearch.class, "streamSearchMock");

    /**
     * System under test.
     */
    private StreamSearchUpdater sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new StreamSearchUpdater(updateCachedCompositeStreamSearchMock, streamViewDAO, personDAO,
                userStreamSearchByIdDAO);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));
            }
        });
    }

    /**
     * Test get() method.
     */
    @Test
    public void testGet()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("streamViewId", 1L);
        map.put("name", "nameGoesHere");
        map.put("id", 4L);
        map.put("keywords", "this, that, other");

        context.checking(new Expectations()
        {
            {
                oneOf(principal).getId();
                will(returnValue(5L));

                oneOf(userStreamSearchByIdDAO).execute(with(any(FindUserStreamFilterByIdRequest.class)));
            }
        });

        sut.get(taskHandlerActionContext, map);
        context.assertIsSatisfied();
    }

    /**
     * Test persist method.
     */
    @Test
    public void testPersistStreamSearch()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(userStreamSearchByIdDAO).flush();

                oneOf(updateCachedCompositeStreamSearchMock).execute(streamSearchMock);
            }
        });

        sut.persist(null, null, streamSearchMock);
        context.assertIsSatisfied();
    }

    /**
     * Test setProperties method.
     */
    @Test
    public void testSetProperties()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("streamViewId", 1L);
        map.put("name", "nameGoesHere");
        map.put("id", 4L);
        map.put("keywords", "this, that, other");
        final StreamSearch ss = context.mock(StreamSearch.class);

        context.checking(new Expectations()
        {
            {
                oneOf(ss).setName("nameGoesHere");

                oneOf(ss).getStreamView();
                will(returnValue(streamView));

                oneOf(streamView).getId();
                will(returnValue(9L));

                oneOf(streamViewDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamView));

                oneOf(ss).setStreamView(with(any(StreamView.class)));

                oneOf(ss).setKeywords(with(any(Set.class)));

            }
        });

        sut.setProperties(ss, map);
        context.assertIsSatisfied();
    }

}
