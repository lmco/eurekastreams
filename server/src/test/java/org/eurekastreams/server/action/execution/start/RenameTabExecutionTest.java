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
package org.eurekastreams.server.action.execution.start;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RenameTabExecution class.
 * 
 */
public class RenameTabExecutionTest
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
     * {@link TabMapper}.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link RenameTabRequest}.
     */
    private RenameTabRequest request = context.mock(RenameTabRequest.class);

    /**
     * {@link Tab}.
     */
    private Tab tab = context.mock(Tab.class);

    /**
     * Tab name for tests.
     */
    private String tabName = "tabName";

    /**
     * Tab id used for tests.
     */
    private Long tabId = 1L;

    /**
     * System under test.
     */
    private RenameTabExecution sut = new RenameTabExecution(tabMapper);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getTabId();
                will(returnValue(tabId));

                allowing(tabMapper).findById(tabId);
                will(returnValue(tab));

                allowing(request).getTabName();
                will(returnValue(tabName));

                allowing(tab).setTabName(tabName);

                allowing(tabMapper).flush();

            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

}
