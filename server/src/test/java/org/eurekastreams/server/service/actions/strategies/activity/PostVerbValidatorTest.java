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

import static junit.framework.Assert.assertTrue;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the PostVerbValidator functionality.
 * 
 */
public class PostVerbValidatorTest
{
    /**
     * Local instance of Activity for tests.
     */
    private ActivityDTO testActivity;

    /**
     * Local instance of Actor StreamEntitDTO for tests.
     */
    private StreamEntityDTO testActorEntityDTO;

    /**
     * Local instance of PostVerbValidator - System Under Test.
     */
    private PostVerbValidator sut;

    /**
     * Constant string id for tests.
     */
    private static final String UNIQUE_ID = "unique";

    /**
     * Setup the sut for testing.
     */
    @Before
    public void setUp()
    {
        sut = new PostVerbValidator();
    }

    /**
     * Test the successful path of the validation.
     */
    @Test
    public void testValidate()
    {
        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.POST);

        sut.validate(testActivity);
    }

    /**
     * Test validation if the original actor is supplied.
     */
    @Test
    public void testFullOriginalActorValidate()
    {
        testActorEntityDTO = new StreamEntityDTO();
        testActorEntityDTO.setUniqueIdentifier(UNIQUE_ID);

        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.POST);
        testActivity.setOriginalActor(testActorEntityDTO);

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().size() > 0);
        }
    }

    /**
     * Test the validation if the original actor is there with a null unique id.
     */
    @Test
    public void testPartialOriginalActorValidate()
    {
        testActorEntityDTO = new StreamEntityDTO();
        testActorEntityDTO.setUniqueIdentifier(null);

        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.POST);
        testActivity.setOriginalActor(testActorEntityDTO);

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().size() > 0);
        }
    }

    /**
     * Test the validation if the original actor is there with an empty string unique id.
     */
    @Test
    public void testMissingUniqueIdOriginalActorValidate()
    {
        testActorEntityDTO = new StreamEntityDTO();
        testActorEntityDTO.setUniqueIdentifier("");

        testActivity = new ActivityDTO();
        testActivity.setVerb(ActivityVerb.POST);
        testActivity.setOriginalActor(testActorEntityDTO);

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().size() > 0);
        }
    }
}
