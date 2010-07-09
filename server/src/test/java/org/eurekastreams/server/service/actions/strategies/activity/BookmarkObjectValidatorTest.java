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
 * Class that tests the BookMarkObjectValidator functionality.
 * 
 */
public class BookmarkObjectValidatorTest
{
    /**
     * Local instance of BookmarkObjectValidator for testing.
     */
    private BookmarkObjectValidator sut;

    /**
     * Local instance of map validator for testing.
     */
    private MapParameterValidatorDecorator mapTargetUrlRequiredValidator;

    /**
     * Local instance of map validator for testing.
     */
    private MapParameterValidatorDecorator mapTargetTitleRequiredValidator;

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
    private static final String TARGETURL_REQUIRED_FIELD_ERROR = "You must supply the target url";

    /**
     * Constant message to receive when a required field is missing.
     */
    private static final String TARGETTITLE_REQUIRED_FIELD_ERROR = "You must supply the target title";

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
     * Setup system under test.
     */
    @Before
    public void setUp()
    {
        mapTargetTitleRequiredValidator = new MapParameterValidator("targetTitle", java.lang.String.class,
                TARGETTITLE_REQUIRED_FIELD_ERROR);
        mapTargetUrlRequiredValidator = new MapParameterValidator("targetUrl", java.lang.String.class,
                TARGETURL_REQUIRED_FIELD_ERROR);
        mapLengthValidator = new MapParameterLengthValidator("content", MAX_LENGTH, FIELD_LENGTH_ERROR);
        mapTargetUrlRequiredValidator.setMapParameterValidatorDecorator(mapLengthValidator);
        mapTargetTitleRequiredValidator.setMapParameterValidatorDecorator(mapTargetUrlRequiredValidator);

        sut = new BookmarkObjectValidator(mapTargetTitleRequiredValidator);
    }

    /**
     * tests the successful path for validation.
     */
    @Test
    public void testValidate()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("content", "good content");
        testValueMap.put("targetTitle", "title");
        testValueMap.put("targetUrl", "testurl");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }

    /**
     * tests the successful path for validation without content.
     */
    @Test
    public void testValidateWithoutContent()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("targetTitle", "title");
        testValueMap.put("targetUrl", "testurl");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }

    /**
     * tests content too large validation.
     */
    @Test(expected = ValidationException.class)
    public void testContentTooLargeValidate()
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
        testValueMap.put("targetTitle", "title");
        testValueMap.put("targetUrl", "testurl");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }

    /**
     * tests the successful path for validation.
     */
    @Test(expected = ValidationException.class)
    public void testMissingTargetTitleValidate()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("content", "good content");
        testValueMap.put("targetUrl", "testurl");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }

    /**
     * tests the successful path for validation.
     */
    @Test(expected = ValidationException.class)
    public void testMissingTargetUrlValidate()
    {
        HashMap<String, String> testValueMap = new HashMap<String, String>();
        testValueMap.put("content", "good content");
        testValueMap.put("targetTitle", "title");

        testActivity = new ActivityDTO();
        testActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        testActivity.setBaseObjectProperties(testValueMap);

        sut.validate(testActivity);
    }
}
