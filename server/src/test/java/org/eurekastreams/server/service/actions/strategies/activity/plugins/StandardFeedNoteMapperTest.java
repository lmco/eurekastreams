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

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Note mapper test.
 *
 */
public class StandardFeedNoteMapperTest
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
    private final StandardFeedNoteMapper sut = new StandardFeedNoteMapper();

    /**
     * Content.
     */
    private final String contentVal = "myContent";

    /** Fixture: feed. */
    private final Feed feed = context.mock(Feed.class);

    /**
     * Test with content.
     */
    @Test
    public void getBaseObjectWithContent()
    {
        final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
        final SyndContentImpl content = context.mock(SyndContentImpl.class);

        final List<SyndContentImpl> myContents = new LinkedList<SyndContentImpl>();
        myContents.add(content);

        context.checking(new Expectations()
        {
            {
                allowing(entry).getContents();
                will(returnValue(myContents));

                oneOf(content).getValue();
                will(returnValue(contentVal));
            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.NOTE, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(contentVal, result.get("content"));
    }

    /**
     * Test w/o content.
     */
    @Test
    public void getBaseObjectWithoutContent()
    {
        final SyndEntryImpl entry = context.mock(SyndEntryImpl.class);
        final List<SyndContentImpl> myContents = new LinkedList<SyndContentImpl>();

        context.checking(new Expectations()
        {
            {
                oneOf(entry).getContents();
                will(returnValue(myContents));

                oneOf(entry).getTitle();
                will(returnValue(contentVal));

            }
        });

        Activity activity = new Activity();
        sut.build(feed, entry, activity);

        assertEquals(BaseObjectType.NOTE, activity.getBaseObjectType());

        HashMap<String, String> result = activity.getBaseObject();
        assertEquals(contentVal, result.get("content"));
    }
}
