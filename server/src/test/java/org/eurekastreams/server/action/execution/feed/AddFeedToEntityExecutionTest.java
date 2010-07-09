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
package org.eurekastreams.server.action.execution.feed;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.Action;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.execution.stream.PostActivityExecutionStrategy;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedByUrlOrCreateMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriberOrCreateMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedByUrlRequest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for add feed to current user.
 *
 */
public class AddFeedToEntityExecutionTest
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
     * Update mapper.
     */
    private UpdateMapper<Feed> updateMapper = context.mock(UpdateMapper.class);
    /**
     * Get mapper.
     */
    private GetFeedByUrlOrCreateMapper getMapper = context.mock(GetFeedByUrlOrCreateMapper.class);
    /**
     * User details.
     */
    private ExtendedUserDetails user = context.mock(ExtendedUserDetails.class);

    /**
     * Task Handler.
     */
    private TaskHandlerActionContext taskHandlerContext = context.mock(TaskHandlerActionContext.class);
    /**
     * Action context mock.
     */
    private PrincipalActionContext ac = context.mock(PrincipalActionContext.class);

    /**
     * Mock principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Person mapper.
     */
    private GetEntityIdForFeedSubscription getEntityId = context.mock(GetEntityIdForFeedSubscription.class);

    /**
     * Get the feed subscribers mapper.
     */
    private GetFeedSubscriberOrCreateMapper getFeedSubMapper = context.mock(GetFeedSubscriberOrCreateMapper.class);

    /**
     * Get title from feed.
     */
    private GetTitleFromFeedExecution getTitleFromFeed = context.mock(GetTitleFromFeedExecution.class);

    /**
     * Get title from feed.
     */
    private PostActivityExecutionStrategy postActivity = context.mock(PostActivityExecutionStrategy.class);
    /**
     * System under test.
     */
    private AddFeedToEntityExecution sut;

    /**
     * Delete feed sub action.
     */
    Action deleteFedSub = context.mock(Action.class);

    /**
     * Setup.
     */
    @Before
    public final void setup()
    {
        sut = new AddFeedToEntityExecution(
        		updateMapper, getMapper, getEntityId, getFeedSubMapper,
        		getTitleFromFeed, deleteFedSub, EntityType.PERSON, postActivity);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerContext).getActionContext();
                will(returnValue(ac));

                allowing(taskHandlerContext).getUserActionRequests();
            }
        });
    }

    /**
     * Perform action.
     * @throws Exception the exception.
     */
    @Test
    public void performActionWithSuccess() throws Exception
    {
        final FeedSubscriber feedSub = context.mock(FeedSubscriber.class);
        final Feed feed = context.mock(Feed.class);
        final HashMap<String, Serializable> conf = new HashMap<String, Serializable>();

        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:TOS", Boolean.TRUE);
        values.put("EUREKA:PLUGINID", 1L);
        values.put("EUREKA:FEEDURL", "feedurl");
        values.put("somekey", "somevalue");
        values.put("REQUIRED:somekey", "somekey is required!");


        context.checking(new Expectations()
        {
            {
                oneOf(getMapper).execute(with(any(GetFeedByUrlRequest.class)));
                will(returnValue(feed));

                oneOf(getFeedSubMapper).execute(with(any(GetFeedSubscriberRequest.class)));
                will(returnValue(feedSub));

                allowing(feed).getId();

                allowing(feedSub).getConfSettings();
                will(returnValue(conf));

                allowing(user).getUsername();
                will(returnValue("username1"));

                allowing(getTitleFromFeed).execute(with(any(ServiceActionContext.class)));
                will(returnValue("title"));

                allowing(feed).setTitle("title");

                allowing(getEntityId).getEntityId(values);

                oneOf(updateMapper).execute(with(any(PersistenceRequest.class)));

                allowing(ac).getParams();
                will(returnValue(values));

                allowing(ac).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("user"));

                oneOf(postActivity).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        sut.execute(taskHandlerContext);

        context.assertIsSatisfied();

        assertEquals("somevalue", conf.get("somekey"));
    }

    /**
     * Perform action.
     * @throws Exception the exception.
     */
    @Test
    public void performActionWithSuccessWithGroupAndEdit() throws Exception
    {
        final FeedSubscriber feedSub = context.mock(FeedSubscriber.class);
        final Feed feed = context.mock(Feed.class);
        final HashMap<String, Serializable> conf = new HashMap<String, Serializable>();

        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:TOS", Boolean.TRUE);
        values.put("EUREKA:PLUGINID", 1L);
        values.put("EUREKA:FEEDSUBID", 1L);
        values.put("EUREKA:GROUP", "something");
        values.put("EUREKA:FEEDURL", "feedurl");
        values.put("somekey", "somevalue");
        values.put("REQUIRED:somekey", "somekey is required!");

        final ExecutionStrategy es = context.mock(ExecutionStrategy.class);

        context.checking(new Expectations()
        {
            {
                oneOf(getMapper).execute(with(any(GetFeedByUrlRequest.class)));
                will(returnValue(feed));

                oneOf(getFeedSubMapper).execute(with(any(GetFeedSubscriberRequest.class)));
                will(returnValue(feedSub));

                allowing(feed).getId();

                allowing(feedSub).getConfSettings();
                will(returnValue(conf));

                allowing(user).getUsername();
                will(returnValue("username1"));

                allowing(feed).setTitle("title");

                allowing(getEntityId).getEntityId(values);

                oneOf(updateMapper).execute(with(any(PersistenceRequest.class)));

                oneOf(deleteFedSub).getExecutionStrategy();
                will(returnValue(es));

                oneOf(es).execute(with(any(ServiceActionContext.class)));

                allowing(getTitleFromFeed).execute(with(any(ServiceActionContext.class)));
                will(returnValue("title"));

                allowing(ac).getParams();
                will(returnValue(values));

                allowing(ac).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("user"));
                oneOf(postActivity).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        sut.execute(taskHandlerContext);

        context.assertIsSatisfied();

        assertEquals("somevalue", conf.get("somekey"));
    }

    /**
     * Perform action.
     * @throws Exception the exception.
     */
    @Test(expected = ValidationException.class)
    public void performActionWithValidationErrorNoTOS() throws Exception
    {
        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:TOS", Boolean.FALSE);
        values.put("EUREKA:PLUGINID", 1L);
        values.put("EUREKA:FEEDURL", "feedurl");
        values.put("somekey", "somevalue");
        values.put("REQUIRED:somekey", "somekey is required!");

        context.checking(new Expectations()
        {
            {
                allowing(ac).getParams();
                will(returnValue(values));
            }
        });


        sut.execute(taskHandlerContext);

    }

    /**
     * Perform action.
     * @throws Exception the exception.
     */
    @Test(expected = ValidationException.class)
    public void performActionWithValidationErrorMissingRequired() throws Exception
    {
        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:TOS", Boolean.TRUE);
        values.put("EUREKA:PLUGINID", 1L);
        values.put("EUREKA:FEEDURL", "feedurl");
        values.put("somekey", "");
        values.put("REQUIRED:somekey", "somekey is required!");

        context.checking(new Expectations()
        {
            {
                allowing(ac).getParams();
                will(returnValue(values));
            }
        });

        sut.execute(taskHandlerContext);
    }
}
