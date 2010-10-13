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
    private ActivitySourceClassBridge sut = new ActivitySourceClassBridge();

    /**
     * Tests with a null App Id.
     */
    @Test
    public void nullAppIdTest()
    {
        Activity activity = new Activity();
        activity.setAppId(null);

        Assert.assertEquals("0", sut.objectToString(activity));
    }

    /**
     * Tests with an App Id.
     */
    @Test
    public void appIdTest()
    {
        Activity activity = new Activity();
        activity.setAppId(1L);

        Assert.assertEquals(Long.toString(activity.getAppId()), sut.objectToString(activity));
    }
}
