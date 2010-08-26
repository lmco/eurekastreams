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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.Arrays;

import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests finding all activity associated with an organization from the cache.
 */
public class OrgActivityIdsMapperTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static OrgActivityIdsMapper sut;

    /**
     * Cache fed into the loader.
     */
    private static Cache cache = CONTEXT.mock(Cache.class);

    /**
     * Org DAO.
     */
    private static GetOrganizationsByIds orgDAO = CONTEXT.mock(GetOrganizationsByIds.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new OrgActivityIdsMapper();
        sut.setCache(cache);
        sut.setOrganizationDAO(orgDAO);
    }

    /**
     * Tests mapping.
     */
    @Test
    public void testMapping()
    {
        final OrganizationModelView org = new OrganizationModelView();
        org.setCompositeStreamId(2L);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(orgDAO).execute(Arrays.asList(1L));
                will(returnValue(Arrays.asList(org)));

                oneOf(cache).getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 2L);
            }
        });

        sut.execute(1L);

        CONTEXT.assertIsSatisfied();
    }
}
