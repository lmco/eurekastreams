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
package org.eurekastreams.web.client.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests BaseActivityLinkBuilder implemented methods.
 */
public class BaseActivityLinkBuilderTest
{
    /** Test data. */
    private static final long ACTIVITY_ID = 7531L;

    /** Test data. */
    private static final EntityType STREAM_TYPE = EntityType.GROUP;

    /** Test data. */
    private static final String STREAM_UNIQUE_ID = "mystream";

    /** Test data. */
    private static final String URL = "myurl";

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: request. */
    private CreateUrlRequest request = context.mock(CreateUrlRequest.class, "request");

    /** Fixture: session. */
    private Session session = context.mock(Session.class);

    /** Fixture: parameter map (as a mock, it can't be altered w/o us knowing). */
    private Map<String, String> parameterMap = context.mock(Map.class);

    /** SUT. */
    private BaseActivityLinkBuilder sut;

    /** Saved session to restore singleton. */
    private Session savedSession = Session.getInstance();

    /** Expected parameters. */
    private Map<String, String> expectedParameters;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        Session.setInstance(session);
        sut = new BaseActivityLinkBuilder()
        {
            @Override
            public CreateUrlRequest buildActivityPermalinkUrlRequest(final long inActivityId,
                    final EntityType inStreamType, final String inStreamUniqueId,
                    final Map<String, String> inExtraParameters)
            {
                assertEquals(ACTIVITY_ID, inActivityId);
                assertEquals(STREAM_TYPE, inStreamType);
                assertEquals(STREAM_UNIQUE_ID, inStreamUniqueId);
                assertSame(expectedParameters, inExtraParameters);
                return request;
            }
        };
    }

    /**
     * Setup after each test.
     */
    @After
    public void tearDown()
    {
        Session.setInstance(savedSession);
    }

    /**
     * Tests that the convenience methods are pass-thrus.
     */
    @Test
    public void testBuildActivityPermalink1()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(session).generateUrl(with(same(request)));
                will(returnValue(URL));
            }
        });

        expectedParameters = null;
        String result = sut.buildActivityPermalink(ACTIVITY_ID, STREAM_TYPE, STREAM_UNIQUE_ID);

        context.assertIsSatisfied();
        assertEquals(URL, result);
    }

    /**
     * Tests that the convenience methods are pass-thrus.
     */
    @Test
    public void testBuildActivityPermalink2()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(session).generateUrl(with(same(request)));
                will(returnValue(URL));
            }
        });

        expectedParameters = parameterMap;
        String result = sut.buildActivityPermalink(ACTIVITY_ID, STREAM_TYPE, STREAM_UNIQUE_ID, parameterMap);

        context.assertIsSatisfied();
        assertEquals(URL, result);
    }

    /**
     * Tests using stored extra parameters.
     */
    @Test
    public void testBuildParameters1()
    {
        sut.addExtraParameter("p1", "v1");
        sut.addExtraParameter("p2", "v2");

        Map<String, String> parms = sut.buildParameters(null);

        assertMap(parms, "p1", "v1", "p2", "v2");
    }

    /**
     * Tests using stored extra parameters.
     */
    @Test
    public void testBuildParameters2()
    {
        Map<String, String> parms = sut.buildParameters(null);
        assertTrue(parms.isEmpty());
    }

    /**
     * Tests using stored extra parameters.
     */
    @Test
    public void testBuildParameters3()
    {
        sut.addExtraParameter("p1", "v1");
        sut.addExtraParameter("p2", "v2");

        Map<String, String> extraParms = new HashMap<String, String>();
        extraParms.put("p2", "v2a");
        extraParms.put("p3", "v3");

        Map<String, String> parms = sut.buildParameters(extraParms);

        assertMap(parms, "p1", "v1", "p2", "v2a", "p3", "v3");
    }

    /**
     * Tests using stored extra parameters.
     */
    @Test
    public void testBuildParameters4()
    {
        Map<String, String> extraParms = new HashMap<String, String>();
        extraParms.put("p2", "v2a");
        extraParms.put("p3", "v3");

        Map<String, String> parms = sut.buildParameters(extraParms);

        assertMap(parms, "p2", "v2a", "p3", "v3");
    }

    /**
     * Tests that a result map contains what is expected.
     *
     * @param actual
     *            Map actually returned.
     * @param entries
     *            Keys and values expected to be in the map.
     */
    private void assertMap(final Map<String, String> actual, final String... entries)
    {
        assertTrue("Test code is broken:  assertMap must have key-value pairs.", entries.length % 2 == 0);
        assertNotNull(actual);
        assertEquals(entries.length / 2, actual.size());
        for (int i = 0; i < entries.length; i += 2)
        {
            assertEquals(entries[i + 1], actual.get(entries[i]));
        }
    }
}
