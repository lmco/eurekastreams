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

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.OAuthEntryMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link RemoveOAuthTokenExecution}.
 *
 */
public class RemoveOAuthTokenExecutionTest
{
    /**
     * System under test.
     */
    private RemoveOAuthTokenExecution sut;
    
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
     * Instance of OAuth entry mapper injected by spring.
     */
    private final OAuthEntryMapper entryMapper = context.mock(OAuthEntryMapper.class);
    
    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
    
    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new RemoveOAuthTokenExecution(entryMapper);
    }
    
    /**
     * Test successful remove of oauth token. 
     */
    @Test
    public void testSuccessfulRemoval()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("testtoken"));
                
                oneOf(entryMapper).delete("testtoken");
            }
        });
        
        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
