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
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RenameTabAuthorization class.
 * 
 */
public class RenameTabAuthorizationTest
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
     * {@link TabPermission}.
     */
    private TabPermission tabPermission = context.mock(TabPermission.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * {@link RenameTabRequest}.
     */
    private RenameTabRequest request = context.mock(RenameTabRequest.class);

    /**
     * Account id for tests.
     */
    private String accountId = "accountid";

    /**
     * Tab id used for tests.
     */
    private Long tabId = 1L;

    /**
     * System under test.
     */
    private RenameTabAuthorization sut = new RenameTabAuthorization(tabPermission);

    /**
     * Test.
     */
    @Test
    public void testAuthorizePass()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(accountId));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getTabId();
                will(returnValue(tabId));

                allowing(tabPermission).canRenameTab(accountId, tabId, false);
                will(returnValue(true));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(accountId));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getTabId();
                will(returnValue(tabId));

                allowing(tabPermission).canRenameTab(accountId, tabId, false);
                will(returnValue(false));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
