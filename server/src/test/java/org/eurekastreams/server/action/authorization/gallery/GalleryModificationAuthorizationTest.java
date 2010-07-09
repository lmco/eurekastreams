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
package org.eurekastreams.server.action.authorization.gallery;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.authorization.IsOrganizationCoordinatorForAnyOrg;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GalleryModificationAuthorization class.
 * 
 */
public class GalleryModificationAuthorizationTest
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
     * {@link IsOrganizationCoordinatorForAnyOrg}.
     */
    private IsOrganizationCoordinatorForAnyOrg isOrgCoordStrategy = context
            .mock(IsOrganizationCoordinatorForAnyOrg.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * {@link GalleryModificationAuthorization} sut.
     */
    private GalleryModificationAuthorization sut = new GalleryModificationAuthorization(isOrgCoordStrategy);

    /**
     * Test pass.
     */
    @Test
    public void testAuthorizePass()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(5L));

                allowing(isOrgCoordStrategy).execute(5L);
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test fail.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(5L));

                allowing(isOrgCoordStrategy).execute(5L);
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

}
