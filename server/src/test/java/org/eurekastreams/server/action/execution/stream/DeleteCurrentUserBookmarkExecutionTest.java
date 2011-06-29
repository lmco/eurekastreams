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

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteCurrentUserBookmarkExecution. NOTE: This is a mapper test to ensure that changes to person don't break
 * the ORM relationship that will actually delete the item. A test with just mocks won't catch that.
 * 
 */
public class DeleteCurrentUserBookmarkExecutionTest extends MapperTest
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
    private DeleteCurrentUserBookmarkExecution sut = null;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Find person by ID mapper.
     */
    private FindByIdMapper<Person> findByIdMapper;

    /**
     * Setup Fixtures.
     */
    @Before
    public final void setup()
    {
        findByIdMapper = new FindByIdMapper<Person>();
        findByIdMapper.setEntityManager(getEntityManager());

        sut = new DeleteCurrentUserBookmarkExecution(findByIdMapper);
    }

    /**
     * Test executing the action.
     */
    @Test
    public final void executeTest()
    {
        final Long personId = 42L;
        final Long streamId = 1L;

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(actionContext).getParams();
                will(returnValue(streamId));
            }
        });

        Person p = findByIdMapper.execute(new FindByIdRequest("Person", personId));
        Assert.assertEquals(2, p.getBookmarks().size());

        sut.execute(actionContext);

        getEntityManager().clear();

        p = findByIdMapper.execute(new FindByIdRequest("Person", personId));
        Assert.assertEquals(1, p.getBookmarks().size());
        Assert.assertEquals(2L, p.getBookmarks().get(0).getId());

        context.assertIsSatisfied();
    }
}
