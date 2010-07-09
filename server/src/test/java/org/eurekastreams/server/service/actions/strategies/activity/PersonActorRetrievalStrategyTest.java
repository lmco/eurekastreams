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
package org.eurekastreams.server.service.actions.strategies.activity;

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the PersonActorRetrievalStrategy class.
 *
 */
public class PersonActorRetrievalStrategyTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Local instance of ExtendedUserDetails to be used in tests.
     */
    private Principal userMock = context.mock(Principal.class);

    /**
     * Local instance of System under test.
     */
    private PersonActorRetrievalStrategy sut;

    /**
     * Test account id to use.
     */
    private static final String ACCOUNT_ID = "ntid";

    /**
     * Test user id to use.
     */
    private static final Long USER_ID = 1L;

    /**
     * Prepare the sut before testing.
     */
    @Before
    public void setUp()
    {
        sut = new PersonActorRetrievalStrategy();
    }

    /**
     * Test that the entity type returned is what is expected.
     */
    @Test
    public void testGetEntity()
    {
        EntityType results = sut.getEntityType();
        assertEquals(results, EntityType.PERSON);
    }

    /**
     * Test retrieving the Actor's account id.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testGetActorAccountId() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(userMock).getAccountId();
                will(returnValue(ACCOUNT_ID));
            }
        });

        String results = sut.getActorAccountId(userMock, new ActivityDTO());
        assertEquals(results, ACCOUNT_ID);
        context.assertIsSatisfied();
    }

    /**
     * Test retrieving the Actor's user id.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testGetActorId() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(userMock).getId();
                will(returnValue(USER_ID));
            }
        });

        Long results = sut.getActorId(userMock, new ActivityDTO());
        assertEquals(results, USER_ID);
        context.assertIsSatisfied();
    }
}
