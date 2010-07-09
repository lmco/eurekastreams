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

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.junit.Before;
import org.junit.Test;

/**
 * This class provides the test suite for the {@link KeepSessionAliveExecution} class.
 *
 */
public class KeepSessionAliveExecutionTest
{
    /**
     * System under test.
     */
    private KeepSessionAliveExecution sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new KeepSessionAliveExecution();
    }

    /**
     * Test the only flow through the code path.
     */
    @Test
    public void testExecution()
    {
        ServiceActionContext currentContext = new ServiceActionContext(null, new DefaultPrincipal("", "", 1L));
        sut.execute(currentContext);
    }
}
