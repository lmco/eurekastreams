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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the updater action execution.
 */
public class UpdateSettingsExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: updater. */
    private SettingsUpdater updater1 = context.mock(SettingsUpdater.class, "updater1");

    /** Fixture: updater2. */
    private SettingsUpdater updater2 = context.mock(SettingsUpdater.class, "updater2");

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final HashMap<String, Serializable> settings = context.mock(HashMap.class, "settings");
        final Principal principal = context.mock(Principal.class);
        final PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);
        context.checking(new Expectations()
        {
            {
                allowing(actionCtx).getPrincipal();
                will(returnValue(principal));
                allowing(actionCtx).getParams();
                will(returnValue(settings));

                oneOf(updater1).update(settings, principal);
                oneOf(updater2).update(settings, principal);
            }
        });

        UpdateSettingsExecution sut = new UpdateSettingsExecution(Arrays.asList(updater1, updater2));

        sut.execute(actionCtx);

        context.assertIsSatisfied();
    }
}
