/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.EntityIdentifier;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests GetEmailContactForStreamExecution.
 */
public class GetEmailAddressForStreamExecutionTest
{
    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Builds the token content. */
    private final TokenContentFormatter tokenContentFormatter = mockery.mock(TokenContentFormatter.class);

    /** Builds the recipient email address with a token. */
    private final TokenContentEmailAddressBuilder tokenAddressBuilder = mockery
            .mock(TokenContentEmailAddressBuilder.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final String address = "system+token@eurekastreams.org";
        final long userId = 8L;
        final long groupId = 6L;
        final String tokenContent = "stuffToGoInTheToken";

        ExecutionStrategy<PrincipalActionContext> sut = new GetEmailAddressForStreamExecution(tokenContentFormatter,
                tokenAddressBuilder);

        mockery.checking(new Expectations()
        {
            {
                oneOf(tokenContentFormatter).buildForStream(EntityType.GROUP, groupId);
                will(returnValue(tokenContent));

                oneOf(tokenAddressBuilder).build(tokenContent, userId);
                will(returnValue(address));
            }
        });

        Serializable result = sut.execute(TestContextCreator.createPrincipalActionContext(new EntityIdentifier(
                EntityType.GROUP, groupId), null, userId));
        mockery.assertIsSatisfied();
        assertEquals(address, result);
    }
}
