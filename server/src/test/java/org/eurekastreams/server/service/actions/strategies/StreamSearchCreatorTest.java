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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.AddCachedCompositeStreamSearch;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for StreamSearchCreator.
 *
 */
@SuppressWarnings("unchecked")
public class StreamSearchCreatorTest
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
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * The add cached composite stream search mapper.
     */
    private AddCachedCompositeStreamSearch addCachedCompositeStreamSearchMock = context
            .mock(AddCachedCompositeStreamSearch.class);

    /**
     * FindById stream view DAO.
     */
    private FindByIdMapper<StreamView> streamViewDAO = context.mock(FindByIdMapper.class, "streamViewDAO");

    /**
     * FindById person DAO.
     */
    private FindByIdMapper<Person> personDAO = context.mock(FindByIdMapper.class, "personDAO");

    /**
     * StreamView mock.
     */
    private StreamView streamView = context.mock(StreamView.class);

    /**
     * System under test.
     */
    private StreamSearchCreator sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new StreamSearchCreator(addCachedCompositeStreamSearchMock, streamViewDAO, personDAO);
    }

    /**
     * Test get() method.
     */
    @Test
    public void testGet()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();

        map.put("name", "nameGoesHere");
        map.put("streamViewId", 5L);
        map.put("keywords", "this, that, other");

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                oneOf(streamViewDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamView));
            }
        });

        StreamSearch result = sut.get(taskHandlerActionContext, map);
        context.assertIsSatisfied();

        assertNotNull(result);
        assertEquals("nameGoesHere", result.getName());
        assertEquals(3, result.getKeywords().size());
        assertTrue(result.getKeywords().contains("that"));
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
        final Person person = context.mock(Person.class);
        final long personId = 2L;
        final StreamSearch search = context.mock(StreamSearch.class);
        final List<StreamSearch> searches = new ArrayList<StreamSearch>();

        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(personDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                oneOf(person).getStreamSearches();
                will(returnValue(searches));

                oneOf(personDAO).flush();

                oneOf(addCachedCompositeStreamSearchMock).execute(personId, search);
            }
        });

        sut.persist(taskHandlerActionContext, null, search);
        assertEquals(1, searches.size());
        context.assertIsSatisfied();

    }

    /**
     * Stubbed out methods.
     */
    @Test
    public void testSetProperties()
    {
        sut.setProperties(null, null);
        assertTrue(true);
    }

}
