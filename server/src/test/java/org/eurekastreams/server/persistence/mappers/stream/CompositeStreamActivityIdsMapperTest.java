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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.stream.StreamView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for getting activities for a composite stream.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class CompositeStreamActivityIdsMapperTest
{

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
     * CompositeStream DAO mock.
     */
    private BulkCompositeStreamsMapper getCompositeStreamByIdDAO = context.mock(BulkCompositeStreamsMapper.class);

    /**
     * CompositeStreamLoader mock.
     */
    private CompositeStreamLoader csLoader = context.mock(CompositeStreamLoader.class);

    /**
     * CompositeStream mock.
     */
    private StreamView compositeStream = context.mock(StreamView.class);

    /**
     * CompositeStream id used in tests.
     */
    private final long compositeStreamId = 99L;

    /**
     * User id used in tests.
     */
    private final long userId = 999L;

    /**
     * List of compositeStreamIds used in tests.
     */
    private final ArrayList<Long> compositeStreamIds = new ArrayList<Long>();

    /**
     * List of streamViews used by tests.
     */
    private final ArrayList<StreamView> compositeStreams = new ArrayList<StreamView>();

    /**
     * BulkCompositeStreamsMapper from real autowired version of CompositeStreamActivityIdsMapper.
     */
    private BulkCompositeStreamsMapper realBulkCompositeStreamMapper;

    /**
     * real CompositeStreamLoaders from autowired version of System under test.
     */
    private Map<StreamView.Type, CompositeStreamLoader> realLoaders;

    /**
     * System under test.
     */
    @Autowired
    private CompositeStreamActivityIdsMapper compositeStreamActivityIdsMapper;

    /**
     * Test setup. This stores the "real" versions of compositeStreamActivityIdsMapper properties that tests will
     * replace with mock values. This is so they can be replaced in @After so it doesn't affect other unit test classes.
     */
    @Before
    public void setUp()
    {
        compositeStreamIds.clear();
        compositeStreams.clear();
        realBulkCompositeStreamMapper = compositeStreamActivityIdsMapper.getCompositeStreamMapper();
        realLoaders = compositeStreamActivityIdsMapper.getCompositeStreamLoaders();
    }

    /**
     * Restores the "real" version of compositeStreamActivityIdsMapper properties.
     */
    @After
    public void cleanUp()
    {
        compositeStreamActivityIdsMapper.setCompositeStreamMapper(realBulkCompositeStreamMapper);
        compositeStreamActivityIdsMapper.setCompositeStreamLoaders(realLoaders);
    }

    /**
     * Mock CompositeStreamLoaders are used in other tests, but this test will Verify that the map of loaders created by
     * spring is set as exepected with correct key value types and accessible as expected by the class.
     */
    @Test
    public void testVerifySpringCreatedMapWorksAsExpected()
    {
        // Mock CompositeStreamLoaders are used in other tests, but this test will
        // Verify that the map of loaders created by spring is set as exepected with correct key
        // value types and accessible as expected by the class.
        assertNotNull("Loader map was not set", compositeStreamActivityIdsMapper.getCompositeStreamLoaders());
        assertEquals(5, compositeStreamActivityIdsMapper.getCompositeStreamLoaders().size());
        assertTrue(compositeStreamActivityIdsMapper.getCompositeStreamLoaders().containsKey(StreamView.Type.EVERYONE));
        assertNotNull(compositeStreamActivityIdsMapper.getCompositeStreamLoaders().get(StreamView.Type.EVERYONE));
    }

    /**
     * Test getters/setters.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetterSetters()
    {
        // create empty map of loaders.
        Map testMap = new HashMap();

        // set loaders and compositeStream mapper.
        compositeStreamActivityIdsMapper.setCompositeStreamLoaders(testMap);
        compositeStreamActivityIdsMapper.setCompositeStreamMapper(getCompositeStreamByIdDAO);

        assertEquals(testMap, compositeStreamActivityIdsMapper.getCompositeStreamLoaders());
        assertEquals(getCompositeStreamByIdDAO, compositeStreamActivityIdsMapper.getCompositeStreamMapper());
    }

    /**
     * Test success path of execute method.
     */
    @Test
    public void testExecuteSuccess()
    {
        // create map of loaders.
        Map<StreamView.Type, CompositeStreamLoader> loaders = new HashMap<StreamView.Type, CompositeStreamLoader>();
        loaders.put(StreamView.Type.STARRED, csLoader);

        // set loaders and compositeStream mapper.
        compositeStreamActivityIdsMapper.setCompositeStreamLoaders(loaders);
        compositeStreamActivityIdsMapper.setCompositeStreamMapper(getCompositeStreamByIdDAO);

        // add ids lists.
        compositeStreamIds.add(compositeStreamId);
        compositeStreams.add(compositeStream);

        context.checking(new Expectations()
        {
            {
                oneOf(getCompositeStreamByIdDAO).execute(compositeStreamIds);
                will(returnValue(compositeStreams));

                allowing(compositeStream).getType();
                will(returnValue(StreamView.Type.STARRED));

                oneOf(csLoader).getActivityIds(compositeStream, userId);

            }
        });

        compositeStreamActivityIdsMapper.execute(compositeStreamId, userId);
        context.assertIsSatisfied();
    }

    /**
     * Test error path of no loader throwing exception.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteNoLoader()
    {
        // create empty map of loaders.
        Map<StreamView.Type, CompositeStreamLoader> loaders = new HashMap<StreamView.Type, CompositeStreamLoader>();

        // set loaders and compositeStream mapper.
        compositeStreamActivityIdsMapper.setCompositeStreamLoaders(loaders);
        compositeStreamActivityIdsMapper.setCompositeStreamMapper(getCompositeStreamByIdDAO);

        // add ids lists.
        compositeStreamIds.add(compositeStreamId);
        compositeStreams.add(compositeStream);

        context.checking(new Expectations()
        {
            {
                oneOf(getCompositeStreamByIdDAO).execute(compositeStreamIds);
                will(returnValue(compositeStreams));

                allowing(compositeStream).getType();
                will(returnValue(StreamView.Type.STARRED));

                oneOf(compositeStream).getId();
                will(returnValue(compositeStreamId));

            }
        });

        compositeStreamActivityIdsMapper.execute(compositeStreamId, userId);
        context.assertIsSatisfied();
    }

    /**
     * Test error path of no CompositeStream throwing exception.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteNoCompositeStream()
    {
        compositeStreamActivityIdsMapper.setCompositeStreamMapper(getCompositeStreamByIdDAO);
        compositeStreamIds.add(compositeStreamId);

        context.checking(new Expectations()
        {
            {
                oneOf(getCompositeStreamByIdDAO).execute(compositeStreamIds);
                will(returnValue(compositeStreams));

            }
        });

        compositeStreamActivityIdsMapper.execute(compositeStreamId, userId);
        context.assertIsSatisfied();
    }
}
