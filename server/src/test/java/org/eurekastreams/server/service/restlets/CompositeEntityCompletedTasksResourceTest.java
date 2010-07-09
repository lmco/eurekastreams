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
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Task;
import org.eurekastreams.server.persistence.CompositeEntityMapper;
import org.eurekastreams.server.persistence.TaskMapper;

/**
 * Test for completed tasks resource.
 * 
 */
public class CompositeEntityCompletedTasksResourceTest
{
    /**
     * SUT.
     */
    private CompositeEntityCompletedTasksResource sut;

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
    private CompositeEntityMapper mapper = context.mock(CompositeEntityMapper.class);

    /**
     * Mocked mapper.
     */
    private TaskMapper taskMapper = context.mock(TaskMapper.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gadgetDefId", (String) "1");
        attributes.put("shortName", (String) "isgs");

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new CompositeEntityCompletedTasksResource();
        sut.init(restContext, request, response);
        sut.setEntityMapper(mapper);
        sut.setTaskMapper(taskMapper);
    }

    /**
     * Test DELETE.
     * 
     * @throws ResourceException
     *             error.
     * @throws IOException
     *             error.
     */
    @Test
    public void removeRepresentations() throws ResourceException, IOException
    {
        final CompositeEntity me = context.mock(CompositeEntity.class);
        final List<Task> tasks = new LinkedList<Task>();
        final Task task1 = context.mock(Task.class);
        final Task task2 = context.mock(Task.class, "t2");
        tasks.add(task1);
        tasks.add(task2);

        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findByShortName("isgs");
                will(returnValue(me));

                oneOf(me).getCompletedTasks();
                will(returnValue(tasks));

                oneOf(entity).getText();
                will(returnValue("{ name : 'something' }"));

                oneOf(taskMapper).findByNameAndGadgetDefId("something", 1L);

                oneOf(mapper).flush();
            }
        });

        sut.acceptRepresentation(entity);
        context.assertIsSatisfied();
    }
}
