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

import junit.framework.Assert;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.junit.Test;

/**
 * Tests class bridge to get for activity app source.
 */
public class ActivitySourceClassBridgeTest
{
    /**
     * System under test.
     */
    private final ActivitySourceClassBridge sut = new ActivitySourceClassBridge();

    /**
     * Tests with type not set.
     */
    @Test
    public void testTypeNotSet()
    {
        Activity activity = new Activity();
        activity.setAppId(9L);
        activity.setAppType(EntityType.NOTSET);

        Assert.assertEquals("0", sut.objectToString(activity));
    }

    /**
     * Tests with type not an app.
     */
    @Test
    public void testTypeNotApp()
    {
        Activity activity = new Activity();
        activity.setAppId(9L);
        activity.setAppType(EntityType.PERSON);

        Assert.assertEquals("0", sut.objectToString(activity));
    }

    /**
     * Tests with null id.
     */
    @Test
    public void testIdNull()
    {
        Activity activity = new Activity();
        activity.setAppId(null);
        activity.setAppType(EntityType.APPLICATION);

        Assert.assertEquals("0", sut.objectToString(activity));
    }

    /**
     * Tests with null type.
     */
    @Test
    public void testTypeNull()
    {
        Activity activity = new Activity();
        activity.setAppId(5L);

        Assert.assertEquals("0", sut.objectToString(activity));
    }

    /**
     * Tests for an app.
     */
    @Test
    public void testForApp()
    {
        Activity activity = new Activity();
        activity.setAppId(5L);
        activity.setAppType(EntityType.APPLICATION);

        Assert.assertEquals(ActivitySourceClassBridge.APPLICATION_PREFIX + Long.toString(5L), sut
                .objectToString(activity));
    }

    /**
     * Tests for a plugin.
     */
    @Test
    public void testForPlugin()
    {
        Activity activity = new Activity();
        activity.setAppId(7L);
        activity.setAppType(EntityType.PLUGIN);

        Assert.assertEquals(ActivitySourceClassBridge.PLUGIN_PREFIX + Long.toString(7L), sut.objectToString(activity));
    }
}
