/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetCurrentUserFollowingStatusExecution.
 */
@SuppressWarnings("unchecked")
public class GetFollowingExecutionTest
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

    /** Fixture: action context. */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /** SUT. */
    private GetFollowingExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
    }
    
    /**
     * TODO: Write real tests.
     */
    @Test
    public void tempTest()
    {
        Assert.assertTrue(true);
    }
}
