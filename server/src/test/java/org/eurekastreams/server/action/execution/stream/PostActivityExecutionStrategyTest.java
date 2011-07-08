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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.action.validation.stream.PostActivityTestHelpers;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.PostActivityUpdateStreamsByActorMapper;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.stream.InsertActivityComment;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.RecipientRetriever;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link PostActivityExecutionStrategy}.
 * 
 * Note: There are not tests for failure scenarios since this execution strategy
 * does not have a need to handle any exceptions. They are passed up to the
 * action controller which then wraps them and returns them to the client.
 * 
 */
@SuppressWarnings("unchecked")
public class PostActivityExecutionStrategyTest
{
    /**
     * System under test.
     */
    private PostActivityExecutionStrategy sut;

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
     * Mocked test instance of the {@link InsertMapper}.
     */
    private final InsertMapper<Activity> activityInsertMapperMock = context.mock(InsertMapper.class);

    /**
     * Mocked test instance of the {@link InsertActivityComment}.
     */
    private final InsertActivityComment commentInsertMapperMock = context.mock(InsertActivityComment.class);

    /**
     * Mocked test instance of the {@link BulkActivitiesMapper}.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapperMock = context.mock(DomainMapper.class);

    /**
     * Mocked test instance of the {@link RecipientRetriever}.
     */
    private final RecipientRetriever recipientRetrieverMock = context.mock(RecipientRetriever.class);

    /**
     * Mocked test instance of the
     * {@link PostActivityUpdateStreamsByActorMapper}.
     */
    private final PostActivityUpdateStreamsByActorMapper updateStreamsByActorMapperMock = context
	    .mock(PostActivityUpdateStreamsByActorMapper.class);

    /**
     * Mapper to get or insert shared resources.
     */
    private DomainMapper<SharedResourceRequest, SharedResource> findOrInsertSharedResourceMapper = context.mock(
	    DomainMapper.class, "findOrInsertSharedResourceMapper");

    /**
     * Mocked test instance of a {@link CommentDTO}.
     */
    private final CommentDTO testComment = context.mock(CommentDTO.class);

    /**
     * Test value for the Destination id.
     */
    private static final Long DESTINATION_ID = 123L;

    /**
     * Test value for a user id.
     */
    private static final Long USER_ID = 1L;

    /**
     * Test value for an Account Id.
     */
    private static final String ACCOUNT_ID = "testaccount";

    /**
     * Test value for the OpenSocial id.
     */
    private static final String OPENSOCIAL_ID = "testopensocial";

    /**
     * Cache.
     */
    private Cache cache = context.mock(Cache.class);
    
    /**
     * Person.
     */
    private PersonModelView person = context.mock(PersonModelView.class);

    /**
     * List of filters to apply to action.
     */
    private List<ActivityFilter> filters = new ArrayList<ActivityFilter>();

    /**
     * Filter.
     */
    private ActivityFilter filter = context.mock(ActivityFilter.class);
    
    /**
     * Mapper to get a person model view by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
	    DomainMapper.class, "personMapper");

    /**
     * Prepare the test suite.
     */
    @Before
    public void setup()
    {
	// Notice that the EntityType is Person, since group and person entity
	// types only determine
	// the type of notifications that are sent and that is not tested here,
	// picking person as
	// the default.
	filters.add(filter);
	
	sut = new PostActivityExecutionStrategy(activityInsertMapperMock, commentInsertMapperMock,
		activitiesMapperMock, recipientRetrieverMock, updateStreamsByActorMapperMock,
		findOrInsertSharedResourceMapper, cache, getPersonModelViewByAccountIdMapper, filters);
    }

    /**
     * This test ensures a successful execution of the business logic for a
     * Person.
     */
    @Test
    public void testSuccessfulExecute()
    {
	final ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
		PostActivityTestHelpers.DestinationStreamTestState.VALID, false, testComment, DESTINATION_ID);

	final List<ActivityDTO> activityResults = new ArrayList<ActivityDTO>();
	activityResults.add(currentActivity);

	final PostActivityRequest request = new PostActivityRequest(currentActivity);

	final Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, USER_ID);

	final ServiceActionContext actionContext = new ServiceActionContext(request, currentPrincipal);

	context.checking(new Expectations()
	{
	    {
		oneOf(recipientRetrieverMock).getStreamScope(currentActivity);

		oneOf(recipientRetrieverMock).isDestinationStreamPublic(currentActivity);

		oneOf(activityInsertMapperMock).execute(with(any(PersistenceRequest.class)));

		oneOf(activityInsertMapperMock).flush();

		oneOf(activitiesMapperMock).execute(with(any(List.class)));
		will(returnValue(activityResults));
		
		oneOf(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
		will(returnValue(person));

		oneOf(filter).filter(with(any(List.class)), with(person));
		
		oneOf(updateStreamsByActorMapperMock).execute(currentActivity);
	    }
	});

	TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerActionContext //
	= new TaskHandlerActionContext<PrincipalActionContext>(actionContext, new ArrayList<UserActionRequest>());
	sut.execute(currentTaskHandlerActionContext);

	context.assertIsSatisfied();
    }

    /**
     * Test submitting an activity with a url.
     */
    @Test
    public void testSuccessfulShareWithLink()
    {
	final ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
		PostActivityTestHelpers.DestinationStreamTestState.VALID, false, testComment, DESTINATION_ID);
	currentActivity.getBaseObjectProperties().put("targetUrl", "http://foo.com");

	final List<ActivityDTO> activityResults = new ArrayList<ActivityDTO>();
	activityResults.add(currentActivity);

	final PostActivityRequest request = new PostActivityRequest(currentActivity);

	final Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, USER_ID);

	final ServiceActionContext actionContext = new ServiceActionContext(request, currentPrincipal);

	final SharedResource sr = new SharedResource();

	final String expectedCacheKey = CacheKeys.SHARED_RESOURCE_BY_UNIQUE_KEY + "http://foo.com";

	context.checking(new Expectations()
	{
	    {
		oneOf(recipientRetrieverMock).getStreamScope(currentActivity);

		oneOf(recipientRetrieverMock).isDestinationStreamPublic(currentActivity);

		oneOf(activityInsertMapperMock).execute(with(any(PersistenceRequest.class)));

		oneOf(activityInsertMapperMock).flush();

		oneOf(activitiesMapperMock).execute(with(any(List.class)));
		will(returnValue(activityResults));

		oneOf(updateStreamsByActorMapperMock).execute(currentActivity);

		oneOf(findOrInsertSharedResourceMapper).execute(with(any(SharedResourceRequest.class)));
		will(returnValue(sr));

		oneOf(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
		will(returnValue(person));

		oneOf(filter).filter(with(any(List.class)), with(person));
		
		oneOf(cache).delete(expectedCacheKey);
	    }
	});

	TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerActionContext //
	= new TaskHandlerActionContext<PrincipalActionContext>(actionContext, new ArrayList<UserActionRequest>());
	sut.execute(currentTaskHandlerActionContext);

	Assert.assertEquals(3, currentTaskHandlerActionContext.getUserActionRequests().size());

	// make sure all of the actions are kicked off
	boolean postActivityAsyncActionFound = false;
	boolean createNotificationsActionFound = false;
	boolean deleteCacheKeysActionFound = false;

	for (UserActionRequest req : currentTaskHandlerActionContext.getUserActionRequests())
	{
	    if (req.getActionKey().equals("postActivityAsyncAction"))
	    {
		postActivityAsyncActionFound = true;
	    } 
	    else if (req.getActionKey().equals("createNotificationsAction"))
	    {
		createNotificationsActionFound = true;
	    } 
	    else if (req.getActionKey().equals("deleteCacheKeysAction"))
	    {
		deleteCacheKeysActionFound = true;
		Set<String> params = (Set<String>) req.getParams();
		Assert.assertEquals(1, params.size());
		for (String s : params)
		{
		    Assert.assertEquals(expectedCacheKey, s);
		}
	    }
	}

	Assert.assertTrue(postActivityAsyncActionFound);
	Assert.assertTrue(createNotificationsActionFound);
	Assert.assertTrue(deleteCacheKeysActionFound);

	context.assertIsSatisfied();
    }

    /**
     * This test ensures a successful execution of an activity share that
     * includes a comment.
     */
    @Test
    public void testSuccessfulShareWithComment()
    {
	final ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
		PostActivityTestHelpers.DestinationStreamTestState.VALID, true, testComment, DESTINATION_ID);

	final List<ActivityDTO> activityResults = new ArrayList<ActivityDTO>();
	activityResults.add(currentActivity);

	final PostActivityRequest request = new PostActivityRequest(currentActivity);

	final Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, USER_ID);

	final ServiceActionContext actionContext = new ServiceActionContext(request, currentPrincipal);

	context.checking(new Expectations()
	{
	    {
		oneOf(recipientRetrieverMock).getStreamScope(currentActivity);

		oneOf(recipientRetrieverMock).isDestinationStreamPublic(currentActivity);

		oneOf(activityInsertMapperMock).execute(with(any(PersistenceRequest.class)));

		oneOf(activityInsertMapperMock).flush();

		oneOf(activitiesMapperMock).execute(with(any(List.class)));
		will(returnValue(activityResults));

		oneOf(updateStreamsByActorMapperMock).execute(currentActivity);

		oneOf(testComment).getBody();
		
		oneOf(getPersonModelViewByAccountIdMapper).execute(ACCOUNT_ID);
		will(returnValue(person));
		
		oneOf(filter).filter(with(any(List.class)), with(person));

		oneOf(commentInsertMapperMock).execute(with(any(InsertActivityCommentRequest.class)));

	    }
	});

	TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerActionContext //
	= new TaskHandlerActionContext<PrincipalActionContext>(actionContext, new ArrayList<UserActionRequest>());
	sut.execute(currentTaskHandlerActionContext);

	context.assertIsSatisfied();
    }
}
