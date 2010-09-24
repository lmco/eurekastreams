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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ActivityTemplateEmailBuilder.
 */
public class ActivityTemplateEmailBuilderTest
{
    /** Test data. */
    private static final long ACTIVITY_ID = 9876L;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** List of builders to choose from. */
    private Map<BaseObjectType, TemplateEmailBuilder> builders = new HashMap<BaseObjectType, TemplateEmailBuilder>();

    /** Fixture: builderNote. */
    private TemplateEmailBuilder builderNote = context.mock(TemplateEmailBuilder.class, "builderNote");

    /** Fixture: builderBookmark. */
    private TemplateEmailBuilder builderBookmark = context.mock(TemplateEmailBuilder.class, "builderBookmark");

    /** Fixture: For getting activity info. */
    private BulkActivitiesMapper activitiesMapper = context.mock(BulkActivitiesMapper.class);

    /** Fixture: message. */
    private MimeMessage message = context.mock(MimeMessage.class);

    /** SUT. */
    private ActivityTemplateEmailBuilder sut;

    /** Fixture: request. */
    private NotificationDTO dto;

    /** Fixture: activity. */
    private ActivityDTO activity;

    /**
     * Constructor.
     */
    public ActivityTemplateEmailBuilderTest()
    {
        builders.put(BaseObjectType.NOTE, builderNote);
        builders.put(BaseObjectType.BOOKMARK, builderBookmark);
        activity = new ActivityDTO();
        activity.setBaseObjectProperties(new HashMap<String, String>());
        activity.getBaseObjectProperties().put("content", "This is the text");
        activity.getBaseObjectProperties().put("targetUrl", "http://www.eurekastreams.org");
        activity.getBaseObjectProperties().put("targetTitle", "Eureka Streams");
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ActivityTemplateEmailBuilder(activitiesMapper, builders);
        dto = new NotificationDTO();
    }

    /**
     * Tests build.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuild() throws Exception
    {
        final AnonymousClassInterceptor<Map> intMap = new AnonymousClassInterceptor<Map>(1);

        context.checking(new Expectations()
        {
            {
                allowing(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(activity));
                oneOf(builderBookmark).build(with(equal(dto)), with(any(Map.class)), with(same(message)));
                will(intMap);
            }
        });

        dto.setActivity(ACTIVITY_ID, BaseObjectType.BOOKMARK);

        sut.build(dto, message);

        context.assertIsSatisfied();
        Map map = intMap.getObject();
        assertEquals(3, map.size());
        assertEquals("This is the text", map.get("activity.content"));
        assertEquals("http://www.eurekastreams.org", map.get("activity.targetUrl"));
        assertEquals("Eureka Streams", map.get("activity.targetTitle"));
    }

    /**
     * Tests build: unknown activity type.
     *
     * @throws Exception
     *             Should.
     */
    @Test(expected = Exception.class)
    public void testBuildUnknownType() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(activity));
            }
        });

        dto.setActivity(ACTIVITY_ID, BaseObjectType.PHOTO);

        sut.build(dto, message);

        context.assertIsSatisfied();
    }

    /**
     * Tests build: unknown activity.
     *
     * @throws Exception
     *             Should.
     */
    @Test(expected = Exception.class)
    public void testBuildUnknownActivity() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(null));
            }
        });

        dto.setActivity(ACTIVITY_ID, BaseObjectType.NOTE);

        sut.build(dto, message);

        context.assertIsSatisfied();
    }


}
