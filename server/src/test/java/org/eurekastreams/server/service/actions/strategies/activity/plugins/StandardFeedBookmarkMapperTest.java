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
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Bookmark mapper test.
 *
 */
public class StandardFeedBookmarkMapperTest
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
    private final StandardFeedBookmarkMapper sut = new StandardFeedBookmarkMapper();

    /**
     * Title.
     */
    private final String title = "myTitle";
    /**
     * Link.
     */
    private final String link = "myLink";
    /**
     * Description.
     */
    private final String desc = "myDesc";
    /**
     * Entry mock.
     */
    private final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
    /**
     * Description content mock.
     */
    private final SyndContent description = context.mock(SyndContent.class);

    /** Fixture: feed. */
    private final Feed feed = context.mock(Feed.class);

    /**
     * Test with thumbnail.
     * @throws URISyntaxException exception.
     */
    @Test
    public void getBaseObjectWithThumbnail() throws URISyntaxException
    {
        final MediaModuleImpl media = context.mock(MediaModuleImpl.class);
        final Metadata metadata = context.mock(Metadata.class);

        final Thumbnail thumbnail = context.mock(Thumbnail.class);
        final Thumbnail[] thumbArr = { thumbnail };
        final URI uri = new URI("http://www.sample.com");

        context.checking(new Expectations()
        {
            {
                allowing(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLink();
                will(returnValue(link));

                allowing(entry).getDescription();
                will(returnValue(description));

                allowing(description).getValue();
                will(returnValue(desc));

                oneOf(entry).getModule(MediaModule.URI);
                will(returnValue(media));

                allowing(media).getMetadata();
                will(returnValue(metadata));

                allowing(metadata).getThumbnail();
                will(returnValue(thumbArr));

                oneOf(thumbnail).getUrl();
                will(returnValue(uri));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.BOOKMARK, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(title, result.get("targetTitle"));
        assertEquals(title, result.get("title"));
        assertEquals(link, result.get("targetUrl"));
        assertEquals(desc, result.get("description"));
        assertEquals("http://www.sample.com", result.get("thumbnail"));
    }


    /**
     * Test without thumbnail.
     */
    @Test
    public void getBaseObjectWithoutThumbnail()
    {
        final MediaModuleImpl media = context.mock(MediaModuleImpl.class);
        final Metadata metadata = context.mock(Metadata.class);

        final Thumbnail[] thumbArr = {};

        context.checking(new Expectations()
        {
            {
                allowing(entry).getTitle();
                will(returnValue(title));

                oneOf(entry).getLink();
                will(returnValue(link));

                oneOf(entry).getDescription();
                will(returnValue(null));


                oneOf(entry).getModule(MediaModule.URI);
                will(returnValue(media));

                allowing(media).getMetadata();
                will(returnValue(metadata));

                allowing(metadata).getThumbnail();
                will(returnValue(thumbArr));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.BOOKMARK, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();

        assertEquals(title, result.get("targetTitle"));
        assertEquals(title, result.get("title"));
        assertEquals(link, result.get("targetUrl"));
        assertEquals("", result.get("description"));
        assertFalse(result.containsKey("thumbnail"));
    }
}
