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

import java.util.Date;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for AcceptTermsOfServiceExecution class.
 *
 */
public class AcceptTermsOfServiceExecutionTest
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
     * The mapper.
     */
    private PersonMapper mapperMock = context.mock(PersonMapper.class);

    /**
     * The system under test.
     */
    private AcceptTermsOfServiceExecution sut = new AcceptTermsOfServiceExecution(mapperMock);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal} mock.
     */
    private Principal actionContextPrincipal = context.mock(Principal.class);

    /**
     * Test performing the action.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformAction() throws Exception
    {
        final ExtendedUserDetails userMock = context.mock(ExtendedUserDetails.class);

        final String expectedUserName = "user";

        final Person expectedPerson = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                // get user's account id
                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(expectedUserName));

                // look up person object.
                oneOf(mapperMock).findByAccountId(expectedUserName);
                will(returnValue(expectedPerson));

                // Can't reliably verify the last accepted time is set to now.
                oneOf(expectedPerson).setLastAcceptedTermsOfService(with(any(Date.class)));

                oneOf(mapperMock).flush();
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

}
