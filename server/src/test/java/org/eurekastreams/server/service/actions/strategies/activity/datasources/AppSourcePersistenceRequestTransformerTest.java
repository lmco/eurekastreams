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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static org.junit.Assert.assertEquals;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the authored by transformer used by the persistence data source.
 */
public class AppSourcePersistenceRequestTransformerTest
{
    /**
     * System under test.
     */
    private AppSourcePersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        sut = new AppSourcePersistenceRequestTransformer();
    }

    /**
     * Tests transformation with handled types.
     */
    @Test
    public void testTransform()
    {
        final String rqst = "{'fromApp':[{'name':'4','type':'app'},{'name':'*','type':'plugin'}]}";
        final JSONObject request = (JSONObject) JSONSerializer.toJSON(rqst);

        String result = (String) sut.transform(request, null);

        assertEquals("a4 p*", result);
    }

    /**
     * Tests transformation with unhandled types.
     */
    @Test(expected = RuntimeException.class)
    public void testTransformUnhandledType()
    {
        final String rqst = "{'fromApp':[{'name':'4','type':'person'}]}";
        final JSONObject request = (JSONObject) JSONSerializer.toJSON(rqst);

        String result = (String) sut.transform(request, null);
    }

    /**
     * Tests transformation with invalid id.
     */
    @Test(expected = RuntimeException.class)
    public void testTransformInvalidId()
    {
        final String rqst = "{'fromApp':[{'name':'4;','type':'person'}]}";
        final JSONObject request = (JSONObject) JSONSerializer.toJSON(rqst);

        String result = (String) sut.transform(request, null);
    }
}
