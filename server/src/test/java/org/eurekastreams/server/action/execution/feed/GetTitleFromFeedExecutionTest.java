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

import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.FeedFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Get title from feed action test.
 *
 */
public class GetTitleFromFeedExecutionTest
{
    /** Test data. */
    private static final String FEED_URL = "http://www.google.com";

    /** Test data. */
    private static final String USER = "jdoe";

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
    private PrincipalActionContext ac = context.mock(PrincipalActionContext.class);

    /** Fixture: principal. */
    private Principal principal = context.mock(Principal.class);


    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetTitleFromFeedExecution(feedFetcherFactory);

        context.checking(new Expectations()
        {
            {
                allowing(ac).getParams();
                will(returnValue(FEED_URL));
                allowing(ac).getPrincipal();
                will(returnValue(principal));
                allowing(principal).getAccountId();
                will(returnValue(USER));
            }
        });
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
        context.checking(new Expectations()
        {
            {
                oneOf(feedFetcherFactory).getSyndicatedFeed(with(equal(FEED_URL)),
                        (List<String>) with(org.hamcrest.Matchers.hasItem(USER)));
                will(returnValue(Collections.singletonMap(USER, atomFeed)));

                allowing(atomFeed).getTitle();
                will(returnValue("title"));
            }
        });

        String title = (String) sut.execute(ac);

        context.assertIsSatisfied();

        assertEquals("title", title);
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
        context.checking(new Expectations()
        {
            {
                oneOf(feedFetcherFactory).getSyndicatedFeed(with(equal(FEED_URL)),
                        (List<String>) with(org.hamcrest.Matchers.hasItem(USER)));
                will(throwException(new Exception()));
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }
}
