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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for UpdateSystemSettingsExecution.
 *
 */
public class UpdateSystemSettingsExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link UpdateMapper}.
     *
     */
    private UpdateMapper<SystemSettings> updateMapper = context.mock(UpdateMapper.class);

    /**
     * {@link FindSystemSettings}.
     *
     */
    private DomainMapper<MapperRequest, SystemSettings> finder = context.mock(DomainMapper.class);

    /**
     * {@link UpdaterStrategy}.
     */
    private UpdaterStrategy updater = context.mock(UpdaterStrategy.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * System under test.
     */
    private UpdateSystemSettingsExecution sut;

    /**
     * Set up before each test.
     */
    @Before
    public void setup()
    {
        sut = new UpdateSystemSettingsExecution(finder, updater, updateMapper);
    }

    /**
     * Test method.
     */
    @Test
    public void testExecute()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));

                allowing(finder).execute(null);
                will(returnValue(systemSettings));

                allowing(updater).setProperties(systemSettings, formData);

                allowing(updateMapper).execute(null);
            }
        });

        assertEquals(systemSettings, sut.execute(actionContext));
        context.assertIsSatisfied();

    }
}
