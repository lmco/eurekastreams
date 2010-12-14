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
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CompressGadgetZoneExecution.
 */
public class CompressGadgetZoneExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Mapper to retrieve the gadgets for the zone. */
    private final DomainMapper<CompressGadgetZoneRequest, List<Gadget>> gadgetMapper = context.mock(
            DomainMapper.class, "gadgetMapper");

    /** Fixture: Mapper to refresh user's start page data in cache. */
    private final DomainMapper<Long, Object> pageMapper = context.mock(DomainMapper.class, "pageMapper");

    /** Fixture: gadget. */
    private Gadget gadget1;

    /** Fixture: gadget. */
    private Gadget gadget2;

    /** Fixture: gadget. */
    private Gadget gadget3;

    /** SUT. */
    private CompressGadgetZoneExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new CompressGadgetZoneExecution(gadgetMapper, pageMapper);
        gadget1 = new Gadget(new GadgetDefinition(null, null), 9, 3, null);
        gadget2 = new Gadget(new GadgetDefinition(null, null), 9, 5, null);
        gadget3 = new Gadget(new GadgetDefinition(null, null), 9, 7, null);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoOwner()
    {
        final CompressGadgetZoneRequest request = new CompressGadgetZoneRequest(8L, 9, null);
        context.checking(new Expectations()
        {
            {
                allowing(gadgetMapper).execute(request);
                will(returnValue(Arrays.asList(gadget1, gadget2, gadget3)));
            }
        });

        AsyncActionContext innerContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> wrapperContext = new TaskHandlerActionContext<ActionContext>(
                innerContext, new ArrayList<UserActionRequest>());

        sut.execute(wrapperContext);

        context.assertIsSatisfied();
        assertEquals(0, gadget1.getZoneIndex());
        assertEquals(1, gadget2.getZoneIndex());
        assertEquals(2, gadget3.getZoneIndex());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteWithOwner()
    {
        final CompressGadgetZoneRequest request = new CompressGadgetZoneRequest(8L, 9, 4L);
        context.checking(new Expectations()
        {
            {
                allowing(gadgetMapper).execute(request);
                will(returnValue(Arrays.asList(gadget1, gadget2, gadget3)));

                oneOf(pageMapper).execute(4L);
            }
        });

        AsyncActionContext innerContext = new AsyncActionContext(request);
        TaskHandlerActionContext<ActionContext> wrapperContext = new TaskHandlerActionContext<ActionContext>(
                innerContext, new ArrayList<UserActionRequest>());

        sut.execute(wrapperContext);

        context.assertIsSatisfied();
        assertEquals(0, gadget1.getZoneIndex());
        assertEquals(1, gadget2.getZoneIndex());
        assertEquals(2, gadget3.getZoneIndex());
    }
}
