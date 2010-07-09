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
package org.eurekastreams.server.action.authorization;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for CurrentUserGroupCoordinatorRecursivelyAuthorization class.
 * 
 */
public class CurrentUserGroupCoordinatorRecursivelyAuthorizationTest
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
     * Transform the request.
     */
    private RequestTransformer groupIdtransformer = context.mock(RequestTransformer.class);

    /**
     * Mapper to get all the coordinators of an group, traversing up the tree.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupPermissionsChecker = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * User id for tests.
     */
    private Long userId = 6L;

    /**
     * System under test.
     */
    private CurrentUserGroupCoordinatorRecursivelyAuthorization sut = // \n
    new CurrentUserGroupCoordinatorRecursivelyAuthorization(groupIdtransformer, groupPermissionsChecker);

    /**
     * Test.
     */
    @Test
    public void authorizePass()
    {
        context.checking(new Expectations()
        {
            {
                allowing(groupIdtransformer).transform(actionContext);
                will(returnValue("5"));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(userId));

                allowing(groupPermissionsChecker).hasGroupCoordinatorAccessRecursively(userId, 5L);
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
    public void authorizeFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(groupIdtransformer).transform(actionContext);
                will(returnValue("5"));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(userId));

                allowing(groupPermissionsChecker).hasGroupCoordinatorAccessRecursively(userId, 5L);
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

}
