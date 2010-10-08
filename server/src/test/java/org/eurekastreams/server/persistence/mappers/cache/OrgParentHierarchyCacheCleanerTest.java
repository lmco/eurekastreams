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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ClearRecursiveOrgChildIdCache.
 */
public class OrgParentHierarchyCacheCleanerTest extends MapperTest
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
     * System under test.
     */
    private OrgParentHierarchyCacheCleaner sut;

    /**
     * Mocked cache.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> getRecursiveParentOrgIdsMapper = context.mock(DomainMapper.class);

    /**
     * Setup.
     *
     * @throws Exception
     *             on error
     */
    @Before
    public void setup() throws Exception
    {
        // setup sut
        sut = new OrgParentHierarchyCacheCleaner();
        sut.setGetRecursiveParentOrgIdsMapper(getRecursiveParentOrgIdsMapper);
        sut.setCache(cache);
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        final Long orgId = 123L;
        final Long parent1Id = 323L;
        final Long parent2Id = 3234L;
        final Long parent3Id = 32345L;
        final Long parent4Id = 323456L;

        final List<Long> recursiveParentOrgIds = new ArrayList<Long>();
        recursiveParentOrgIds.add(parent1Id);
        recursiveParentOrgIds.add(parent2Id);
        recursiveParentOrgIds.add(parent3Id);
        recursiveParentOrgIds.add(parent4Id);

        context.checking(new Expectations()
        {
            {
                oneOf(getRecursiveParentOrgIdsMapper).execute(orgId);
                will(returnValue(recursiveParentOrgIds));

                oneOf(cache).delete(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + parent1Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + parent2Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + parent3Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + parent4Id);

                oneOf(cache).delete(CacheKeys.ORGANIZATION_BY_ID + parent1Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_BY_ID + parent2Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_BY_ID + parent3Id);
                oneOf(cache).delete(CacheKeys.ORGANIZATION_BY_ID + parent4Id);
            }
        });

        // test on org tree id org7a1
        sut.execute(orgId);

        context.assertIsSatisfied();
    }
}
