/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.decorators.verb;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Test for the share populator.
 *
 */
public class SharePopulatorTest
{    
    /**
     * Test value for original activity ID.
     */
    private static final Long TEST_ORIG_ACTIVITY_ID = 32L;
    
    /**
     * populate test with a comment.
     */
    @Test
    public final void populate()
    {
        // Construct the activity we're sharing.
        ActivityDTO original = new ActivityDTO();

        original.setId(1L);
        original.setBaseObjectType(BaseObjectType.NOTE);
        StreamEntityDTO originalActor = new StreamEntityDTO();
        originalActor.setUniqueIdentifier("testacct");

        original.setActor(originalActor);

        HashMap<String, String> props = new HashMap<String, String>();
        props.put("test", "value");

        original.setBaseObjectProperties(props);

        SharePopulator sut = new SharePopulator(original, "test comment");

        ActivityDTO activity = new ActivityDTO();
        
        // Populate the new activity.
        sut.populate(activity);
        
        // Ensure that the new activity DTO has the old properties inject into it.
        Assert.assertEquals(ActivityVerb.SHARE, activity.getVerb());
        Assert.assertEquals(originalActor, activity.getOriginalActor());
        Assert.assertEquals(props, activity.getBaseObjectProperties());
        Assert.assertEquals(BaseObjectType.NOTE, activity.getBaseObjectType());
        Assert.assertEquals(String.valueOf(1L), activity
                .getBaseObjectProperties().get("originalActivityId"));
        Assert.assertEquals("test comment", activity.getFirstComment().getBody());
    }

    /**
     * populate test with a comment and share something that has already been shared.
     */
    @Test
    public final void populateActivityAlreadyShared()
    {
        // Construct the activity we're sharing.
        ActivityDTO original = new ActivityDTO();

        original.setId(1L);
        original.setBaseObjectType(BaseObjectType.NOTE);
        StreamEntityDTO originalActor = new StreamEntityDTO();
        originalActor.setUniqueIdentifier("testacct");

        original.setOriginalActor(originalActor);

        HashMap<String, String> props = new HashMap<String, String>();
        props.put("test", "value");
        //An activity that has already been shared will have the originalActivityId
        //populated in the properties map.
        props.put("originalActivityId", TEST_ORIG_ACTIVITY_ID.toString());

        original.setBaseObjectProperties(props);

        SharePopulator sut = new SharePopulator(original, "test comment");

        ActivityDTO activity = new ActivityDTO();
        
        // Populate the new activity.
        sut.populate(activity);

        // Ensure that the new activity DTO has the old properties inject into it.
        Assert.assertEquals(ActivityVerb.SHARE, activity.getVerb());
        Assert.assertEquals(originalActor, activity.getOriginalActor());
        Assert.assertEquals(props, activity.getBaseObjectProperties());
        Assert.assertEquals(BaseObjectType.NOTE, activity.getBaseObjectType());
        Assert.assertEquals(String.valueOf(TEST_ORIG_ACTIVITY_ID), activity
                .getBaseObjectProperties().get("originalActivityId"));
        Assert.assertEquals("test comment", activity.getFirstComment().getBody());
    }
    
    /**
     * populate test without a comment.
     */
    @Test
    public final void populateWithoutComment()
    {
        // Construct the activity we're sharing.
        ActivityDTO original = new ActivityDTO();

        original.setId(1L);
        original.setBaseObjectType(BaseObjectType.NOTE);
        StreamEntityDTO originalActor = new StreamEntityDTO();
        originalActor.setUniqueIdentifier("testacct");

        original.setActor(originalActor);

        HashMap<String, String> props = new HashMap<String, String>();
        props.put("test", "value");

        original.setBaseObjectProperties(props);

        SharePopulator sut = new SharePopulator(original, "");

        ActivityDTO activity = new ActivityDTO();
        
        // Populate the new activity.
        sut.populate(activity);

        // Ensure that the new activity DTO has the old properties inject into it.
        Assert.assertEquals(ActivityVerb.SHARE, activity.getVerb());
        Assert.assertEquals(originalActor, activity.getOriginalActor());
        Assert.assertEquals(props, activity.getBaseObjectProperties());
        Assert.assertEquals(BaseObjectType.NOTE, activity.getBaseObjectType());
        Assert.assertEquals(String.valueOf(1L), activity
                .getBaseObjectProperties().get("originalActivityId"));
        Assert.assertNull(activity.getFirstComment());
    }
}
