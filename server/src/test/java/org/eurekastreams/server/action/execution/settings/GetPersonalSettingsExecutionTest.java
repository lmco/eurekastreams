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
package org.eurekastreams.server.action.execution.settings;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import java.util.Arrays;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the action execution.
 */
public class GetPersonalSettingsExecutionTest
{
    /** Test data. */
    static final long USER_ID = 987L;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final String key1a = "key1a";
        final String key1b = "key1b";
        final String key2a = "key2a";
        final String key2b = "key2b";
        final Object o1a = new Object();
        final Object o1b = new Object();
        final Object o2a = new Object();
        final Object o2b = new Object();

        SettingsRetriever r1 = new SettingsRetriever()
        {
            public void retrieve(final long inId, final RetrieveSettingsResponse inResponse)
            {
                assertEquals(USER_ID, inId);
                inResponse.getSettings().put(key1a, o1a);
                inResponse.getSupport().put(key1b, o1b);
            }
        };
        SettingsRetriever r2 = new SettingsRetriever()
        {
            public void retrieve(final long inId, final RetrieveSettingsResponse inResponse)
            {
                assertEquals(USER_ID, inId);
                inResponse.getSettings().put(key2a, o2a);
                inResponse.getSupport().put(key2b, o2b);
            }
        };

        final Principal principal = context.mock(Principal.class);
        final PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);
        context.checking(new Expectations()
        {
            {
                allowing(actionCtx).getPrincipal();
                will(returnValue(principal));
                allowing(principal).getId();
                will(returnValue(USER_ID));
            }
        });

        GetPersonalSettingsExecution sut = new GetPersonalSettingsExecution(Arrays.asList(r1, r2));

        RetrieveSettingsResponse response = (RetrieveSettingsResponse) sut.execute(actionCtx);
        context.assertIsSatisfied();

        assertEquals(2, response.getSettings().size());
        assertSame(o1a, response.getSettings().get(key1a));
        assertSame(o2a, response.getSettings().get(key2a));
        assertEquals(2, response.getSupport().size());
        assertSame(o1b, response.getSupport().get(key1b));
        assertSame(o2b, response.getSupport().get(key2b));
    }


}
