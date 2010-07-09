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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteCachedCompositeStreamById;
import org.eurekastreams.server.persistence.mappers.stream.DeleteStreamViewAndRelatedSearches;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteStreamViewExecution class.
 *
 */
public class DeleteStreamViewExecutionTest
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
     * Person id.
     */
    private final Long personId = 82717L;

    /**
     * Stream view id.
     */
    private final Long streamViewId = 8271L;

    /**
     * FindById DAO mock.
     */
    @SuppressWarnings("unchecked")
    private FindByIdMapper findByIdDAO = context.mock(FindByIdMapper.class);

    /**
     * Delete stream view from cache mock.
     */
    private DeleteCachedCompositeStreamById deleteCachedCompositeStreamById = context
            .mock(DeleteCachedCompositeStreamById.class);
    
    /**
     * Delete streamview mock.
     */
    private DeleteStreamViewAndRelatedSearches deleteStreamview = 
        context.mock(DeleteStreamViewAndRelatedSearches.class);

    /**
     * System under test.
     */
    private DeleteStreamViewExecution sut;

    /**
     * Set up.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        sut = new DeleteStreamViewExecution(deleteCachedCompositeStreamById, findByIdDAO, deleteStreamview);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecuteWithDelete()
    {
        final Person person = context.mock(Person.class);
        final ArrayList<StreamView> viewList = new ArrayList<StreamView>(2);
        final StreamView sv1 = context.mock(StreamView.class, "sv1");
        final StreamView sv2 = context.mock(StreamView.class, "sv2");

        viewList.add(sv1);
        viewList.add(sv2);

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getStreamViewDefinitions();
                will(returnValue(viewList));

                oneOf(sv1).getId();
                will(returnValue(streamViewId));

                oneOf(sv1).getType();
                will(returnValue(null));

                oneOf(findByIdDAO).flush();

                oneOf(deleteCachedCompositeStreamById).execute(personId, streamViewId);
                
                oneOf(deleteStreamview);
            }
        });

        assertTrue(sut.execute(buildPrincipalActionContext()));
        assertEquals(1, viewList.size());
        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test(expected = AuthorizationException.class)
    public void testExecuteWithDeleteReadOnly()
    {
        final Person person = context.mock(Person.class);
        final ArrayList<StreamView> viewList = new ArrayList<StreamView>(2);
        final StreamView sv1 = context.mock(StreamView.class, "sv1");
        final StreamView sv2 = context.mock(StreamView.class, "sv2");
        viewList.add(sv1);
        viewList.add(sv2);

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getStreamViewDefinitions();
                will(returnValue(viewList));

                oneOf(sv1).getId();
                will(returnValue(streamViewId));

                oneOf(sv1).getType();
                will(returnValue(StreamView.Type.PARENTORG));
            }
        });

        assertTrue(sut.execute(buildPrincipalActionContext()));
        assertEquals(1, viewList.size());
        context.assertIsSatisfied();
    }

    /**
     * Test perform action.
     */
    @Test
    public void testExecuteWithNoDelete()
    {
        final Person person = context.mock(Person.class);
        final ArrayList<StreamView> viewList = new ArrayList<StreamView>(2);
        final StreamView sv1 = context.mock(StreamView.class, "sv1");
        final StreamView sv2 = context.mock(StreamView.class, "sv2");
        viewList.add(sv1);
        viewList.add(sv2);

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getStreamViewDefinitions();
                will(returnValue(viewList));

                oneOf(sv1).getId();
                will(returnValue(5L));

                oneOf(sv2).getId();
                will(returnValue(6L));
            }
        });

        assertTrue(!sut.execute(buildPrincipalActionContext()));
        assertEquals(2, viewList.size());
        context.assertIsSatisfied();
    }

    /**
     * Build a principal action context for testing the person id and stream search id.
     *
     * @return a principal action context for testing with the person id and stream search id.
     */
    private PrincipalActionContext buildPrincipalActionContext()
    {
        return new PrincipalActionContext()
        {
            private static final long serialVersionUID = 609827472203519793L;

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public Serializable getParams()
            {
                return streamViewId;
            }

            @Override
            public Principal getPrincipal()
            {
                return new Principal()
                {
                    private static final long serialVersionUID = -7466938458336643832L;

                    @Override
                    public String getAccountId()
                    {
                        return null;
                    }

                    @Override
                    public Long getId()
                    {
                        return personId;
                    }

                    @Override
                    public String getOpenSocialId()
                    {
                        return null;
                    }
                };
            }
        };
    }
}
