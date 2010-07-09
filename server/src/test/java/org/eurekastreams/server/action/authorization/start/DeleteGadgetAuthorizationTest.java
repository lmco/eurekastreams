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
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteGadgetAuthorization} class.
 *
 */
public class DeleteGadgetAuthorizationTest
{
    /**
     * System under test.
     */
    private DeleteGadgetAuthorization sut;

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
     * The mock user information from the session.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Id of a tab to use for testing. Arbitrary.
     */
    private static final Long TAB_ID = 23L;

    /**
     * An id for a gadget to be deleted. Arbitrary.
     */
    private static final Long GADGET_ID = 53L;

    /**
     * The mock mapper to be used by the action.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * TabPermissionMock.
     */
    private TabPermission tabPermission = context.mock(TabPermission.class);

    /**
     * Setup the sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteGadgetAuthorization(tabMapper, tabPermission);
    }

    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test
    public void testPerformSecurityCheck()
    {
        final long tabId = TAB_ID;
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(GADGET_ID);
                will(returnValue(tab));

                oneOf(tab).getId();
                will(returnValue(tabId));

                oneOf(principalMock).getAccountId();
                will(returnValue("testUser"));

                oneOf(tabPermission).canModifyGadgets("testUser", tabId, true);
                will(returnValue(true));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(GADGET_ID, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test(expected = AuthorizationException.class)
    public void testPerformSecurityCheckFailWithExecption()
    {
        final long tabId = TAB_ID;
        final Tab tab = context.mock(Tab.class);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapper).findTabByGadgetId(GADGET_ID);
                will(returnValue(tab));

                oneOf(tab).getId();
                will(returnValue(tabId));

                oneOf(principalMock).getAccountId();
                will(returnValue("testUser"));

                oneOf(tabPermission).canModifyGadgets("testUser", tabId, true);
                will(returnValue(false));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(GADGET_ID, principalMock);
        sut.authorize(currentContext);
        context.assertIsSatisfied();
    }
}
