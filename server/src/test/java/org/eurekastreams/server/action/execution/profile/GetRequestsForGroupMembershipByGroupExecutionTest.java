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
package org.eurekastreams.server.action.execution.profile;

import static junit.framework.Assert.assertSame;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.db.GetRequestsForGroupMembershipByGroup;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests the action execution.
 */
public class GetRequestsForGroupMembershipByGroupExecutionTest
{
    /** Test data. */
    private static final int START_INDEX = 20;

    /** Test data. */
    private static final int END_INDEX = 29;

    /** Test data. */
    private static final long GROUP_ID = 9988L;

    /** Test data. */
    private static final long PERSON_ID = 222L;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper for people. */
    private GetPeopleByIds peopleMapper = context.mock(GetPeopleByIds.class);

    /** Mapper for list of people. */
    private GetRequestsForGroupMembershipByGroup requestMapper =
            context.mock(GetRequestsForGroupMembershipByGroup.class);

    /** Fixture: person. */
    private PersonModelView person1 = context.mock(PersonModelView.class, "person1");

    /** Fixture: request. */
    private GetRequestForGroupMembershipRequest request;

    /** Fixture: action context. */
    private PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);

    /** SUT. */
    private GetRequestsForGroupMembershipByGroupExecution sut;


    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetRequestsForGroupMembershipByGroupExecution(requestMapper, peopleMapper);
        request = new GetRequestForGroupMembershipRequest(GROUP_ID, START_INDEX, END_INDEX);

        context.checking(new Expectations()
        {
            {
                allowing(actionCtx).getParams();
                will(returnValue(request));
            }
        });
    }

    /**
     * Tests getting requests.
     */
    @Test
    public void testExecute()
    {
        final int total = 21;
        final List<Long> ids = Collections.singletonList(PERSON_ID);
        final PagedSet<Long> idsSet = new PagedSet<Long>(START_INDEX, START_INDEX + 0, total, ids);

        context.checking(new Expectations()
        {
            {
                oneOf(requestMapper).execute(with(equalInternally(request)));
                will(returnValue(idsSet));

                oneOf(peopleMapper).execute(ids);
                will(returnValue(Collections.singletonList(person1)));
            }
        });

        PagedSet<PersonModelView> results = (PagedSet<PersonModelView>) sut.execute(actionCtx);

        context.assertIsSatisfied();
        assertEquals(START_INDEX, results.getFromIndex());
        assertEquals(START_INDEX + 0, results.getToIndex());
        assertEquals(total, results.getTotal());
        assertEquals(1, results.getPagedSet().size());
        assertSame(person1, results.getPagedSet().get(0));
    }

    /**
     * Tests getting requests.
     */
    @Test
    public void testExecuteNoResults()
    {
        final int total = 20;
        final PagedSet<Long> idsSet = new PagedSet<Long>(START_INDEX, START_INDEX + 0, total, Collections.EMPTY_LIST);

        context.checking(new Expectations()
        {
            {
                oneOf(requestMapper).execute(with(equalInternally(request)));
                will(returnValue(idsSet));
            }
        });

        PagedSet<PersonModelView> results = (PagedSet<PersonModelView>) sut.execute(actionCtx);

        context.assertIsSatisfied();
        assertEquals(START_INDEX, results.getFromIndex());
        assertEquals(START_INDEX + 0, results.getToIndex());
        assertEquals(total, results.getTotal());
        assertTrue(results.getPagedSet().isEmpty());
    }

}
