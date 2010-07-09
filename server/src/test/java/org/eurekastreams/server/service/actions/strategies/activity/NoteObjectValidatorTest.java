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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.service.actions.strategies.MapParameterLengthValidator;
import org.eurekastreams.server.service.actions.strategies.MapParameterValidator;
import org.eurekastreams.server.service.actions.strategies.MapParameterValidatorDecorator;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test the NoteObjectValidator.
 * 
 */
public class NoteObjectValidatorTest
{

    /**
     * Local instance of NoteObjectValidator for testing.
     */
    private NoteObjectValidator sut;

    /**
     * Local instance of map validator for testing.
     */
    private MapParameterValidatorDecorator mapRequiredValidator;

    /**
     * Local instance of map validator for testing length.
     */
    private MapParameterValidatorDecorator mapLengthValidator;

    /**
     * Local instance of ActivityDTO for testing.
     */
    private ActivityDTO testActivity;

    /**
     * Constant message to receive when a required field is missing.
     */
    private static final String REQUIRED_FIELD_ERROR = "You must supply content";

    /**
     * Constant message to receive when a field is longer than the max characters.
     */
    private static final String FIELD_LENGTH_ERROR = "Content must be less than 250 characters";

    /**
     * Max length constant for tests.
     */
    private static final int MAX_LENGTH = 250;

    /**
     * Over Max length constant for tests.
     */
    private static final int OVER_MAX_LENGTH = 251;

    /**
     * Setup method.
     */
    @Before
    public void setUp()
    {
        mapRequiredValidator = new MapParameterValidator("content", java.lang.String.class, REQUIRED_FIELD_ERROR);
        mapLengthValidator = new MapParameterLengthValidator("content", MAX_LENGTH, FIELD_LENGTH_ERROR);
        mapRequiredValidator.setMapParameterValidatorDecorator(mapLengthValidator);

        sut = new NoteObjectValidator(mapRequiredValidator);
    }

    /**
     * Test the successful validation path.
     */
    @Test
    public void testValidate()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("content", "good content");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.NOTE);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }

    /**
     * Test validation when required field is missing.
     */
    @Test
    public void testFailedRequireValidate()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("somethingElse", "good content");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.NOTE);
        testActivity.setBaseObjectProperties(testValueMap);

        final HashMap<String, String> errorMessages = new HashMap<String, String>();
        errorMessages.put("content", REQUIRED_FIELD_ERROR);

        final ValidationException vex = new ValidationException();
        vex.setErrors(errorMessages);

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException veex)
        {
            assertTrue(veex.getErrors().containsKey("content"));
            assertEquals(REQUIRED_FIELD_ERROR, veex.getErrors().get("content"));
        }
    }

    /**
     * Test validation when required field is missing.
     */
    @Test
    public void testFailedLengthValidate()
    {
        // Create a string longer than the max length of 250.
        int count = OVER_MAX_LENGTH;
        char[] chars = new char[count];
        while (count > 0)
        {
            chars[--count] = 'x';
        }
        String longString = new String(chars);

        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("content", longString);

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.NOTE);
        testActivity.setBaseObjectProperties(testValueMap);

        final HashMap<String, String> errorMessages = new HashMap<String, String>();
        errorMessages.put("content", FIELD_LENGTH_ERROR);

        final ValidationException vex = new ValidationException();
        vex.setErrors(errorMessages);

        try
        {
            sut.validate(testActivity);
        }
        catch (ValidationException veex)
        {
            assertTrue(veex.getErrors().containsKey("content"));
            assertEquals(FIELD_LENGTH_ERROR, veex.getErrors().get("content"));
        }
    }

}
