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
package org.eurekastreams.server.search.bridge;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the last comment id bridge for indexing activity. Used to sort by comment date.
 */
public class ActivityLastCommentIdClassBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static ActivityLastCommentIdClassBridge sut;

    /**
     * Get ordered comment IDs DAO mock.
     */
    private static DomainMapper<Long, List<Long>> commentIdsByActivityIdDAOMock = CONTEXT.mock(DomainMapper.class);

    /**
     * Activity mock.
     */
    private static Activity activity = CONTEXT.mock(Activity.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setUp()
    {
        ActivityLastCommentIdClassBridge.setCommentIdsByActivityIdDAO(commentIdsByActivityIdDAOMock);

        sut = new ActivityLastCommentIdClassBridge();
    }

    /**
     * Tests with a null comment list.
     */
    @Test
    public void nullCommentListTest()
    {
        final Long activityID = 1L;

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activity).getId();
                will(returnValue(activityID));
                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(null));
            }
        });

        Assert.assertEquals("0", sut.objectToString(activity));

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests with a comment list with no comments.
     */
    @Test
    public void noCommentListTest()
    {
        final Long activityID = 1L;

        final List<Long> commentList = new ArrayList<Long>();

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activity).getId();
                will(returnValue(activityID));
                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(commentList));
            }
        });

        Assert.assertEquals("0", sut.objectToString(activity));

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests with a comment list with comments.
     */
    @Test
    public void commentListTest()
    {
        final Long activityID = 1L;
        final long commentId1 = 1L;
        final long commentId2 = 2L;

        final List<Long> commentList = new ArrayList<Long>();
        commentList.add(commentId1);
        commentList.add(commentId2);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activity).getId();
                will(returnValue(activityID));
                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(commentList));
            }
        });

        Assert.assertEquals(Long.toString(commentId2), sut.objectToString(activity));

        CONTEXT.assertIsSatisfied();
    }
}
