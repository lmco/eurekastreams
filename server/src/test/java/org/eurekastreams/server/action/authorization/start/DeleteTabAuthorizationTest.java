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
package org.eurekastreams.server.action.authorization.start;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteTabAuthorization} class.
 *
 */
public class DeleteTabAuthorizationTest
{
    /**
     * System under test.
     */
    private DeleteTabAuthorization sut;

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
     * TabPermissionMock.
     */
    private TabPermission tabPermissionMock = context.mock(TabPermission.class);

    /**
     * Mocked instance of the principal object for this test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new DeleteTabAuthorization(tabPermissionMock);
    }

    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test
    public void testPerformSecurityCheck()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue("testUser"));

                oneOf(tabPermissionMock).canDeleteStartPageTab("testUser", tabId, true);
                will(returnValue(true));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(tabId, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Verify that tabPermission is called correctly but authorization fails.
     */
    @Test(expected = AuthorizationException.class)
    public void testFailedPerformSecurityCheck()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue("testUser"));

                oneOf(tabPermissionMock).canDeleteStartPageTab("testUser", tabId, true);
                will(throwException(new AuthorizationException()));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(tabId, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }
}
