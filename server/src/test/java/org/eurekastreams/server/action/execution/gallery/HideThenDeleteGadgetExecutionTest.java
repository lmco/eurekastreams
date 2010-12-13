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
package org.eurekastreams.server.action.execution.gallery;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.persistence.GalleryItemMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests HideThenDeleteGadgetExecution.
 */
public class HideThenDeleteGadgetExecutionTest
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

    /** Fixture: mapper. */
    private final GalleryItemMapper<GadgetDefinition> mapper = context.mock(GalleryItemMapper.class);

    /** Fixture: gadget definition. */
    private final GadgetDefinition gadgetDef = context.mock(GadgetDefinition.class);

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        HideThenDeleteGadgetExecution sut = new HideThenDeleteGadgetExecution(mapper, ACTION);

        context.checking(new Expectations()
        {
            {
                allowing(mapper).findById(9L);
                will(returnValue(gadgetDef));

                oneOf(gadgetDef).setShowInGallery(false);
            }
        });

        AsyncActionContext innerContext = new AsyncActionContext(9L);
        TaskHandlerActionContext<ActionContext> wrapperContext = new TaskHandlerActionContext<ActionContext>(
                innerContext, new ArrayList<UserActionRequest>());

        sut.execute(wrapperContext);

        context.assertIsSatisfied();
        assertEquals(1, wrapperContext.getUserActionRequests().size());
        UserActionRequest asyncRqst = wrapperContext.getUserActionRequests().get(0);
        assertEquals(9L, asyncRqst.getParams());
        assertEquals(ACTION, asyncRqst.getActionKey());
    }
}
