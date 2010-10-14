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
package org.eurekastreams.server.action.validation.stream;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityValidator;
import org.eurekastreams.server.service.actions.strategies.activity.PostVerbValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class contains tests for the {@link PostActivityValidationStrategy} class.
 *
 */
public class PostActivityValidationStrategyTest
{
    /**
     * System under test.
     */
    private PostActivityValidationStrategy sut;

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
     * Local instance of PostVerbValidator.
     */
    private final PostVerbValidator postVerbValMock = context.mock(PostVerbValidator.class);

    /**
     * Local instance of validator.
     */
    private final ActivityValidator noteObjValMock = context.mock(ActivityValidator.class);

    /**
     * Test comment.
     */
    private final CommentDTO testComment = context.mock(CommentDTO.class);

    /**
     * Test destination id.
     */
    private static final Long DESTINATION_ID = 1L;

    /**
     * Test account id.
     */
    private static final String ACCOUNT_ID = "testaccount";

    /**
     * Test OpenSocial id.
     */
    private static final String OPENSOCIAL_ID = "opensocialid";

    /**
     * Test id.
     */
    private static final Long ID = 1L;

    /**
     * Local instance of the PostActivityRequest object assembled for tests.
     */
    private PostActivityRequest currentRequest;

    /**
     * Local instance of the current principal object assembled for tests.
     */
    private Principal currentPrincipal;

    /**
     * Local instance of the ServiceActionContext object assembled for tests.
     */
    private ServiceActionContext currentActionContext;

    /**
     * Setup the system under test.
     */
    @Before
    public void setup()
    {
        sut = new PostActivityValidationStrategy(getVerbValidators(), getObjectValidators());

        currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
    }

    /**
     * Test successful validation of the action.
     */
    @Test
    public void testSuccessfulValidation()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.VALID, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);

        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                oneOf(postVerbValMock).validate(with(any(ActivityDTO.class)));

                oneOf(noteObjValMock).validate(with(any(ActivityDTO.class)));
            }
        });

        sut.validate(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Tests that the Check for Verb Validator presence in the Map fails correctly.
     */
    @Test(expected = ValidationException.class)
    public void testVerbTypeFailure()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.VALID, true, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Tests that the VerbValidator fails correctly.
     */
    @Test(expected = ValidationException.class)
    public void testVerbValidatorFailure()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.VALID, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                oneOf(postVerbValMock).validate(with(any(ActivityDTO.class)));
                will(throwException(new ValidationException()));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test that the ActivityValidator fails correctly.
     */
    @Test(expected = ValidationException.class)
    public void testObjectValidatorFailure()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.VALID, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                oneOf(postVerbValMock).validate(with(any(ActivityDTO.class)));

                oneOf(noteObjValMock).validate(with(any(ActivityDTO.class)));
                will(throwException(new ValidationException()));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Tests that a Null Destination Stream fails validation.
     */
    @Test(expected = ValidationException.class)
    public void testNullDestinationStream()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.NULLSTREAM, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        sut.validate(currentActionContext);
    }

    /**
     * Tests that a Null Destination Stream Identifier fails validation.
     */
    @Test(expected = ValidationException.class)
    public void testNullDestinationStreamIdentifier()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.NULLIDENTIFIER, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        sut.validate(currentActionContext);
    }

    /**
     * Tests that an Empty Destination Stream Identifier fails validation.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyDestinationStreamIdentifier()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.EMPTYIDENTIFIER, true, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        sut.validate(currentActionContext);
    }

    /**
     * Tests that an Empty Destination Stream Identifier fails validation.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidDestinationStreamIdentifier()
    {
        ActivityDTO currentActivity = PostActivityTestHelpers.buildActivityDTO(
                PostActivityTestHelpers.DestinationStreamTestState.INVALIDTYPE, false, testComment, DESTINATION_ID);

        currentRequest = new PostActivityRequest(currentActivity);
        currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        sut.validate(currentActionContext);
    }

    /**
     * Prepare the hashmap of VerbValidators.
     * @return - Map of verbvalidators.
     */
    private Map<String, ActivityValidator> getVerbValidators()
    {
        Map<String, ActivityValidator> verbValidators = new HashMap<String, ActivityValidator>();

        verbValidators.put(ActivityVerb.POST.name(), postVerbValMock);

        return verbValidators;
    }

    /**
     * Prepare the hashmap of ObjectValidators.
     * @return - Map of objectvalidators.
     */
    private Map<String, ActivityValidator> getObjectValidators()
    {
        Map<String, ActivityValidator> objectValidators = new HashMap<String, ActivityValidator>();

        objectValidators.put(BaseObjectType.NOTE.name(), noteObjValMock);

        return objectValidators;
    }

}
