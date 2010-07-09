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

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.FindSystemSettings;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetSystemSettingsExecution class.
 * 
 */
public class GetSystemSettingsExecutionTest
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
     * Subject under test.
     */
    private GetSystemSettingsExecution sut = null;

    /**
     * Mocked mapper for the action to look up the SystemSettings.
     */
    private FindSystemSettings systemSettingDAO = context.mock(FindSystemSettings.class);

    /**
     * Mocked mapper for the SystemSettings.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetSystemSettingsExecution(systemSettingDAO);
    }

    /**
     * Check that the action correctly returns the system settings.
     * 
     */
    @Test
    public final void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingDAO).execute(null);
                will(returnValue(systemSettings));
            }
        });

        assertEquals(systemSettings, sut.execute(null));
        context.assertIsSatisfied();
    }
}
