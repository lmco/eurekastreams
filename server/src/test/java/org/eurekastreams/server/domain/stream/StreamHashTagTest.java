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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StreamHashTag.
 */
public class StreamHashTagTest
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
     * Hash tag.
     */
    private HashTag hashTag = context.mock(HashTag.class);

    /**
     * Activity responsible for this hashtag.
     */
    private Activity activity = context.mock(Activity.class);

    /**
     * Unique key of the stream entity that the hashtag was posted under.
     */
    private String streamEntityUniqueKey = "sdlfjsdlkfd";

    /**
     * Type of stream that the hashtag is applied to.
     */
    private ScopeType streamScopeType = ScopeType.GROUP;

    /**
     * Date of the activity.
     */
    private Date activityDate = context.mock(Date.class);

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activity).getPostedTime();
                will(returnValue(activityDate));
            }
        });

        StreamHashTag sut = new StreamHashTag(hashTag, activity, streamEntityUniqueKey, streamScopeType);
        assertSame(hashTag, sut.getHashTag());
        assertSame(streamEntityUniqueKey, sut.getStreamEntityUniqueKey());
        assertEquals(streamScopeType, sut.getStreamScopeType());
        assertSame(activityDate, sut.getActivityDate());
    }
}
