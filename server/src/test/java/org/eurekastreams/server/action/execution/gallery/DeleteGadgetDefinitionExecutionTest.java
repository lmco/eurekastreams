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
package org.eurekastreams.server.action.execution.gallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests DeleteGadgetDefinitionExecution.
 */
public class DeleteGadgetDefinitionExecutionTest
{
    /** Test data. */
    private static final String ACTION = "nextAction";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to delete the gadget definition. */
    private final DomainMapper<Long, Void> deleteGadgetDefinitionMapper = context.mock(DomainMapper.class,
            "deleteGadgetDefinitionMapper");

    /** Mapper to get list of affected tab templates. */
    private final DomainMapper<Long, Collection<CompressGadgetZoneRequest>> tabListMapper = context.mock(
            DomainMapper.class, "tabListMapper");

    /** Fixture: gadget definition. */
    private final GadgetDefinition gadgetDef = context.mock(GadgetDefinition.class);

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        DeleteGadgetDefinitionExecution sut = new DeleteGadgetDefinitionExecution(deleteGadgetDefinitionMapper,
                tabListMapper, ACTION);
        final Collection<CompressGadgetZoneRequest> tabs = new ArrayList<CompressGadgetZoneRequest>();
        CompressGadgetZoneRequest rqst1 = new CompressGadgetZoneRequest(4L, 8, 5L);
        tabs.add(rqst1);
        CompressGadgetZoneRequest rqst2 = new CompressGadgetZoneRequest(6L, 9, 7L);
        tabs.add(rqst2);

        context.checking(new Expectations()
        {
            {
                allowing(tabListMapper).execute(9L);
                will(returnValue(tabs));

                oneOf(deleteGadgetDefinitionMapper).execute(9L);
            }
        });

        final TaskHandlerActionContext<ActionContext> wrapperContext = TestContextCreator
                .createTaskHandlerAsyncContext(9L);
        sut.execute(wrapperContext);

        context.assertIsSatisfied();
        assertEquals(2, wrapperContext.getUserActionRequests().size());
        UserActionRequest asyncRqst = wrapperContext.getUserActionRequests().get(0);
        assertEquals(ACTION, asyncRqst.getActionKey());
        assertTrue(IsEqualInternally.areEqualInternally(rqst1, asyncRqst.getParams()));

        asyncRqst = wrapperContext.getUserActionRequests().get(1);
        assertEquals(ACTION, asyncRqst.getActionKey());
        assertTrue(IsEqualInternally.areEqualInternally(rqst2, asyncRqst.getParams()));
    }
}
