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
import static org.junit.Assert.assertFalse;

import java.net.URI;
import java.net.URISyntaxException;
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

import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;

/**
 * Flickr mapper test.
 *
 */
public class FlickrMapperTest
{
    /**
     * Url for flickr images.
     */
    private static final String FLICKR_URL = "http://farm4.static.flickr.com/3501/3955961696_2bba0f6f0f_m.jpg";

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
    private final FlickrMapper sut = new FlickrMapper();

    /**
     * Title.
     */
    private final String title = "myTitle";

    /**
     * Content.
     */
    private final SyndContentImpl content = context.mock(SyndContentImpl.class);

    /**
     * Content list.
     */
    private List<SyndContentImpl> myContents = new LinkedList<SyndContentImpl>();

    /** Fixture: feed. */
    private final Feed feed = context.mock(Feed.class);

    /**
     * Test with image urls and thumbnail.
     * @throws URISyntaxException exception.
     */
    @Test
    public void getBaseObjectWithImageUrlsAndThumbnail() throws URISyntaxException
    {
        final String link1Href = "google";
        final String link2Href = "yahoo";

        final MediaModuleImpl media = context.mock(MediaModuleImpl.class);
        final Metadata metadata = context.mock(Metadata.class);

        final Thumbnail thumbnail = context.mock(Thumbnail.class);
        final Thumbnail[] thumbArr = { thumbnail };
        final URI uri = new URI("http://www.sample.com");

        myContents = new LinkedList<SyndContentImpl>();
        myContents.add(content);

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
                oneOf(entry).getModule(MediaModule.URI);
                will(returnValue(media));

                oneOf(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLinks();
                will(returnValue(myLinks));

                oneOf(entry).getDescription();
                will(returnValue(description));

                allowing(link1).getRel();
                will(returnValue("enclosure"));
                oneOf(link1).getHref();
                will(returnValue(link1Href));

                allowing(link2).getRel();
                will(returnValue("alternate"));
                oneOf(link2).getHref();
                will(returnValue(link2Href));

                allowing(media).getMetadata();
                will(returnValue(metadata));

                allowing(metadata).getThumbnail();
                will(returnValue(thumbArr));

                oneOf(thumbnail).getUrl();
                will(returnValue(uri));

                oneOf(entry).getDescription();
                will(returnValue(description));

                oneOf(description).getValue();
                will(returnValue("anything"));

                oneOf(entry).getContents();
                will(returnValue(myContents));

                oneOf(content).getValue();
                will(returnValue("<p>><img src=\"" + FLICKR_URL + "\" width=\"240\" height=\"144\" /></a></p></"));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.PHOTO, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(title, result.get("title"));
        assertEquals(link1Href, result.get("largerImage"));
        assertEquals(link2Href, result.get("imagePageURL"));
        assertEquals(FLICKR_URL, result.get("thumbnail"));
        assertEquals("", result.get("description"));
    }

    /**
     * Test without image urls and thumbnail.
     */
    @Test
    public void getBaseObjectWithoutImageUrlsAndThumbnail()
    {

        final MediaModuleImpl media = context.mock(MediaModuleImpl.class);
        final Metadata metadata = context.mock(Metadata.class);

        final Thumbnail[] thumbArr = {};

        myContents = new LinkedList<SyndContentImpl>();
        myContents.add(content);

        final SyndContent description = context.mock(SyndContent.class);
        final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
        final SyndLinkImpl link1 = context.mock(SyndLinkImpl.class);

        final List<SyndLinkImpl> myLinks = new LinkedList<SyndLinkImpl>();
        myLinks.add(link1);

        context.checking(new Expectations()
        {
            {
                oneOf(entry).getModule(MediaModule.URI);
                will(returnValue(media));

                oneOf(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLinks();
                will(returnValue(myLinks));

                oneOf(entry).getDescription();
                will(returnValue(description));

                allowing(link1).getRel();
                will(returnValue("notenclosure"));

                oneOf(media).getMetadata();
                will(returnValue(metadata));

                oneOf(metadata).getThumbnail();
                will(returnValue(thumbArr));

                oneOf(entry).getDescription();
                will(returnValue(description));

                oneOf(description).getValue();
                will(returnValue("anything"));

                oneOf(entry).getContents();
                will(returnValue(myContents));

                oneOf(content).getValue();
                will(returnValue("<p>><img src=\"" + FLICKR_URL + "\" width=\"240\" height=\"144\" /></a></p></"));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.PHOTO, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(title, result.get("title"));
        assertFalse(result.containsKey("largerImage"));
        assertFalse(result.containsKey("imagePageURL"));
        assertEquals(FLICKR_URL, result.get("thumbnail"));
        assertEquals("", result.get("description"));
    }
}
