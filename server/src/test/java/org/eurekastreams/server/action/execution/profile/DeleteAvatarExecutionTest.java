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
package org.eurekastreams.server.action.execution.profile;

import javax.servlet.ServletContext;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.eurekastreams.server.service.actions.strategies.EntityFinder;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteAvatarExecution class.
 *
 */
@SuppressWarnings("unchecked")
public class DeleteAvatarExecutionTest
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
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Strategy for updating the cache.
     */
    private CacheUpdater cacheUpdaterStrategy = context.mock(CacheUpdater.class);

    /**
     * Mocked person.
     */
    private Person person = context.mock(Person.class);



    /**
     * Mocked ImageWriter.
     */
    private ImageWriter imageWriter = context.mock(ImageWriter.class);



    /**
     * Mocked context for the servlet.
     */
    private ServletContext servletContext = context.mock(ServletContext.class);

    /**
     * Subject under test.
     */
    private DeleteAvatarExecution<Person> sut = null;

    /**
     * Mock.
     */
    private EntityFinder<Person> finder = context.mock(EntityFinder.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new DeleteAvatarExecution<Person>(personMapper, imageWriter, finder);
    }

    /**
     * Testing the delete action. Has cache updater strategy.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithOutNullAvatarId() throws Exception
    {

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(2L));



                oneOf(finder).findEntity(principal, 2L);
                will(returnValue(person));

                allowing(person).getId();
                will(returnValue(2L));

                oneOf(person).getAvatarId();
                will(returnValue("notnull"));


                oneOf(imageWriter).delete("onotnull");
                oneOf(imageWriter).delete("nnotnull");
                oneOf(imageWriter).delete("snotnull");

                oneOf(person).setAvatarId(null);
                oneOf(personMapper).flush();

                oneOf(cacheUpdaterStrategy).getUpdateCacheRequests(principal, 2L);

                allowing(taskHandlerActionContext).getUserActionRequests();
            }
        });

        sut.setCacheUpdaterStategy(cacheUpdaterStrategy);

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test case with a null avatar id. No cache updater strategy,
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithNullAvatarId() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                ignoring(servletContext);

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(2L));


                oneOf(finder).findEntity(principal, 2L);
                will(returnValue(person));

                oneOf(person).getAvatarId();
                will(returnValue(null));

                oneOf(person).setAvatarId(null);
                oneOf(personMapper).flush();
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }
}
