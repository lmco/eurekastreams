/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the UpdateGadgetDefinitionCountExecution.
 */
public class UpdateGadgetDefinitionCountExecutionTest
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
     * The mock mapper to be used by the action.
     */
    private GadgetDefinitionMapper mapper = context.mock(GadgetDefinitionMapper.class);

    /**
     * Subject under test.
     */
    private UpdateGadgetDefinitionCountExecution sut = null;

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new UpdateGadgetDefinitionCountExecution(mapper);
    }

    /**
     * Testing the action.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).refreshGadgetDefinitionUserCounts();
            }
        });

        sut.execute(new ActionContext()
        {
            private static final long serialVersionUID = 5336352843040807883L;

            @Override
            public Serializable getParams()
            {
                return null;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

            }
        });
        context.assertIsSatisfied();
    }
}
