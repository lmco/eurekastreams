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

package org.eurekastreams.server.service.actions.strategies.activity;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.service.actions.TransactionManagerFake;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;

/**
 * This class tests the ShareVerbValidator.
 * 
 */
public class ShareVerbValidatorTest
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.getLog(ShareVerbValidatorTest.class);

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Local instance of the ShareVerbValidator that represents the System under test.
     */
    private ShareVerbValidator sut;

    /**
     * Test instance of the BulkActivitiesMapper.
     */
    private final BulkActivitiesMapper activityMapperMock = context.mock(BulkActivitiesMapper.class);

    /**
     * Test instance of the GetDomainGroupsByShortNames.
     */
    private final GetDomainGroupsByShortNames groupMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mocked transaction manager for testing.
     */
    private TransactionManagerFake transMgr = context.mock(TransactionManagerFake.class);

    /**
     * instance of a TransactionDefinition.
     */
    private DefaultTransactionDefinition transDef = null;

    /**
     * Mocked instance of a TransactionStatus.
     */
    private TransactionStatus transStatus = context.mock(TransactionStatus.class);

    /**
     * Local instance of ActivityDTO for testing.
     */
    private ActivityDTO testActivity;

    /**
     * Local instance of ActivityDTO for testing of Original Activity.
     */
    private ActivityDTO testOriginalActivity;

    /**
     * Local instance of Original Activity Actor.
     */
    private StreamEntityDTO testOriginalActor;

    /**
     * The destination Steam of original Activity.
     */
    private StreamEntityDTO destinationStream;

    /**
     * Local instance of Base Object Properties for testing. important when checking the orginal activity.
     */
    private HashMap<String, String> testBaseObjectProperties;

    /**
     * Constant value for original actor id.
     */
    private static final String ORIGINAL_ACTOR_UNIQUE_ID = "originalactor";

    /**
     * Constant value for the original activity id.
     */
    private static final Long ORIGINAL_ACTIVITY_ID = 1L;

    /**
     * Constant value for the original activity id.
     */
    private static final Long FAIL_ORIGINAL_ACTIVITY_ID = 2L;

    /**
     * Method for prepping the System Under Test.
     */
    @Before
    public void setUp()
    {
        transDef = new DefaultTransactionDefinition();
        transDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        transDef.setName("TestTransDef");

        destinationStream = new StreamEntityDTO();

        sut = new ShareVerbValidator(activityMapperMock, transMgr, groupMapperMock);

        testOriginalActivity = new ActivityDTO();
        testOriginalActivity.setVerb(ActivityVerb.SHARE);

        testOriginalActor = new StreamEntityDTO();
        testOriginalActor.setUniqueIdentifier(ORIGINAL_ACTOR_UNIQUE_ID);

        testOriginalActivity.setActor(testOriginalActor);

        testBaseObjectProperties = new HashMap<String, String>();
        testBaseObjectProperties.put("originalActivityId", ORIGINAL_ACTIVITY_ID.toString());
        testBaseObjectProperties.put("targetUrl", "target url");
        testBaseObjectProperties.put("targetTitle", "targetTitle");

        testOriginalActivity.setBaseObjectProperties(testBaseObjectProperties);
        testOriginalActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
    }

    /**
     * Test the successful validation path.
     */
    @Test
    public void testValidate()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);

        testActivity.setOriginalActor(testOriginalActor);
        testActivity.setBaseObjectProperties(testBaseObjectProperties);
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(ORIGINAL_ACTIVITY_ID);

        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();
        matchingActivities.add(testOriginalActivity);

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(transMgr).commit(transStatus);
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            logger.debug("Caught validation exception.");
            for (Entry<String, String> currentError : vex.getErrors().entrySet())
            {
                logger.debug("Error key: " + currentError.getKey() + " value: " + currentError.getValue());
            }
            throw vex;
        }
        context.assertIsSatisfied();
    }

    /**
     * Test failure on Original Actor missing.
     */
    @Test
    public void testValidateOriginalActorMissing()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);

        testActivity.setOriginalActor(null);

        testActivity.setBaseObjectProperties(testBaseObjectProperties);
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("OriginalActor"));
            assertTrue(vex.getErrors().get("OriginalActor").equals("Must be included for Share verbs."));
        }
        context.assertIsSatisfied();
    }

    /**
     * Test the failure on no activity results returned.
     */
    @Test
    public void testValidateEmptyActivityResults()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);

        testActivity.setOriginalActor(testOriginalActor);

        testActivity.setBaseObjectProperties(testBaseObjectProperties);
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(ORIGINAL_ACTIVITY_ID);

        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(transMgr).rollback(transStatus);
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("OriginalActivity"));
            assertTrue(vex.getErrors().get("OriginalActivity")
                    .equals("Error occurred accessing the original activity."));
        }
        
        context.assertIsSatisfied();
    }

    /**
     * Test the failure multiple activities being returned.
     */
    @Test
    public void testFailureValidate()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);

        StreamEntityDTO mismatchedOriginalActor = new StreamEntityDTO();
        mismatchedOriginalActor.setUniqueIdentifier("something");
        testActivity.setOriginalActor(mismatchedOriginalActor);

        HashMap<String, String> mismatchedObjProperties = new HashMap<String, String>();
        mismatchedObjProperties.put("originalActivityId", FAIL_ORIGINAL_ACTIVITY_ID.toString());
        testActivity.setBaseObjectProperties(mismatchedObjProperties);
        testActivity.setBaseObjectType(BaseObjectType.NOTE);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(FAIL_ORIGINAL_ACTIVITY_ID);

        // Expect two results to trigger an error.
        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();
        matchingActivities.add(new ActivityDTO());
        matchingActivities.add(new ActivityDTO());

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(transMgr).commit(transStatus);
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("OriginalActivity"));
            assertTrue(vex.getErrors().get("OriginalActivity").equals(
                    "more than one result was found for the original activity id."));
            assertTrue(vex.getErrors().containsKey("BaseObjectProperties"));
            assertTrue(vex.getErrors().get("BaseObjectProperties").equals(
                    "Oringal Activity BaseObjectProperties must equal the properties of the Shared Activity."));
            assertTrue(vex.getErrors().containsKey("OriginalActivity"));
            assertTrue(vex.getErrors().get("OriginalActor").equals(
                    "Original actor of the shared activity does not match the actor of the original activity."));
            assertTrue(vex.getErrors().containsKey("BaseObjectType"));
            assertTrue(vex.getErrors().get("BaseObjectType").equals(
                    "activity must be of the same type as the original activity."));
        }
        context.assertIsSatisfied();
    }

    /**
     * Test to make sure that if you are sharing an activity from a private group it fails.
     */
    @Test
    public void testSharePrivateGroupFailureValidate()
    {

        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);
        testActivity.setOriginalActor(testOriginalActor);
        testActivity.setBaseObjectProperties(testBaseObjectProperties);

        StreamEntityDTO mismatchedOriginalActor = new StreamEntityDTO();
        mismatchedOriginalActor.setUniqueIdentifier("something");
        testActivity.setOriginalActor(mismatchedOriginalActor);

        final String testGroupId = "testGroup";

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(ORIGINAL_ACTIVITY_ID);

        // set up private test group
        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(false);

        destinationStream.setUniqueIdentifier("testGroup");
        destinationStream.setType(EntityType.GROUP);

        // one result of private group
        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();
        ActivityDTO orgActivity = new ActivityDTO();
        orgActivity.setDestinationStream(destinationStream);
        matchingActivities.add(orgActivity);

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(groupMapperMock).fetchUniqueResult(testGroupId);
                will(returnValue(testGroup));

                oneOf(transMgr).rollback(transStatus);
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("OriginalActivity"));
            assertTrue(vex.getErrors().get("OriginalActivity")
                    .equals("Error occurred accessing the original activity."));
        }
        context.assertIsSatisfied();
    }

    /**
     * Test to make sure a group is not returned the correct error is thrown.
     */
    @Test
    public void testSharePrivateGroupNullStreamReturnedFailureValidate()
    {
        final String testGroupId = "testGroup";

        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);
        testActivity.setOriginalActor(testOriginalActor);
        testActivity.setBaseObjectProperties(testBaseObjectProperties);

        StreamEntityDTO mismatchedOriginalActor = new StreamEntityDTO();
        mismatchedOriginalActor.setUniqueIdentifier("something");
        testActivity.setOriginalActor(mismatchedOriginalActor);

        final List<String> groupIds = new ArrayList<String>();
        groupIds.add("testGroup");

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(ORIGINAL_ACTIVITY_ID);

        // set up private test group
        final DomainGroupModelView testGroup = null;

        destinationStream.setUniqueIdentifier("testGroup");
        destinationStream.setType(EntityType.GROUP);

        // one result of private group
        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();
        ActivityDTO orgActivity = new ActivityDTO();
        orgActivity.setDestinationStream(destinationStream);
        matchingActivities.add(orgActivity);

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(groupMapperMock).fetchUniqueResult(testGroupId);
                will(returnValue(testGroup));

                oneOf(transMgr).rollback(transStatus);
            }
        });

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("OriginalActivity"));
            assertTrue(vex.getErrors().get("OriginalActivity")
                    .equals("Error occurred accessing the original activity."));
        }
        context.assertIsSatisfied();
    }

    /**
     * Test the success of attempting to share a public group message.
     */
    @Test
    public void testSharePublicGroupSuccessValidate()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.SHARE);
        testActivity.setOriginalActor(testOriginalActor);
        testActivity.setBaseObjectProperties(testBaseObjectProperties);
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);

        final String testGroupId = "testGroup";

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(ORIGINAL_ACTIVITY_ID);

        final List<ActivityDTO> matchingActivities = new ArrayList<ActivityDTO>();
        testOriginalActivity.setDestinationStream(destinationStream);
        matchingActivities.add(testOriginalActivity);

        final List<String> groupIds = new ArrayList<String>();
        groupIds.add("testGroup");

        // set up private test group
        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(true);

        destinationStream.setUniqueIdentifier("testGroup");
        destinationStream.setType(EntityType.GROUP);

        context.checking(new Expectations()
        {
            {
                oneOf(transMgr).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(activityMapperMock).execute(activityIds, null);
                will(returnValue(matchingActivities));

                oneOf(groupMapperMock).fetchUniqueResult(testGroupId);
                will(returnValue(testGroup));

                oneOf(transMgr).commit(transStatus);
            }
        });

        sut.validate(testActivity);
        context.assertIsSatisfied();
    }
}
