/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.service.restlets.support.RestletQueryRequestParser;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

/**
 * Test for system filter restlet.
 *
 */
@SuppressWarnings("unchecked")
public class StreamResourceTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Action.
     */
    private final ServiceAction action = mockery.mock(ServiceAction.class);

    /**
     * Service Action Controller.
     */
    private final ServiceActionController serviceActionController = mockery.mock(ServiceActionController.class);

    /**
     * Stream mapper.
     */
    private final FindByIdMapper<Stream> streamMapper = mockery.mock(FindByIdMapper.class);

    /** Fixture: principal DAO. */
    private final DomainMapper principalDao = mockery.mock(DomainMapper.class, "principalDao");

    /** Fixture: principal. */
    private final Principal principal = mockery.mock(Principal.class, "principal");

    /**
     * System under test.
     */
    private StreamResource sut = null;

    /**
     * Results.
     */
    private PagedSet<ActivityDTO> results = null;

    /**
     * Setup.
     */
    @Before
    public void setUp()
    {
        final List<String> globalWords = new ArrayList<String>();
        globalWords.add("minId");
        globalWords.add("maxId");

        final List<String> multipleEntityWords = new ArrayList<String>();
        multipleEntityWords.add("recipient");

        final List<String> otherWords = new ArrayList<String>();
        otherWords.add("keywords");
        otherWords.add("followedBy");

        sut = new StreamResource(action, serviceActionController, principalDao, streamMapper,
                new RestletQueryRequestParser(globalWords, multipleEntityWords, otherWords));

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
        results = new PagedSet<ActivityDTO>();
        results.setPagedSet(list);
    }

    /**
     * Test representing as JSONP.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void representJsonP() throws Exception
    {
        // callback in attributes, JSONP response
        final String callback = "callback";
        final String jsonReq = "";
        final String osId = "guid";
        sut.setPathOverride("/resources/stream/guid/callback/test/query/keywords/test");

        final Request request = mockery.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", jsonReq);
        attributes.put("openSocialId", osId);
        attributes.put("callback", callback);
        attributes.put("mode", "query");

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(principalDao).execute("guid");
                will(returnValue(principal));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(returnValue(results));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);

        // Confirm JSONP
        assertTrue(actual.getText().startsWith(callback + "("));
        assertTrue(actual.getText().endsWith(")"));

        String jsonText = actual.getText();
        // remove function call and open parenthesis in the beginning
        jsonText = jsonText.substring((callback + "(").length());
        // remove close parenthesis at the end.
        jsonText = jsonText.substring(0, jsonText.length() - 1);

        JSONObject json = JSONObject.fromObject(jsonText);
        assertEquals("OK", json.getString("status"));
        JSONArray activityArray = json.getJSONArray("activities");

        assertNotNull("Activity array is null and should not be", activityArray);
        assertEquals("Activity array should contain 1 item and does not", 1, activityArray.size());

        assertEquals("actorDisplayName", activityArray.getJSONObject(0).getString("actorDisplayName"));
        assertEquals("actorUniqueId", activityArray.getJSONObject(0).getString("actorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString("actorType"));
        assertEquals("origActorDisplayName", activityArray.getJSONObject(0).getString("originalActorDisplayName"));
        assertEquals("origActorUniqueId", activityArray.getJSONObject(0).getString("originalActorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString("originalActorType"));

        assertEquals("SHARE", activityArray.getJSONObject(0).getString("verb"));
        assertEquals("NOTE", activityArray.getJSONObject(0).getString("baseObjectType"));
        assertEquals("my content", activityArray.getJSONObject(0).getString("content"));

        mockery.assertIsSatisfied();
    }

    /**
     * Test representing as JSON.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void representTest() throws Exception
    {
        // no callback in attributes, JSON response
        final String query = "";
        final String osId = "guid";
        sut.setPathOverride("/resources/stream/guid/query/keywords/test");

        final Request request = mockery.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("mode", "query");
        attributes.put("openSocialId", osId);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(principalDao).execute("guid");
                will(returnValue(principal));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(returnValue(results));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);

        JSONObject json = JSONObject.fromObject(actual.getText());
        assertEquals("OK", json.getString("status"));

        JSONArray activityArray = json.getJSONArray("activities");

        assertNotNull("Activity array is null and should not be", activityArray);
        assertEquals("Activity array should contain 1 item and does not", 1, activityArray.size());

        assertEquals("actorDisplayName", activityArray.getJSONObject(0).getString("actorDisplayName"));
        assertEquals("actorUniqueId", activityArray.getJSONObject(0).getString("actorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString("actorType"));
        assertEquals("origActorDisplayName", activityArray.getJSONObject(0).getString("originalActorDisplayName"));
        assertEquals("origActorUniqueId", activityArray.getJSONObject(0).getString("originalActorUniqueIdentifier"));
        assertEquals("PERSON", activityArray.getJSONObject(0).getString("originalActorType"));

        assertEquals("SHARE", activityArray.getJSONObject(0).getString("verb"));
        assertEquals("NOTE", activityArray.getJSONObject(0).getString("baseObjectType"));
        assertEquals("my content", activityArray.getJSONObject(0).getString("content"));

        mockery.assertIsSatisfied();
    }

    /**
     * Test representing as JSON with a bad request.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void representTestBadParse() throws Exception
    {
        // no callback in attributes, JSON response
        final String query = "";
        final String osId = "guid";
        sut.setPathOverride("/resources/stream/guid/query/keywords/test");

        final Request request = mockery.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("mode", "query");
        attributes.put("openSocialId", osId);

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(principalDao).execute("guid");
                will(returnValue(principal));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(throwException(new Exception("Something went wrong")));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertTrue(json.getString("status").startsWith("Error"));

        mockery.assertIsSatisfied();
    }

    /**
     * Test representing as JSON with a service exception.
     *
     * @throws Exception
     *             exception.
     */
    @Test
    public void representTestServiceException() throws Exception
    {
        // no callback in attributes, JSON response
        final String query = "";
        final String osId = "guid";
        sut.setPathOverride("/resources/stream/guid/query/unrecognized/test");

        final Request request = mockery.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("openSocialId", osId);
        attributes.put("mode", "query");

        mockery.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertTrue(json.getString("status").startsWith("Error"));

        mockery.assertIsSatisfied();
    }
}
