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
import java.net.URL;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.FeedFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;

/**
 * Get title from feed action test.
 * 
 */
public class GetTitleFromFeedExecutionTest
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
     * Feed factory mock.
     */
    private FeedFactory feedFetcherFactory = context.mock(FeedFactory.class);
    /**
     * Feed fetcher mock.
     */
    private FeedFetcher feedFetcher = context.mock(FeedFetcher.class);
    
    /**
     * Atom feed mock.
     */
    private SyndFeed atomFeed = context.mock(SyndFeed.class);
    /**
     * System under test.
     */
    private GetTitleFromFeedExecution sut;
    
    /**
     * Action Context for test.
     */
    private ActionContext ac = context.mock(ActionContext.class);
    

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetTitleFromFeedExecution(feedFetcherFactory);
    }

    /**
     * Perform the action successfully.
     * 
     * @throws Exception
     *             the exception.
     */
    @Test
    public void performActionWithNoException() throws Exception
    {
        final Serializable params = "http://www.google.com";

        context.checking(new Expectations()
        {
            {
            	oneOf(ac).getParams();
            	will(returnValue(params));
            	
                oneOf(feedFetcherFactory).getSyndicatedFeed((with(any(URL.class))));
                will(returnValue(atomFeed));

                oneOf(atomFeed).getTitle();
                will(returnValue("title"));
            }
        });

        String title = (String) sut.execute(ac);

        assertEquals("title", title);

        context.assertIsSatisfied();
    }

    /**
     * Perform the action with an error in the feed.
     * 
     * @throws Exception
     *             the exception.
     */
    @Test(expected = ExecutionException.class)
    public void performActionWithException() throws Exception
    {
        final Serializable params = "http://www.google.com";
    	
        context.checking(new Expectations()
        {
            {
            	oneOf(ac).getParams();
            	will(returnValue(params));            
            	
                oneOf(feedFetcherFactory).getSyndicatedFeed(with(any(URL.class)));
                will(throwException(new Exception()));
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }
}
