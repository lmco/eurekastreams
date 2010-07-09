/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Task;

/**
 * Test the checklist resource.
 * 
 */
public class ChecklistResourceTest
{
    /**
     * SUT.
     */
    private ChecklistResource sut;

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
     * Mocked restlet context.
     */
    private Context restContext = context.mock(Context.class);

    /**
     * Mocked request.
     */
    private Request request = context.mock(Request.class);

    /**
     * Mocked response.
     */
    private Response response = context.mock(Response.class);

    /**
     * Mocked mapper.
     */
    private GadgetDefinitionMapper mapper = context
            .mock(GadgetDefinitionMapper.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gadgetDefId", (String) "1");

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new ChecklistResource();
        sut.init(restContext, request, response);
        sut.setGadgetDefinitionMapper(mapper);
    }

    /**
     * Test PUT.
     * 
     * @throws ResourceException
     *             error.
     * @throws IOException
     *             error.
     */
    @Test
    public void storeRepresentation() throws ResourceException, IOException
    {
        final GadgetDefinition gd = context.mock(GadgetDefinition.class);
        final List<Task> tasks = new LinkedList<Task>();
        
        tasks.add(new Task("old", "olddesc"));
        tasks.add(new Task("deleteme", "deleteme"));

        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findById(1L);
                will(returnValue(gd));

                oneOf(entity).getText();
                will(returnValue("{ tasks : [ { name : \"old\", description : \"olddesc\" }," 
                        + " { name : \"new\", description : \"new\" } ]}"));

                oneOf(gd).getTasks();
                will(returnValue(tasks));
                oneOf(mapper).flush();
            }
        });

        sut.storeRepresentation(entity);
        context.assertIsSatisfied();
    }
}
