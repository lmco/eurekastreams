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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for person finder.
 * 
 */
public class PersonFinderTest
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
     * Mock.
     */
    private PersonMapper mapper = context.mock(PersonMapper.class);

    /**
     * Mock.
     */
    private Principal user = context.mock(Principal.class);

    /**
     * Sut.
     */
    private PersonFinder sut = new PersonFinder(mapper);

    /**
     * Test.
     */
    @Test
    public void findEntity()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(user).getAccountId();
                will(returnValue("joe"));

                oneOf(mapper).findByAccountId("joe");
            }
        });

        sut.findEntity(user, 3L);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void findEntityAccountId()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(user).getAccountId();
                will(returnValue("joe"));

                oneOf(mapper).findByAccountId("joe");
            }
        });

        sut.findEntity(user, "accountid");
        context.assertIsSatisfied();
    }
}
