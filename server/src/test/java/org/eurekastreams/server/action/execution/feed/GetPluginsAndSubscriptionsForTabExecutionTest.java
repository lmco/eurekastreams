/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.response.feed.PluginAndFeedSubscriptionsResponse;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.mappers.db.GetAllPluginsMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriptionsByEntity;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.service.actions.requests.EmptyRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test the get plugins and subs action.
 *
 */
public class GetPluginsAndSubscriptionsForTabExecutionTest 
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
     * Get plugins Mapper mock.
     */
    private GetAllPluginsMapper getAllPluginsMapper = context.mock(GetAllPluginsMapper.class);
	
    /**
     * Mapper mock.
     */
    private GetFeedSubscriptionsByEntity getFeedSubsMapper = context.mock(GetFeedSubscriptionsByEntity.class);
	/**
	 * Get entity id mapper mock.
	 */
    private GetEntityIdForFeedSubscription getEntityId = context.mock(GetEntityIdForFeedSubscription.class);
	/**
	 * User details mock.
	 */
    private UserDetails user = context.mock(UserDetails.class);
	
    /**
     * System under test.
     */
	private GetPluginsAndSubscriptionsForTabExecution sut;
	
	/**
	 * Actioncontext mock.
	 */
	private PrincipalActionContext ac = context.mock(PrincipalActionContext.class);
	
	/**
	 * user principal mock.
	 */
	private Principal principal = context.mock(Principal.class);
	
    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetPluginsAndSubscriptionsForTabExecution(
        		getAllPluginsMapper, getFeedSubsMapper, getEntityId, EntityType.PERSON);
    }
    
    /**
     * Perform the action successfully.
     * @throws Exception the exception.
     */
    @Test
    public void performAction() throws Exception
    {
    	final PluginDefinition plugin = context.mock(PluginDefinition.class);
    	final Feed feed = context.mock(Feed.class);
    	final Feed feed2 = context.mock(Feed.class, "f2");
    	final FeedSubscriber feedSub = context.mock(FeedSubscriber.class);
    	final FeedSubscriber feedSub2 = context.mock(FeedSubscriber.class, "fs2");
    	
    	final List<FeedSubscriber> feedSubs = new ArrayList<FeedSubscriber>();
    	feedSubs.add(feedSub);
    	feedSubs.add(feedSub2);

        context.checking(new Expectations()
        {
            {
                allowing(ac).getPrincipal();
                will(returnValue(principal));
                
                allowing(ac).getParams();
                will(returnValue("username1"));
                
                allowing(principal).getAccountId();
                will(returnValue("user"));

                oneOf(getEntityId).getEntityId(with(any(HashMap.class)));
            	oneOf(getAllPluginsMapper).execute(with(any(EmptyRequest.class)));
            	oneOf(getFeedSubsMapper).execute(with(any(GetFeedSubscriberRequest.class)));
            	will(returnValue(feedSubs));
            	
            	allowing(feedSub).getFeed();
            	will(returnValue(feed));
            	allowing(feedSub2).getFeed();
            	will(returnValue(feed2));
            	

            	allowing(plugin).getId();
            	
            	oneOf(feed).getPlugin();
            	will(returnValue(plugin));
            	allowing(feed).getLastUpdated();
            	will(returnValue(0L));
            	oneOf(feed).setTimeAgo(with(any(String.class)));
            	
            	oneOf(feed2).getPlugin();
            	will(returnValue(plugin));
            	allowing(feed2).getLastUpdated();
            	will(returnValue(null));
            	oneOf(feed2).setTimeAgo(null);
            }
        });

        PluginAndFeedSubscriptionsResponse response = 
        	(PluginAndFeedSubscriptionsResponse) sut.execute(ac);

        assertEquals(feedSubs, response.getFeedSubcribers());
        
        context.assertIsSatisfied();
    }
}
