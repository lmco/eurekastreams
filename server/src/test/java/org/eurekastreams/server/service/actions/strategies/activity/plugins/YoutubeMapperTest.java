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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;

/**
 * Youtube mapper test.
 *
 */
public class YoutubeMapperTest
{

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private final YoutubeMapper sut = new YoutubeMapper();

    /**
     * Title.
     */
    private final String title = "myTitle";

    /**
     * Link 1 href.
     */
    private final String link1Href = "http://www.youtube.com/v/7";
    /**
     * Link 2 href.
     */
    private final String link2Href = "http://www.youtube.com?v=7&x=3";

    /** Fixture: feed. */
    private final Feed feed = context.mock(Feed.class);

    /**
     * Test with enclosure.
     */
    @Test
    public void getBaseObjectWithEnclosure()
    {

        final SyndContent description = context.mock(SyndContent.class);
        final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
        final SyndLinkImpl link1 = context.mock(SyndLinkImpl.class, "link1");
        final SyndLinkImpl link2 = context.mock(SyndLinkImpl.class, "link2");

        final List<SyndLinkImpl> myLinks = new LinkedList<SyndLinkImpl>();
        myLinks.add(link1);
        myLinks.add(link2);

        context.checking(new Expectations()
        {
            {
                oneOf(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLinks();
                will(returnValue(myLinks));

                allowing(link1).getRel();
                will(returnValue("enclosure"));
                oneOf(link1).getHref();
                will(returnValue(""));

                allowing(link2).getRel();
                will(returnValue("alternate"));
                oneOf(link2).getHref();
                will(returnValue(""));

                allowing(entry).getDescription();
                will(returnValue(description));

                allowing(description).getValue();
                will(returnValue("struffhere<img alt=\"\" src=\"source\"><span>desc</span>stuff"));

                allowing(entry).getLink();
                will(returnValue(link2Href));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.VIDEO, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(title, result.get("title"));
        assertEquals(link1Href, result.get("videoStream"));
        assertEquals(link2Href, result.get("videoPageUrl"));
        assertEquals("desc", result.get("description"));
        assertEquals("source", result.get("thumbnail"));

    }

    /**
     * Test with no enclosure.
     */
    @Test
    public void getBaseObjectWithoutImageUrls()
    {
        final SyndContent description = context.mock(SyndContent.class);
        final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
        final SyndLinkImpl link1 = context.mock(SyndLinkImpl.class);

        final List<SyndLinkImpl> myLinks = new LinkedList<SyndLinkImpl>();
        myLinks.add(link1);

        context.checking(new Expectations()
        {
            {
                oneOf(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLinks();
                will(returnValue(myLinks));

                allowing(link1).getRel();
                will(returnValue("notenclosure"));

                allowing(entry).getDescription();
                will(returnValue(description));

                allowing(description).getValue();
                will(returnValue("struffhere<img alt=\"\" src=\"source\"><span>desc</span>stuff"));

                allowing(entry).getLink();
                will(returnValue(link2Href));

            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.VIDEO, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(title, result.get("title"));
        assertEquals(link1Href, result.get("videoStream"));
        assertEquals(link2Href, result.get("videoPageUrl"));
        assertEquals("desc", result.get("description"));
        assertEquals("source", result.get("thumbnail"));
    }
}
