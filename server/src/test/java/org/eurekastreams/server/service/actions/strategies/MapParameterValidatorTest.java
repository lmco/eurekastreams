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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.junit.Test;

/**
 * Test for MapParameterValidator.
 * 
 */
public class MapParameterValidatorTest
{
    /**
     * System under test.
     */
    private MapParameterValidator sut;

    /**
     * Test validate.
     */
    @Test
    public void testValidateStringPass()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "i");

        sut = new MapParameterValidator("key", String.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateFailEmptyList()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        ArrayList<StreamScope> scopes = new ArrayList<StreamScope>();
        map.put("key", scopes);

        sut = new MapParameterValidator("key", List.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test
    public void testValidatePassList()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        ArrayList<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.PERSON, "key"));
        map.put("key", scopes);

        sut = new MapParameterValidator("key", List.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateStringFailNull()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", null);

        sut = new MapParameterValidator("key", String.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateStringFailType()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", new Long(4));

        sut = new MapParameterValidator("key", String.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateStringFailEmpty()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "");

        sut = new MapParameterValidator("key", String.class, "message here");
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test
    public void testValidateDecoration()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "i");
        map.put("long", 4L);

        sut = new MapParameterValidator("key", String.class, "message here");
        MapParameterValidator sut1 = new MapParameterValidator("long", Long.class, "message here");
        sut.setMapParameterValidatorDecorator(sut1);
        sut.validate(map, new HashMap<String, String>());
    }

    /**
     * Test validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateDecorationFail()
    {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("key", "i");
        map.put("long", "i");

        sut = new MapParameterValidator("key", String.class, "message here");
        MapParameterValidator sut1 = new MapParameterValidator("long", Long.class, "message here");
        sut.setMapParameterValidatorDecorator(sut1);
        sut.validate(map, new HashMap<String, String>());
    }

}
