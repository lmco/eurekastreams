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
package org.eurekastreams.server.action.authorization.feed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriptionsByEntity;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.userdetails.UserDetails;

/**
 * Delete strategy test.
 *
 */
public class DeleteFeedSubscriberAuthorizationStrategyTest
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
     * Get all the subs for an entity.
     */
    private GetFeedSubscriptionsByEntity getFeedSubsMapper = context.mock(GetFeedSubscriptionsByEntity.class);
    /**
     * Get the entity id.
     */
    private GetEntityIdForFeedSubscription getEntityId = context.mock(GetEntityIdForFeedSubscription.class);

    /**
     * User details mock.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * Empty params.
     */
    final DeleteFeedSubscriptionRequest param = context.mock(DeleteFeedSubscriptionRequest.class);

    /**
     * Action context.
     */
    final PrincipalActionContext ac = context.mock(PrincipalActionContext.class);

    /**
     * user principal.
     */
    final Principal principal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private DeleteFeedSubscriberAuthorizationStrategy sut;

    /**
     * Setup sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteFeedSubscriberAuthorizationStrategy(getFeedSubsMapper, getEntityId, EntityType.PERSON);
    }

    /**
     * Authorize with access.
     */
    @Test
    public void authorizeWithAccess()
    {
        final FeedSubscriber feed1 = context.mock(FeedSubscriber.class);
        final FeedSubscriber feed2 = context.mock(FeedSubscriber.class, "f2");

        final List<FeedSubscriber> feeds = new ArrayList<FeedSubscriber>();
        feeds.add(feed1);
        feeds.add(feed2);

        final Serializable[] paramsForAuthorizor = new Serializable[1];
        paramsForAuthorizor[0] = "something";

        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:USER", "user");
        values.put("EUREKA:GROUP", "something");

        context.checking(new Expectations()
        {
            {
                oneOf(getEntityId).getEntityId(values);
                oneOf(getFeedSubsMapper).execute(with(any(GetFeedSubscriberRequest.class)));
                will(returnValue(feeds));

                allowing(feed1).getId();
                will(returnValue(1L));

                allowing(feed2).getId();
                will(returnValue(2L));

                allowing(param).getEntityId();
                will(returnValue("something"));

                allowing(param).getFeedSubscriberId();
                will(returnValue(2L));

                allowing(ac).getPrincipal();
                will(returnValue(principal));

                allowing(ac).getParams();
                will(returnValue(param));

                allowing(principal).getAccountId();
                will(returnValue("user"));

                allowing(principal).getId();
                will(returnValue(9L));
            }
        });

        sut.authorize(ac);
        context.assertIsSatisfied();
    }

    /**
     * Authorize with out access.
     */
    @Test(expected = AccessDeniedException.class)
    public void authorizeWithOutAccess()
    {
        final FeedSubscriber feed1 = context.mock(FeedSubscriber.class);
        final FeedSubscriber feed2 = context.mock(FeedSubscriber.class, "f2");

        final List<FeedSubscriber> feeds = new ArrayList<FeedSubscriber>();
        feeds.add(feed1);
        feeds.add(feed2);

        final Serializable[] paramsForAuthorizor = new Serializable[1];
        paramsForAuthorizor[0] = "something";

        final HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        values.put("EUREKA:USER", "user");
        values.put("EUREKA:GROUP", "something");

        context.checking(new Expectations()
        {
            {
                oneOf(getEntityId).getEntityId(values);
                oneOf(getFeedSubsMapper).execute(with(any(GetFeedSubscriberRequest.class)));
                will(returnValue(feeds));

                allowing(feed1).getId();
                will(returnValue(1L));

                allowing(feed2).getId();
                will(returnValue(3L));

                allowing(param).getEntityId();
                will(returnValue("something"));

                allowing(param).getFeedSubscriberId();
                will(returnValue(2L));

                allowing(ac).getPrincipal();
                will(returnValue(principal));

                allowing(ac).getParams();
                will(returnValue(param));

                allowing(principal).getAccountId();
                will(returnValue("user"));

                allowing(principal).getId();
                will(returnValue(9L));
            }
        });

        sut.authorize(ac);
        context.assertIsSatisfied();
    }
}
