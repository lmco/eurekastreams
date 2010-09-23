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
package org.eurekastreams.server.service.restlets.support;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests RestletQueryRequestParser.
 */
public class RestletQueryRequestParserTest
{

    /** SUT. */
    private RestletQueryRequestParser sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        final List<String> globalWords = new ArrayList<String>();
        globalWords.add("minId");
        globalWords.add("maxId");

        final List<String> multipleEntityWords = new ArrayList<String>();
        multipleEntityWords.add("recipient");

        final List<String> otherWords = new ArrayList<String>();
        otherWords.add("keywords");
        otherWords.add("followedBy");
        sut = new RestletQueryRequestParser(globalWords, multipleEntityWords, otherWords);
    }

    /**
     * Tests parsing a large request.
     *
     * @throws UnsupportedEncodingException
     *             bad request.
     */
    @Test
    public void parseLargeRequestTest() throws UnsupportedEncodingException
    {
        String expected =
                "{\"query\":{\"keywords\":\"test\",\"followedBy\":\"p1\","
                        + "\"recipient\":[{\"name\":\"p1\",\"type\":\"PERSON\"},{\"name\":\"p2\""
                        + ",\"type\":\"PERSON\"},{\"name\":\"g1\",\"type\":\"GROUP\"}]}"
                        + ",\"maxId\":\"20\",\"minId\":\"10\"}";
        JSONObject req =
                sut.parseRequest("/resources/stream/guid/query/recipient/" + "PERSON:p1,PERSON:p2,GROUP:g1/"
                        + "followedBy/p1/keywords/test/minId/10/maxId/20", 5);

        Assert.assertEquals(expected, req.toString());
    }
}
