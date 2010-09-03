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
package org.eurekastreams.server.action.execution.opensocial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link OAuthAuthorizeExecution} class.
 * 
 */
@SuppressWarnings("unchecked")
public class OAuthAuthorizeExecutionTest
{
    /**
     * System under test.
     */
    private OAuthAuthorizeExecution sut;

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
     * The mapper used for retrieving OAuthEntries from the db.
     */
    private final DomainMapper<String, OAuthDomainEntry> entryMapper = context.mock(DomainMapper.class);

    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked Principal object.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Setup the sut.
     */
    @Before
    public void setup()
    {
        sut = new OAuthAuthorizeExecution(entryMapper);
    }

    /**
     * Test successful authorization.
     */
    @Test
    public void testSuccessfulAuthorization()
    {
        final OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setCallbackUrlSigned(true);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue("testacctid"));

                oneOf(actionContext).getParams();
                will(returnValue("token"));

                oneOf(entryMapper).execute("token");
                will(returnValue(dto));

                oneOf(entryMapper).execute("token");
                will(returnValue(dto));
            }
        });

        String callbackurl = sut.execute(actionContext);
        assertNotNull(callbackurl);
        assertEquals(dto.isAuthorized(), true);
        assertTrue(dto.getCallbackToken() != null);

        context.assertIsSatisfied();

    }

    /**
     * Test failure authorization.
     */
    @Test(expected = ExecutionException.class)
    public void testAuthorizationFailedTokenNotFound()
    {
        final OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setCallbackUrlSigned(true);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue("testacctid"));

                oneOf(actionContext).getParams();
                will(returnValue("token"));

                oneOf(entryMapper).execute("token");
                will(throwException(new Exception()));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();

    }

    /**
     * Test successful authorization.
     */
    @Test
    public void testSuccessfulAuthorizationWithCallbackUrl()
    {
        final OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setCallbackUrlSigned(true);
        dto.setCallbackUrl("http://example.com/callbackurl");
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue("testacctid"));

                oneOf(actionContext).getParams();
                will(returnValue("token"));

                oneOf(entryMapper).execute("token");
                will(returnValue(dto));

                oneOf(entryMapper).execute("token");
                will(returnValue(dto));
            }
        });

        String callbackurl = sut.execute(actionContext);
        assertNotNull(callbackurl);
        assertEquals(dto.isAuthorized(), true);
        assertTrue(dto.getCallbackToken() != null);

        context.assertIsSatisfied();

    }
}
