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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.ArrayList;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
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
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for SaveAvatarExecution class.
 *
 */
public class SaveAvatarExecutionTest
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
     * Mocked person who will get the new tab.
     */
    private Person person = context.mock(Person.class);

    /**
     * Mocked person resulting from calling another action. (Note to author of this class: please explain this one a
     * little.
     */
    private Person personResult = context.mock(Person.class, "result");


    /**
     * Mocked image writer.
     */
    private ImageWriter imageWriter = context.mock(ImageWriter.class);


    /**
     * Subject under test.
     */
    private SaveAvatarExecution<Person> sut = null;

    /**
     * The mock user information from the session.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * Mocked image.
     */
    private final BufferedImage imageMock = context.mock(BufferedImage.class);

    /**
     * Mocked action called by the SUT action.
     */
    private TaskHandlerExecutionStrategy action = context.mock(TaskHandlerExecutionStrategy.class);

    /**
     * Parameters to pass to the action.
     */
    Serializable[] testParams = { "avatarId", 1L };

    /**
     * Strategy for updating the cache.
     */
    private CacheUpdater cacheUpdaterStrategy = context.mock(CacheUpdater.class);

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
     * {@link SaveAvatarRequest}.
     */
    private SaveImageRequest request = context.mock(SaveImageRequest.class);



    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new SaveAvatarExecution<Person>(personMapper, action, imageWriter, finder);
    }

    /**
     * Test the action. For this test, we'll have a cache updating strategy.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("unchecked")
    @Test
    public void performActionWithWideImage() throws Exception
    {
        final int imageHeight = 500;

        final int imageWidth = 1000;


        context.checking(new Expectations()
        {
            {
                oneOf(request).getFileItem();
                will(returnValue(null));

                allowing(person).getId();
                ignoring(user);

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getImageId();
                will(returnValue("avatarId"));

                allowing(request).getEntityId();
                will(returnValue(1L));


                oneOf(finder).findEntity(principal, 1L);
                will(returnValue(person));

                oneOf(person).getAvatarId();
                will(returnValue("something"));

                oneOf(person).setAvatarId("avatarId");
                oneOf(personMapper).flush();


                oneOf(imageWriter).getImageFromFile(null);
                will(returnValue(imageMock));

                allowing(imageMock).getWidth(null);
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight(null);
                will(returnValue(imageHeight));

                allowing(imageMock).getWidth();
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight();
                will(returnValue(imageHeight));

                allowing(imageMock).getColorModel();
                allowing(imageMock).getType();
                allowing(imageMock).getRaster();
                allowing(imageMock).getAccelerationPriority();

                oneOf(imageWriter).write(with(any(RenderedImage.class)), with(any(String.class)));

                oneOf(action).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personResult));

                exactly(3).of(imageWriter).delete(with(any(String.class)));

                oneOf(cacheUpdaterStrategy).getUpdateCacheRequests(principal, 1L);

                allowing(taskHandlerActionContext).getUserActionRequests();
            }
        });

        // set the cache-updating strategy
        sut.setCacheUpdaterStategy(cacheUpdaterStrategy);

        sut.execute(taskHandlerActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the action. For this test, there is no cache updating strategy.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("unchecked")
    @Test
    public void performActionWithTallImage() throws Exception
    {
        final int imageHeight = 1000;

        final int imageWidth = 500;

        context.checking(new Expectations()
        {
            {

                oneOf(request).getFileItem();
                will(returnValue(null));

                allowing(person).getId();
                ignoring(user);

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(request).getImageId();
                will(returnValue("avatarId"));

                allowing(request).getEntityId();
                will(returnValue(1L));


                oneOf(finder).findEntity(principal, 1L);
                will(returnValue(person));

                oneOf(person).getAvatarId();
                will(returnValue(null));

                oneOf(person).setAvatarId("avatarId");
                oneOf(personMapper).flush();

                oneOf(imageWriter).getImageFromFile(null);
                will(returnValue(imageMock));

                allowing(imageMock).getWidth(null);
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight(null);
                will(returnValue(imageHeight));

                allowing(imageMock).getWidth();
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight();
                will(returnValue(imageHeight));

                allowing(imageMock).getColorModel();
                allowing(imageMock).getType();
                allowing(imageMock).getRaster();
                allowing(imageMock).getAccelerationPriority();

                oneOf(imageWriter).write(with(any(RenderedImage.class)), with(any(String.class)));

                oneOf(action).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personResult));

            }
        });

        TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerContext =
            new TaskHandlerActionContext<PrincipalActionContext>(
                actionContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerContext);
        context.assertIsSatisfied();
    }

}
