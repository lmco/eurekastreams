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
package org.eurekastreams.server.action.authorization.opensocial;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.opensocial.GetGadgetsByGadgetDefAndConsumerKey;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GetGadgetsByGadgetDefAndConsumerKeyRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link OAuthRequestAuthorization}.
 * 
 */
public class OAuthRequestAuthorizationTest
{
    /**
     * System under test.
     */
    private OAuthRequestAuthorization sut;

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
     * Mocked instnace of the {@link GetGadgetsByGadgetDefAndConsumerKey} mapper.
     */
    private GetGadgetsByGadgetDefAndConsumerKey verifyMapper = context.mock(GetGadgetsByGadgetDefAndConsumerKey.class); 

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance oft he principal object.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new OAuthRequestAuthorization(verifyMapper);
    }

    /**
     * Test successful authorization.
     */
    @Test
    public void testSuccessfulAuthorization()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("testconsumerkey"));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(1L));

                oneOf(verifyMapper).execute(with(any(GetGadgetsByGadgetDefAndConsumerKeyRequest.class)));
                will(returnValue(1L));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test failing authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testFailingAuthorization()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("testconsumerkey"));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(1L));

                oneOf(verifyMapper).execute(with(any(GetGadgetsByGadgetDefAndConsumerKeyRequest.class)));
                will(returnValue(0L));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
