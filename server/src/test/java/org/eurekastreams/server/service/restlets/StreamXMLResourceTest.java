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
package org.eurekastreams.server.service.restlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalPopulator;
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
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Test for system filter restlet.
 *
 */
@SuppressWarnings("unchecked")
public class StreamXMLResourceTest
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
     * Action.
     */
    private ServiceAction action = context.mock(ServiceAction.class);

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * Principal populator.
     */
    private PrincipalPopulator principalPopulator = context.mock(PrincipalPopulator.class);

    /**
     * Stream mapper.
     */
    private FindByIdMapper<Stream> streamMapper = context.mock(FindByIdMapper.class);

    /**
     * Mocked security context.
     */
    private SecurityContext securityContext = context.mock(SecurityContext.class);

    /**
     * System under test.
     */
    private StreamXMLResource sut = null;

    /**
     * Results.
     */
    private PagedSet<ActivityDTO> results = null;

    /**
     * Mocked authentication.
     */
    private Authentication auth = context.mock(Authentication.class);


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

        sut =
                new StreamXMLResource(action, serviceActionController, principalPopulator, streamMapper,
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
        sut.setPathOverride("/resources/atom/stream/query/keywords/test");

        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", jsonReq);
        attributes.put("mode", "query");

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(securityContext).getAuthentication();
                will(returnValue(auth));

                oneOf(auth).getName();
                will(returnValue("guid"));


                oneOf(principalPopulator).getPrincipal("guid");

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(returnValue(results));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);
        SecurityContextHolder.setContext(originalContext);
        context.assertIsSatisfied();
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
        sut.setPathOverride("/resources/atom/stream/query/keywords/test");

        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("mode", "query");

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(securityContext).getAuthentication();
                will(returnValue(auth));

                oneOf(auth).getName();
                will(returnValue("guid"));

                oneOf(principalPopulator).getPrincipal("guid");

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(returnValue(results));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);
        SecurityContextHolder.setContext(originalContext);
        context.assertIsSatisfied();
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
        sut.setPathOverride("/resources/atom/stream/query/keywords/test");

        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("mode", "query");

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));

                oneOf(securityContext).getAuthentication();
                will(returnValue(auth));

                oneOf(auth).getName();
                will(returnValue("guid"));

                oneOf(principalPopulator).getPrincipal("guid");

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)), with(equal(action)));
                will(throwException(new Exception("Something went wrong")));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);


        SecurityContextHolder.setContext(originalContext);
        context.assertIsSatisfied();
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
        sut.setPathOverride("/resources/atom/stream/query/unrecognized/test");

        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", query);
        attributes.put("openSocialId", osId);
        attributes.put("mode", "query");

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut.initParams(request);

        Representation actual = sut.represent(null);
        SecurityContextHolder.setContext(originalContext);
        context.assertIsSatisfied();
    }
}
