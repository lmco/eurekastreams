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
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCacheLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for CompositeStreamLoaderCustom class.
 *
 */
public class CompositeStreamLoaderCustomTest extends CachedMapperTest
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
     * Org mapper.
     */
    @Autowired
    GetOrganizationsByIds orgMapper;


    /**
     * Cache.
     */
    @Autowired
    private Cache memcachedCache;

    /**
     * System under test.
     */
    @Autowired
    private CompositeStreamLoaderCustom compositeStreamLoaderCustom;

    /**
     * User id used in tests.
     */
    private final long userId = 42L;

    /**
     * CompositeStream id used in tests.
     */
    private final long compositeStreamId = 999L;

    /**
     * CompositeStream mock.
     */
    private final StreamView compositeStream = context.mock(StreamView.class, "tehCompositeString");

    /**
     * Cache loader for organizations.
     */
    @Autowired
    private OrganizationHierarchyCacheLoader organizationHierarchyCacheLoader;

    /**
     * Setup method - initialize the caches.
     */
    @Before
    public void setup()
    {
        organizationHierarchyCacheLoader.initialize();
    }

    /**
     * test.
     */
    @Test
    public void testGetActivityIdsPersonWithIds()
    {
        final long smithersStreamScopeId = 87433L;
        final HashSet<StreamScope> streamScopes = new HashSet<StreamScope>();
        streamScopes.add(new StreamScope(ScopeType.PERSON, "smithers", smithersStreamScopeId));
        streamScopes.add(new StreamScope(ScopeType.PERSON, "mrburns", 4L));

        context.checking(new Expectations()
        {
            {
                allowing(compositeStream).getId();
                will(returnValue(compositeStreamId));

                oneOf(compositeStream).getIncludedScopes();
                will(returnValue(streamScopes));
            }
        });

        // assert that cache is empty for compositeStream of interest.
        assertNull(memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId));

        // This should get ids for compositeStream and load them into cache.
        List<Long> results = compositeStreamLoaderCustom.getActivityIds(compositeStream, userId);

        // verify correct number of activities from initial call.
        assertEquals(2, results.size());

        // verify that it's now in cache with correct number of activities.
        assertEquals(2, (memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId)).size());

        context.assertIsSatisfied();
    }

    /**
     * test.
     */
    @Test
    public void testGetActivityIdsPersonWithoutIds()
    {
        final long mrburnsStreamScopeId = 4L;
        final HashSet<StreamScope> streamScopes = new HashSet<StreamScope>();
        streamScopes.add(new StreamScope(ScopeType.PERSON, "mrburns", mrburnsStreamScopeId));

        context.checking(new Expectations()
        {
            {
                allowing(compositeStream).getId();
                will(returnValue(compositeStreamId));

                oneOf(compositeStream).getIncludedScopes();
                will(returnValue(streamScopes));
            }
        });

        // assert that cache is empty for compositeStream of interest.
        assertNull(memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId));

        // This should get ids for compositeStream and load them into cache.
        List<Long> results = compositeStreamLoaderCustom.getActivityIds(compositeStream, userId);

        // verify correct number of activities from initial call.
        assertEquals(0, results.size());

        // verify that it's now in cache with correct number of activities.
        assertEquals(0, (memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId)).size());

        context.assertIsSatisfied();
    }

    /**
     * test.
     */
    @Test
    public void testGetActivityIdsOrgWithIds()
    {
        final long streamScopeIdForTstOrgName = 837433L;
        final HashSet<StreamScope> streamScopes = new HashSet<StreamScope>();
        streamScopes.add(new StreamScope(ScopeType.ORGANIZATION, "TstOrgName", streamScopeIdForTstOrgName));

        context.checking(new Expectations()
        {
            {
                allowing(compositeStream).getId();
                will(returnValue(compositeStreamId));

                oneOf(compositeStream).getIncludedScopes();
                will(returnValue(streamScopes));
            }
        });

        // assert that cache is empty for compositeStream of interest.
        assertNull(memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId));

        // This should get ids for compositeStream and load them into cache.
        List<Long> results = compositeStreamLoaderCustom.getActivityIds(compositeStream, userId);

        // verify correct number of activities from initial call.
        assertEquals(5, results.size());

        // verify that it's now in cache with correct number of activities.
        assertEquals(5, (memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId)).size());

        context.assertIsSatisfied();
    }

    /**
     * test.
     */
    @Test
    public void testGetActivityIdsOrgWithOneId()
    {
        final long streamScopeIdForChild1OrgName = 9374533L;
        final HashSet<StreamScope> streamScopes = new HashSet<StreamScope>();
        streamScopes.add(new StreamScope(ScopeType.ORGANIZATION, "Child1OrgName", streamScopeIdForChild1OrgName));

        context.checking(new Expectations()
        {
            {
                allowing(compositeStream).getId();
                will(returnValue(compositeStreamId));

                oneOf(compositeStream).getIncludedScopes();
                will(returnValue(streamScopes));
            }
        });

        // assert that cache is empty for compositeStream of interest.
        assertNull(memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId));

        // This should get ids for compositeStream and load them into cache.
        List<Long> results = compositeStreamLoaderCustom.getActivityIds(compositeStream, userId);

        // verify correct number of activities from initial call.
        assertEquals(1, results.size());

        // verify that it's now in cache with correct number of activities.
        assertEquals(1, (memcachedCache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStreamId)).size());

        context.assertIsSatisfied();
    }

}
