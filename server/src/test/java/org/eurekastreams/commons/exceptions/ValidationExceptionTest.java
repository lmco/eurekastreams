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
package org.eurekastreams.commons.exceptions;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

/**
 * Test class.
 */
public class ValidationExceptionTest
{
    /**
     * test.
     */
    @Test
    public void testProperties()
    {
        String message = "validation exception properties should work";

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("foo", "bar");

        ValidationException validation = new ValidationException();
        validation.setErrors(map);

        assertEquals(message, map, validation.getErrors());

        validation.addError("foo1", "bar1");
        assertEquals(2, validation.getErrors().size());

    }

    /**
     * Test constructor w/message.
     */
    @Test
    public void testConstructorWithMessage()
    {
        ValidationException sut = new ValidationException("msg");
        assertEquals("msg", sut.getMessage());
    }

}
