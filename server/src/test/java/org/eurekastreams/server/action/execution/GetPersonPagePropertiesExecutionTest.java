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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.cache.GetPersonPagePropertiesById;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetPersonPagePropertiesExecution.
 * 
 */
public class GetPersonPagePropertiesExecutionTest
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
     * An arbitrary person id to use for testing.
     */
    private static final long PERSON_ID = 123L;

    /**
     * {@link GetPersonPagePropertiesById}.
     */
    private GetPersonPagePropertiesById mapper = context.mock(GetPersonPagePropertiesById.class);

    /**
     * {@link PersonPagePropertiesDTO}.
     */
    private PersonPagePropertiesDTO ppp = context.mock(PersonPagePropertiesDTO.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal} mock.
     */
    private Principal actionContextPrincipal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private GetPersonPagePropertiesExecution sut = new GetPersonPagePropertiesExecution(mapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getId();
                will(returnValue(PERSON_ID));

                oneOf(mapper).execute(PERSON_ID);
                will(returnValue(ppp));
            }
        });

        assertEquals(ppp, sut.execute(actionContext));

        context.assertIsSatisfied();
    }
}
