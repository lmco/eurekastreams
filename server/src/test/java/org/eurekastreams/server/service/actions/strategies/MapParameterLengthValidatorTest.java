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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.junit.Test;

/**
 * This class is responsible for testing the MapParameterLengthValidator.
 * 
 */
public class MapParameterLengthValidatorTest
{
    /**
     * System under test.
     */
    private MapParameterLengthValidator sut;

    /**
     * Constant test length.
     */
    private static final int MAX_TEST_LENGTH = 250;

    /**
     * Constant test length.
     */
    private static final int SMALL_MAX_TEST_LENGTH = 10;

    /**
     * Test successful validation.
     */
    @Test
    public void testValidate()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "i");

        sut = new MapParameterLengthValidator("key", MAX_TEST_LENGTH, "Length is invalid");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test failure on length.
     */
    @Test(expected = ValidationException.class)
    public void testLengthFailureValidate()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "1234567890123");

        sut = new MapParameterLengthValidator("key", SMALL_MAX_TEST_LENGTH, "Length is invalid");
        sut.validate(map, new HashMap<String, String>());
    }
}
