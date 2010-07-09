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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetStreamScopeById;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.AddCachedCompositeStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for StreamViewCreator.
 *
 */
public class StreamViewCreatorTest
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
     * The add cached composite stream mock.
     */
    private AddCachedCompositeStream addCachedCompositeStream = context.mock(AddCachedCompositeStream.class);

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
     * FindById DAO.
     */
    @SuppressWarnings("unchecked")
    private FindByIdMapper findByIdDAO = context.mock(FindByIdMapper.class);

    /**
     * CompositeStreamCreator mock.
     */
    private GetStreamScopeById getStreamScopeById = context.mock(GetStreamScopeById.class);

    /**
     * System under test.
     */
    private StreamViewCreator sut;

    /**
     * Setup.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        sut = new StreamViewCreator(addCachedCompositeStream, findByIdDAO, getStreamScopeById);

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
    @Test
    public void testGet()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        ArrayList<StreamScope> scopes = new ArrayList<StreamScope>(1);
        scopes.add(new StreamScope(ScopeType.PERSON, "accountId", 5L));

        map.put("name", "nameGoesHere");
        map.put("scopes", scopes);

        StreamView result = sut.get(taskHandlerActionContext, map);

        assertNotNull(result);
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
        final StreamView viewMock = context.mock(StreamView.class);
        final Person person = context.mock(Person.class);
        final long personId = 2L;

        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        ArrayList<StreamScope> scopes = new ArrayList<StreamScope>(1);
        scopes.add(new StreamScope(ScopeType.PERSON, "accountId"));

        map.put("scopes", scopes);

        context.checking(new Expectations()
        {
            {
                allowing(principal).getId();
                will(returnValue(personId));

                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                oneOf(person).getStreamViewDefinitions();

                oneOf(findByIdDAO).flush();

                oneOf(addCachedCompositeStream).execute(personId, viewMock);
            }
        });

        sut.persist(taskHandlerActionContext, map, viewMock);
        context.assertIsSatisfied();

    }

    /**
     * Stubbed out methods.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSetProperties()
    {
        final StreamView compositeStream = new StreamView();
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        final ArrayList<StreamScope> scopes = new ArrayList<StreamScope>(1);
        scopes.add(new StreamScope(ScopeType.PERSON, "accountId", 5L));
        final HashSet<StreamScope> scopeSet = new HashSet<StreamScope>(1);
        scopeSet.addAll(scopes);

        map.put("name", "theName");
        map.put("scopes", scopes);

        context.checking(new Expectations()
        {
            {
                oneOf(getStreamScopeById).execute(with(any(HashSet.class)));
                will(returnValue(scopeSet));
            }
        });

        sut.setProperties(compositeStream, map);
        assertEquals("theName", compositeStream.getName());
        List<StreamScope> resultScopes = new ArrayList(1);
        resultScopes.addAll(compositeStream.getIncludedScopes());
        assertEquals(5L, resultScopes.get(0).getId());

    }

}
