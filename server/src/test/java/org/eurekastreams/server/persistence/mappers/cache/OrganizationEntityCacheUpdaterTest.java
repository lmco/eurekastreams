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

import org.eurekastreams.server.domain.Organization;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for OrganizationEntityCacheUpdater.
 * 
 */
public class OrganizationEntityCacheUpdaterTest
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
    private OrganizationEntityCacheUpdater sut = new OrganizationEntityCacheUpdater();

    /**
     * Org mock.
     */
    private Organization entity = context.mock(Organization.class, "org");

    /**
     * parent org mock.
     */
    private Organization entityParentOrg = context.mock(Organization.class, "parentOrg");

    /**
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Test set up.
     */
    @Before
    public void setUp()
    {
        sut.setCache(cache);
    }

    /**
     * Test.
     */
    @Test
    public void testPostPersist()
    {
        context.checking(new Expectations()
        {
            {
                allowing(entity).getId();
                will(returnValue(2L));

                allowing(entity).getParentOrganization();
                will(returnValue(entityParentOrg));

                allowing(entityParentOrg).getId();
                will(returnValue(1L));

                oneOf(cache).delete(CacheKeys.ORGANIZATION_TREE_DTO);
            }
        });
        sut.onPostPersist(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = RuntimeException.class)
    public void testPostPersistNoId()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(entity).getId();
                will(returnValue(0));
            }
        });
        sut.onPostPersist(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testPostUpdate()
    {
        context.checking(new Expectations()
        {
            {
                allowing(entity).getId();
                will(returnValue(2L));

                oneOf(cache).delete(CacheKeys.ORGANIZATION_BY_ID + 2L);

                oneOf(cache).delete(CacheKeys.ORGANIZATION_LEADERS_BY_ORG_ID + 2L);
            }
        });
        sut.onPostUpdate(entity);

        context.assertIsSatisfied();
    }

}
