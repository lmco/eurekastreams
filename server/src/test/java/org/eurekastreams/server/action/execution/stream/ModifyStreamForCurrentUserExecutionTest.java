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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests modifying a stream for the current user.
 */
public class ModifyStreamForCurrentUserExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static ModifyStreamForCurrentUserExecution sut;

    /**
     * Person mapper.
     */
    private static PersonMapper personMapper = CONTEXT.mock(PersonMapper.class);

    /**
     * Action context.
     */
    private static PrincipalActionContext actionContext = CONTEXT.mock(PrincipalActionContext.class);

    /**
     * Principle.
     */
    private static Principal principal = CONTEXT.mock(Principal.class);

    /**
     * User Id.
     */
    private static final String USER_ID = "ntid";

    /**
     * Person.
     */
    private static Person person = CONTEXT.mock(Person.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new ModifyStreamForCurrentUserExecution(personMapper);
    }

    /**
     * Tests adding a stream for the current user.
     */
    @Test
    public void testAdd()
    {
        // Stream to add
        final Stream stream = new Stream();
        stream.setId(0L);

        // Empty list
        final List<Stream> streams = new ArrayList<Stream>();

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(USER_ID));

                oneOf(personMapper).findByAccountId(USER_ID);
                will(returnValue(person));

                oneOf(person).getStreams();
                will(returnValue(streams));

                oneOf(actionContext).getParams();
                will(returnValue(stream));

                oneOf(personMapper).flush();
            }
        });

        sut.execute(actionContext);

        Assert.assertEquals(1, streams.size());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests modifying and existing stream for the current user.
     */
    @Test
    public void testModify()
    {
        // Stream to modify
        final Stream modifiedStream = new Stream();
        modifiedStream.setId(1L);
        modifiedStream.setName("New Name");
        modifiedStream.setRequest("{ query : { keywords : 'test' } }");
        modifiedStream.setReadOnly(false);

        final List<Stream> streams = new ArrayList<Stream>();

        // Current stream
        final Stream oldStream = new Stream();
        oldStream.setId(1L);
        oldStream.setName("Old Name");
        oldStream.setRequest("{ query : { keywords : 'cheese' } }");
        oldStream.setReadOnly(true);
        
        streams.add(oldStream);

        
        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(USER_ID));

                oneOf(personMapper).findByAccountId(USER_ID);
                will(returnValue(person));

                oneOf(person).getStreams();
                will(returnValue(streams));

                oneOf(actionContext).getParams();
                will(returnValue(modifiedStream));

                oneOf(personMapper).flush();
            }
        });

        sut.execute(actionContext);

        // Stream size remains at 1.
        Assert.assertEquals(1, streams.size());
        
        // Confirm stream data has changed.
        Assert.assertEquals(modifiedStream.getName(), streams.get(0).getName());
        Assert.assertEquals(modifiedStream.getRequest(), streams.get(0).getRequest());
        Assert.assertEquals(modifiedStream.getReadOnly(), streams.get(0).getReadOnly());

        CONTEXT.assertIsSatisfied();
    }
    
    /**
     * Tests modifying and existing stream that does not belong to the current user..
     */
    @Test
    public void testModifyDenied()
    {
        // Stream to modify
        final Stream modifiedStream = new Stream();
        modifiedStream.setId(2L);
        modifiedStream.setName("New Name");
        modifiedStream.setRequest("{ query : { keywords : 'test' } }");
        modifiedStream.setReadOnly(false);

        final List<Stream> streams = new ArrayList<Stream>();

        // Current stream
        final Stream oldStream = new Stream();
        oldStream.setId(1L);
        oldStream.setName("Old Name");
        oldStream.setRequest("{ query : { keywords : 'cheese' } }");
        oldStream.setReadOnly(true);
        
        streams.add(oldStream);

        
        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(USER_ID));

                oneOf(personMapper).findByAccountId(USER_ID);
                will(returnValue(person));

                oneOf(person).getStreams();
                will(returnValue(streams));

                oneOf(actionContext).getParams();
                will(returnValue(modifiedStream));

                oneOf(personMapper).flush();
            }
        });

        sut.execute(actionContext);

        // Stream size remains at 1
        Assert.assertEquals(1, streams.size());
        
        // Confirm stream data has not changed.
        Assert.assertEquals(oldStream.getName(), streams.get(0).getName());
        Assert.assertEquals(oldStream.getRequest(), streams.get(0).getRequest());
        Assert.assertEquals(oldStream.getReadOnly(), streams.get(0).getReadOnly());

        CONTEXT.assertIsSatisfied();
    }
}
