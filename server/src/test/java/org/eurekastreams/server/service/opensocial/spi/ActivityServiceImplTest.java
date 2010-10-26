/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.opensocial.spi;

import static junit.framework.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.EnumUtil;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class performs the test for the implementation of the Shindig ActivityService interface.
 *
 */
public class ActivityServiceImplTest
{
    /**
     * List of activity expected to be returned from the search for users by opensocial id.
     */
    private final List<org.eurekastreams.server.domain.stream.Activity> expectedActivities = // \n
            new LinkedList<org.eurekastreams.server.domain.stream.Activity>();

    /**
     * List os ActivityDTO's expected to be returned from the search for users by opensocial id.
     */
    private final List<ActivityDTO> expectedActivityDTOs = new LinkedList<ActivityDTO>();

    /**
     * List of activity expected to be returned from the search for users by opensocial id.
     */
    private final Set<String> activityIds = new HashSet<String>();

    /**
     * Object that is being tested.
     */
    private ActivityServiceImpl sut;

    /**
     * This is a fake security token taken from Shindig for testing.
     */
    private static final SecurityToken FAKETOKEN = new FakeGadgetToken();

    /**
     * A test GroupId object to be used during the tests.
     */
    private final GroupId testGroupId = new GroupId(GroupId.Type.self, "654321");

    /**
     * String to use for test application ids.
     */
    private static final String TEST_APP_ID = "192";

    /**
     * String to use for test activity ids.
     */
    private static final String TEST_ACTIVITY_ID = "1";

    /**
     * Set of fields to use for Activity Field Testing.
     */
    private static final Set<String> ACTIVITY_ALL_FIELDS = EnumUtil.getEnumStrings(Activity.Field.values());

    /**
     * Constant string describing the opensocial id for a test user.
     */
    private static final String USERID_ONE = "123456";

    /**
     * Constant string describing the opensocial id for a test user.
     */
    private static final String ACTIVITYID_ONE = "123456";

    /**
     * Constant string describing the opensocial id for a test user.
     */
    private static final String ACTIVITYID_TWO = "654321";

    /**
     * A test UserId object to be used during the tests.
     */
    private final UserId testId = new UserId(Type.userId, ACTIVITYID_ONE);

    /**
     * A test UserId object to be used during the tests.
     */
    private final UserId testId2 = new UserId(Type.userId, ACTIVITYID_TWO);

    /**
     * Activity implementation for tests.
     */
    private  Activity testActivity;

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
     * The mock action to be used for retrieving activities.
     */
    private final ServiceAction getUserActivitiesAction = context.mock(ServiceAction.class, "getUserActivitiesAction");

    /**
     * The mock action to be used for delete activities.
     */
    private final TaskHandlerAction deleteUserActivitiesAction =
            context.mock(TaskHandlerAction.class, "deleteUserActivitiesAction");

    /**
     * The mock instance of the {@link ServiceActionController} to execute actions.
     */
    private final ServiceActionController serviceActionControllerMock = context.mock(ServiceActionController.class);

    /**
     * The mock instance of the {@link OpenSocialPrincipalPopulator} for retrieving a principal object using the
     * OpenSocial id.
     */
    private final OpenSocialPrincipalPopulator openSocialPrincipalPopulatorMock =
            context.mock(OpenSocialPrincipalPopulator.class);

    /**
     * The mock instance of the {@link TaskHandlerAction}TaskHandlerServiceActiontivity Action.
     */
    private final TaskHandlerServiceAction postActivityServiceActionMock =
            context.mock(TaskHandlerServiceAction.class);

    /** Fixture: gadgetDefMapper. */
    private final GadgetDefinitionMapper gadgetDefMapper = context.mock(GadgetDefinitionMapper.class);

    /**
     * TaskHandlerServiceActionprincipal object for an action.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the test.
     */
    @Before
    public void setUp()
    {
        sut =
                new ActivityServiceImpl(getUserActivitiesAction, deleteUserActivitiesAction,
                        serviceActionControllerMock, openSocialPrincipalPopulatorMock, postActivityServiceActionMock,
                        gadgetDefMapper);

        testActivity = new ActivityImpl(TEST_ACTIVITY_ID, testId.getUserId());

        activityIds.add("1");
        activityIds.add("2");
        activityIds.add("3");
    }

    /**
     * Test for deleting activities.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testDeleteActivities() throws Exception
    {
        context.checking(new Expectations()
        {
            {

                allowing(openSocialPrincipalPopulatorMock).getPrincipal(USERID_ONE);
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerAction.class)));
                will(returnValue(null));
            }
        });

        sut.deleteActivities(testId, testGroupId, TEST_APP_ID, activityIds, FAKETOKEN);

        context.assertIsSatisfied();
    }

    /**
     * Test for creating an activity.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testCreateActivity() throws Exception
    {
        final GadgetDefinition gadgetDef = new GadgetDefinition();

        context.checking(new Expectations()
        {
            {
                oneOf(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(principalMock).getOpenSocialId();

                oneOf(principalMock).getAccountId();

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerAction.class)));

                oneOf(gadgetDefMapper).findById(Long.parseLong(TEST_APP_ID));
                will(returnValue(gadgetDef));
            }
        });

        sut.createActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, testActivity, FAKETOKEN);

        context.assertIsSatisfied();
    }

    /**
     * Test for creating an activity.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testCreateActivityFile() throws Exception
    {
        testActivity.setTemplateParams(new HashMap<String, String>());
        testActivity.getTemplateParams().put("baseObjectType", "FILE");
        final GadgetDefinition gadgetDef = new GadgetDefinition();

        context.checking(new Expectations()
        {
            {
                oneOf(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(principalMock).getOpenSocialId();

                oneOf(principalMock).getAccountId();

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerAction.class)));

                oneOf(gadgetDefMapper).findById(Long.parseLong(TEST_APP_ID));
                will(returnValue(gadgetDef));
            }
        });

        sut.createActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, testActivity, FAKETOKEN);

        context.assertIsSatisfied();
    }

    /**
     * Test for creating an activity.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test(expected = ProtocolException.class)
    public void testCreateActivityFailure() throws Exception
    {
        final GadgetDefinition gadgetDef = new GadgetDefinition();

        context.checking(new Expectations()
        {
            {
                oneOf(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(principalMock).getOpenSocialId();

                oneOf(principalMock).getAccountId();

                oneOf(gadgetDefMapper).findById(Long.parseLong(TEST_APP_ID));
                will(returnValue(gadgetDef));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerAction.class)));
                will(throwException(new GeneralException()));
            }

        });

        sut.createActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, testActivity, FAKETOKEN);

        context.assertIsSatisfied();
    }

    /**
     * Test the getActivity method in the ActivityService implementation.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testGetActivity() throws Exception
    {
        final String testActivityId = "123";

        buildActivityDTOs();
        context.checking(new Expectations()
        {
            {

                allowing(openSocialPrincipalPopulatorMock).getPrincipal(USERID_ONE);
                will(returnValue(principalMock));

                allowing(principalMock).getAccountId();
                will(returnValue("foo"));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(expectedActivityDTOs));
            }
        });

        sut.getActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, testActivityId, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * Test forcing an Exception.
     *
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ProtocolException.class)
    public void testGetActivityException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(openSocialPrincipalPopulatorMock).getPrincipal(USERID_ONE);
                will(returnValue(principalMock));

                allowing(principalMock).getAccountId();
                will(returnValue("foo"));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new GeneralException()));
            }
        });

        sut.getActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, TEST_ACTIVITY_ID, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * currentUser Test forcing a NumberFormatException.
     *
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ProtocolException.class)
    public void testGetActivityNumberFormatException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(openSocialPrincipalPopulatorMock).getPrincipal(USERID_ONE);
                will(returnValue(principalMock));

                allowing(principalMock).getAccountId();
                will(returnValue("foo"));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new NumberFormatException()));
            }
        });

        sut.getActivity(testId, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, TEST_ACTIVITY_ID, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    // /**
    // * Test forcing a NumberFormatException.
    // * @throws Exception - covers all exceptions.
    // */
    // @Test(expected = ProtocolException.class)
    // public void testGetActivityNullId() throws Exception
    // {
    // Activity innerTestActivity = sut.getActivity(testNullId, testGroupId, TEST_APP_ID,
    // ACTIVITY_ALL_FIELDS, TEST_ACTIVITY_ID, FAKETOKEN).get();
    // }
    //
    /**
     * Test stub for unimplemented method. This is necessary for code coverage and because all methods for Shindig need
     * to be implemented to not cause runtime errors even though we don't currently have implementations for all methods
     * yet.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testGetActivities() throws Exception
    {
        LinkedHashSet<UserId> userIdSet = new LinkedHashSet<UserId>();
        userIdSet.add(testId);
        userIdSet.add(testId2);
        CollectionOptions collOptions = new CollectionOptions();

        buildActivityDTOs();
        context.checking(new Expectations()
        {
            {
                allowing(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                allowing(principalMock).getAccountId();
                will(returnValue("foo"));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(expectedActivityDTOs));
            }
        });

        RestfulCollection<Activity> activities =
                sut.getActivities(userIdSet, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, collOptions, FAKETOKEN)
                        .get();

        assertNotNull("Collection of activities is null", activities);

        context.assertIsSatisfied();
    }

    /**
     * This test exercises the GetActivities method of the OpenSocial implementation in Shindig. This test throws an
     * exception to test error handling.
     *
     * @throws Exception
     *             - on unhandled errors.
     */
    @Test(expected = ProtocolException.class)
    public void testGetActivitiesThrowsException() throws Exception
    {
        LinkedHashSet<UserId> userIdSet = new LinkedHashSet<UserId>();
        userIdSet.add(testId);
        userIdSet.add(testId2);
        CollectionOptions collOptions = new CollectionOptions();

        context.checking(new Expectations()
        {
            {
                allowing(openSocialPrincipalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                allowing(principalMock).getAccountId();
                will(returnValue("foo"));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new GeneralException()));
            }
        });

        sut.getActivities(userIdSet, testGroupId, TEST_APP_ID, ACTIVITY_ALL_FIELDS, collOptions, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * Build the basic collection of activitydto's that will be returned in the GetActivities tests.
     */
    private void buildActivityDTOs()
    {
        org.eurekastreams.server.domain.Person user = new org.eurekastreams.server.domain.Person();
        user.setOpenSocialId(ACTIVITYID_ONE);

        HashMap<String, String> testBaseObjectProperties = new HashMap<String, String>();
        testBaseObjectProperties.put("Context", "Test");

        StreamEntityDTO testDestinationStream = new StreamEntityDTO();
        testDestinationStream.setDisplayName("test");

        StreamEntityDTO testActor = new StreamEntityDTO();
        testActor.setUniqueIdentifier("testaccount");

        ActivityDTO authorActivity = new ActivityDTO();
        authorActivity.setBaseObjectType(BaseObjectType.NOTE);
        authorActivity.setBaseObjectProperties(testBaseObjectProperties);
        authorActivity.setActor(testActor);
        authorActivity.setDestinationStream(testDestinationStream);
        authorActivity.setId(2L);
        authorActivity.setPostedTime(new Date());

        ActivityDTO subjectActivity = new ActivityDTO();
        subjectActivity.setBaseObjectType(BaseObjectType.NOTE);
        subjectActivity.setBaseObjectProperties(testBaseObjectProperties);
        subjectActivity.setActor(testActor);
        subjectActivity.setDestinationStream(testDestinationStream);
        subjectActivity.setId(1L);
        subjectActivity.setPostedTime(new Date());

        expectedActivityDTOs.add(authorActivity);
        expectedActivityDTOs.add(subjectActivity);
    }
}
