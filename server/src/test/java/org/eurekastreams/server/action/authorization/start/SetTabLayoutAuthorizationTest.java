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
import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;
import org.eurekastreams.server.domain.Layout;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetTabLayoutAuthorization} class.
 *
 */
public class SetTabLayoutAuthorizationTest
{
    /**
     * System under test.
     */
    private SetTabLayoutAuthorization sut;

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
     * Mocked instance of the {@link TabPermission} class.
     */
    private TabPermission tabPermissionMock = context.mock(TabPermission.class);

    /**
     * Mocked instance of the {@link Principal} object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Test account id for this instance.
     */
    private static final String TEST_ACCOUNTID = "testuser";

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetTabLayoutAuthorization(tabPermissionMock);
    }

    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test
    public void testAuthorizeCheck()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNTID));

                oneOf(tabPermissionMock).canChangeTabLayout(TEST_ACCOUNTID, tabId, true);
                will(returnValue(true));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(Layout.TWOCOLUMN, tabId);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Verify that tabPermission fails in this method.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailureExceptionCheck()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNTID));

                oneOf(tabPermissionMock).canChangeTabLayout(TEST_ACCOUNTID, tabId, true);
                will(throwException(new AuthorizationException()));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(Layout.TWOCOLUMN, tabId);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Verify that tabPermission fails in this method.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailureCheck()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNTID));

                oneOf(tabPermissionMock).canChangeTabLayout(TEST_ACCOUNTID, tabId, true);
                will(returnValue(false));
            }
        });

        SetTabLayoutRequest currentRequest = new SetTabLayoutRequest(Layout.TWOCOLUMN, tabId);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }
}
