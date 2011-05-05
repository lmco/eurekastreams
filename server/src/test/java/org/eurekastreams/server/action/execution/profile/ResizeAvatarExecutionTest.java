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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.request.profile.ResizeAvatarRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.eurekastreams.server.service.actions.strategies.EntityFinder;
import org.eurekastreams.server.service.actions.strategies.HashGeneratorStrategy;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for the ResizeAvatarExecution class.
 */
@SuppressWarnings("unchecked")
public class ResizeAvatarExecutionTest
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
     * Strategy for updating the cache.
     */
    private CacheUpdater cacheUpdaterStrategy = context.mock(CacheUpdater.class);

    /**
     * The mock mapper to be used by the action.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Mocked person who will get the new tab.
     */
    private Person person = context.mock(Person.class);

    /**
     * Mocked image writer.
     */
    private ImageWriter imageWriter = context.mock(ImageWriter.class);

    /**
     * Mocked HashGenerator.
     */
    private HashGeneratorStrategy hasher = context.mock(HashGeneratorStrategy.class);
    /**
     * Subject under test.
     */
    private ResizeAvatarExecution<Person> sut = null;

    /**
     * The mock user information from the session.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * Mocked image.
     */
    private final BufferedImage imageMock = context.mock(BufferedImage.class);

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
     * Image height.
     */
    private final int imageHeight = 500;

    /**
     * Image width.
     */
    private final int imageWidth = 1000;

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new ResizeAvatarExecution<Person>(personMapper, hasher, imageWriter, finder);
    }

    /**
     * Test the action. Includes a cache update strategy.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithRefresh() throws Exception
    {
        final ResizeAvatarRequest request = new ResizeAvatarRequest(1, 2, 3, Boolean.TRUE, 1L);

        context.checking(new Expectations()
        {
            {
                ignoring(user);
                ignoring(hasher);

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(imageMock).getWidth(null);
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight(null);
                will(returnValue(imageHeight));

                allowing(imageMock).getWidth();
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight();
                will(returnValue(imageHeight));

                allowing(imageMock).getSubimage(1, 2, 3, 3);
                will(returnValue(imageMock));

                allowing(imageMock).getColorModel();
                allowing(imageMock).getType();
                allowing(imageMock).getRaster();
                allowing(imageMock).getAccelerationPriority();

                oneOf(finder).findEntity(principal, 1L);
                will(returnValue(person));

                oneOf(person).getAvatarId();
                allowing(person).getId();

                exactly(2).of(imageWriter).delete(with(any(String.class)));
                oneOf(imageWriter).rename(with(any(String.class)), with(any(String.class)));

                oneOf(person).setAvatarId(with(any(String.class)));

                oneOf(person).setAvatarCropX(1);
                oneOf(person).setAvatarCropY(2);
                oneOf(person).setAvatarCropSize(3);
                oneOf(personMapper).flush();

                oneOf(imageWriter).read(with(any(String.class)));
                will(returnValue(imageMock));

                exactly(2).of(imageWriter).write(with(any(RenderedImage.class)), with(any(String.class)));

                oneOf(cacheUpdaterStrategy).getUpdateCacheRequests(principal, 1L);

                allowing(taskHandlerActionContext).getUserActionRequests();
            }
        });

        sut.setCacheUpdaterStategy(cacheUpdaterStrategy);
        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test the action. No cache updater.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithOutRefresh() throws Exception
    {
        final ResizeAvatarRequest request = new ResizeAvatarRequest(1, 2, 3, Boolean.FALSE, 1L);

        context.checking(new Expectations()
        {
            {
                ignoring(user);

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(request));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(finder).findEntity(principal, 1L);
                will(returnValue(person));

                oneOf(person).getAvatarId();
                allowing(person).getId();

                allowing(imageMock).getWidth(null);
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight(null);
                will(returnValue(imageHeight));

                allowing(imageMock).getWidth();
                will(returnValue(imageWidth));

                allowing(imageMock).getHeight();
                will(returnValue(imageHeight));

                allowing(imageMock).getSubimage(1, 2, 3, 3);
                will(returnValue(imageMock));

                allowing(imageMock).getColorModel();
                allowing(imageMock).getType();
                allowing(imageMock).getRaster();
                allowing(imageMock).getAccelerationPriority();

                oneOf(person).setAvatarCropX(1);
                oneOf(person).setAvatarCropY(2);
                oneOf(person).setAvatarCropSize(3);
                oneOf(personMapper).flush();

                oneOf(imageWriter).read(with(any(String.class)));
                will(returnValue(imageMock));

                exactly(2).of(imageWriter).write(with(any(RenderedImage.class)), with(any(String.class)));

            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

}
