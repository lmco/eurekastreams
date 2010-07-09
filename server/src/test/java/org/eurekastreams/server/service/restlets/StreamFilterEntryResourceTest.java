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
package org.eurekastreams.server.service.restlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Test for system filter restlet.
 * 
 */
public class StreamFilterEntryResourceTest
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
     * System under test.
     */
    StreamFilterEntryResource sut = new StreamFilterEntryResource();

    /**
     * Init params test.
     */
    @Test
    public void initParams()
    {
        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("id", "7");
        attributes.put("openSocialId", "guid");

        context.checking(new Expectations()
        {
            {
                exactly(2).of(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut.initParams(request);
        context.assertIsSatisfied();
    }

    /**
     * Test with error.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void representWithError() throws Exception
    {
        final StreamFilterFetcher filterFetcher = context
                .mock(StreamFilterFetcher.class);
        sut.setFilterFetcher(filterFetcher);

        context.checking(new Expectations()
        {
            {
                oneOf(filterFetcher).getActivities(with(any(Long.class)),
                        with(any(String.class)), with(any(int.class)));
                will(throwException(new Exception()));
            }
        });

        Representation actual = sut.represent(null);
        context.assertIsSatisfied();

        JSONObject json = JSONObject.fromObject(actual.getText());

        JSONArray activityArray = json.getJSONArray("activities");

        assertNotNull("Activity array is null and should not be", activityArray);
        assertEquals("Activity array should contain 0 items and does not", 0,
                activityArray.size());
    }

    /**
     * Represent test.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void represent() throws Exception
    {
        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO actor = new StreamEntityDTO();
        actor.setAvatarId("actorAvatarId");
        actor.setDisplayName("actorDisplayName");
        actor.setUniqueIdentifier("actorUniqueId");
        actor.setType(EntityType.PERSON);

        StreamEntityDTO origActor = new StreamEntityDTO();
        origActor.setAvatarId("origActorAvatarId");
        origActor.setDisplayName("origActorDisplayName");
        origActor.setUniqueIdentifier("origActorUn" + "iqueId");
        origActor.setType(EntityType.PERSON);

        HashMap<String, String> props = new HashMap<String, String>();
        props.put("content", "my content");

        activity.setServerDateTime(new Date());
        activity.setPostedTime(new Date());
        activity.setId(0L);
        activity.setVerb(ActivityVerb.SHARE);
        activity.setBaseObjectType(BaseObjectType.NOTE);
        activity.setActor(actor);
        activity.setOriginalActor(origActor);
        activity.setBaseObjectProperties(props);

        List<ActivityDTO> list = new java.util.LinkedList<ActivityDTO>();
        list.add(activity);
        final PagedSet<ActivityDTO> results = new PagedSet<ActivityDTO>();
        results.setPagedSet(list);

        final StreamFilterFetcher filterFetcher = context
                .mock(StreamFilterFetcher.class);
        sut.setFilterFetcher(filterFetcher);

        context.checking(new Expectations()
        {
            {
                oneOf(filterFetcher).getActivities(with(any(Long.class)),
                        with(any(String.class)), with(any(int.class)));
                will(returnValue(results));
            }
        });

        Representation actual = sut.represent(null);
        context.assertIsSatisfied();

        JSONObject json = JSONObject.fromObject(actual.getText());

        JSONArray activityArray = json.getJSONArray("activities");

        assertNotNull("Activity array is null and should not be", activityArray);
        assertEquals("Activity array should contain 1 items and does not", 1,
                activityArray.size());

        assertEquals("actorDisplayName", activityArray.getJSONObject(0)
                .getString("actorDisplayName"));
        assertEquals("actorUniqueId", activityArray.getJSONObject(0).getString(
                "actorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString(
                "actorType"));
        assertEquals("origActorDisplayName", activityArray.getJSONObject(0)
                .getString("originalActorDisplayName"));
        assertEquals("origActorUniqueId", activityArray.getJSONObject(0)
                .getString("originalActorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString(
                "originalActorType"));

        assertEquals("SHARE", activityArray.getJSONObject(0).getString("verb"));
        assertEquals("NOTE", activityArray.getJSONObject(0).getString(
                "baseObjectType"));
        assertEquals("my content", activityArray.getJSONObject(0).getString(
                "content"));

    }
}
