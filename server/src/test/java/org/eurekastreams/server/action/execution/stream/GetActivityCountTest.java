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
package org.eurekastreams.server.action.execution.stream;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the counting action.
 * 
 */
public class GetActivityCountTest
{
    /**
     * System under test.
     */
    private GetActivityCount sut;

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
     * Exe mock.
     */
    private GetActivityIdsByJson getIdsMock = context.mock(GetActivityIdsByJson.class);

    /**
     * Test. Put in 3 things, get a count of 3.
     */
    @Test
    public void execute()
    {
        final List<Long> activities = Arrays.asList(1L, 2L, 3L);

        sut = new GetActivityCount(getIdsMock);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final String request = "{}";
        final Long userId = 1L;
        
        final Principal principle = context.mock(Principal.class); 
        
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));
                
                oneOf(actionContext).getPrincipal();
                will(returnValue(principle));
                
                oneOf(principle).getId();
                will(returnValue(userId));
                
                oneOf(getIdsMock).execute(request, userId);
                will(returnValue(activities));
            }
        });

        Integer result = (Integer) sut.execute(actionContext);
        Assert.assertEquals(new Integer(3), result);
        
        context.assertIsSatisfied();
    }
}
