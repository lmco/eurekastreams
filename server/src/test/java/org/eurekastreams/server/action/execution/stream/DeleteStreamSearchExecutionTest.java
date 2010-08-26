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
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteCachedCompositeStreamSearchById;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteStreamSearchExecution.
 * 
 */
public class DeleteStreamSearchExecutionTest
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
     * FindById DAO mock.
     */
    @SuppressWarnings("unchecked")
    private FindByIdMapper findByIdDAO = context.mock(FindByIdMapper.class);

    /**
     * The stream search id the user is trying to delete.
     */
    private final Long streamSearchId = 38271L;

    /**
     * The current user person id.
     */
    private final Long personId = 283717L;

    /**
     * Action Submitter mock.
     */
    private DeleteCachedCompositeStreamSearchById deleteCachedStreamSearchById = context
            .mock(DeleteCachedCompositeStreamSearchById.class);

    /**
     * System under test.
     */
    private DeleteStreamSearchExecution sut;

    /**
     * Set up.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        sut = new DeleteStreamSearchExecution(deleteCachedStreamSearchById, findByIdDAO);
    }

    /**
     * Test perform action.
     */
    @Test
    public void testExecuteWithDelete()
    {
        final Person person = context.mock(Person.class);
        final ArrayList<StreamSearch> viewList = new ArrayList<StreamSearch>(2);
        final StreamSearch ss1 = context.mock(StreamSearch.class, "ss1");
        final StreamSearch ss2 = context.mock(StreamSearch.class, "ss2");
        viewList.add(ss1);
        viewList.add(ss2);

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getStreamSearches();
                will(returnValue(viewList));

                oneOf(ss1).getId();
                will(returnValue(streamSearchId));

                oneOf(findByIdDAO).flush();

                oneOf(person).getId();
                will(returnValue(personId));

                oneOf(deleteCachedStreamSearchById).execute(personId, streamSearchId);
            }
        });

        assertTrue((Boolean) sut.execute(buildPrincipalActionContext()));
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
        final ArrayList<StreamSearch> viewList = new ArrayList<StreamSearch>(2);
        final StreamSearch ss1 = context.mock(StreamSearch.class, "ss1");
        final StreamSearch ss2 = context.mock(StreamSearch.class, "ss2");
        viewList.add(ss1);
        viewList.add(ss2);

        context.checking(new Expectations()
        {
            {
                allowing(person).getId();
                will(returnValue(personId));

                oneOf(findByIdDAO).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getStreamSearches();
                will(returnValue(viewList));

                oneOf(ss1).getId();
                will(returnValue(5L));

                oneOf(ss2).getId();
                will(returnValue(6L));
            }
        });

        assertTrue(!(Boolean) sut.execute(buildPrincipalActionContext()));
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
                return streamSearchId;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

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
