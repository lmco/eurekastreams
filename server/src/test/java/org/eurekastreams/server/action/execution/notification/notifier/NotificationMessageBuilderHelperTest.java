/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.notifier;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.apache.velocity.context.Context;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests NotificationMessageBuilderHelper.
 */
public class NotificationMessageBuilderHelperTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Velocity context. */
    private final Context velocityContext = context.mock(Context.class, "velocityContext");

    /** SUT. */
    private NotificationMessageBuilderHelper sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new NotificationMessageBuilderHelper();
    }

    /**
     * Tests resolveActivityBody.
     */
    @Test
    public void testResolveActivityBody()
    {
        final ActivityDTO activity = context.mock(ActivityDTO.class, "activity");
        final StreamEntityDTO actor = context.mock(StreamEntityDTO.class, "actor");
        final HashMap<String, String> baseObjectProps = new HashMap<String, String>();
        context.checking(new Expectations()
        {
            {
                allowing(activity).getBaseObjectProperties();
                will(returnValue(baseObjectProps));
                allowing(activity).getActor();
                will(returnValue(actor));
                allowing(actor).getDisplayName();
                will(returnValue("John Doe"));
            }
        });
        activity.getBaseObjectProperties().put("content", "Blah %EUREKA:ACTORNAME% blah %EUREKA:NOSUCH% blah.");

        String result = sut.resolveActivityBody(activity, velocityContext);

        context.assertIsSatisfied();

        assertEquals("Blah John Doe blah %EUREKA:NOSUCH% blah.", result);
    }
}
