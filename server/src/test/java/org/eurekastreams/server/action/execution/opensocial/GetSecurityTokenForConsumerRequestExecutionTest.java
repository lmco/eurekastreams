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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.response.opensocial.SecurityTokenResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetSecurityTokenForConsumerRequestExecution}.
 *
 */
public class GetSecurityTokenForConsumerRequestExecutionTest
{
    /**
     * System under test.
     */
    private GetSecurityTokenForConsumerRequestExecution sut;

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
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
    
    /**
     * Mocked Principal object.
     */
    private Principal principal = context.mock(Principal.class);
    
    /**
     * Test domain.
     */
    private static final String SECURITY_TOKEN_DOMAIN = "testdomain";
    
    /**
     * Test container.
     */
    private static final String SECURITY_TOKEN_CONTAINER = "testcontainer";
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
       sut = new GetSecurityTokenForConsumerRequestExecution(SECURITY_TOKEN_DOMAIN, SECURITY_TOKEN_CONTAINER); 
    }
    
    /**
     * Test successful securit token.
     */
    @Test
    public void testSecurityTokenGeneration()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));
                
                oneOf(principal).getOpenSocialId();
                will(returnValue("testosid"));

                oneOf(actionContext).getParams();
                will(returnValue("consumerKey"));
            }
        });
        
        SecurityTokenResponse results = sut.execute(actionContext);
        
        assertEquals(results.getSecurityToken().getContainer(), SECURITY_TOKEN_CONTAINER);
        assertEquals(results.getSecurityToken().getDomain(), SECURITY_TOKEN_DOMAIN);
        context.assertIsSatisfied();
    }
}
