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

import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.authorization.CoordinatorAccessAuthorizer;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for CoordinatorAccessAuthorizerExecution.
 */
public class CoordinatorAccessAuthorizerExecutionTest
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
     * Transform entity id from request.
     */
    private RequestTransformer entityIdTransformer = context.mock(RequestTransformer.class);

    /**
     * Permission checker.
     */
    private CoordinatorAccessAuthorizer<Long, Long> entityPermissionsChecker = context
            .mock(CoordinatorAccessAuthorizer.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private CoordinatorAccessAuthorizerExecution sut = new CoordinatorAccessAuthorizerExecution(entityIdTransformer,
            entityPermissionsChecker);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(entityIdTransformer).transform(actionContext);
                will(returnValue("1"));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(0L));

                oneOf(entityPermissionsChecker).hasCoordinatorAccessRecursively(0L, 1L);
                will(returnValue(true));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }

}
