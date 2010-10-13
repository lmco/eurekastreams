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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateCachedBannerMapperRequest;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * This class contains the test suite for the {@link UpdateCachedOrgBannerIdMapper}.
 *
 */
public class UpdateCachedOrgBannerIdMapperTest
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
     * Mocked instance of cache.
     */
    private Cache cacheMock = context.mock(Cache.class);

    /**
     * System under test.
     */
    private UpdateCachedOrgBannerIdMapper sut;

    /**
     * Cached mapper for retrieving OrganizationModelViews.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> getOrgMapperMock = context.mock(DomainMapper.class);

    /**
     * Test org id.
     */
    private static final Long TEST_ORG_ID = 5L;

    /**
     * Banner id for the test org.
     */
    private static final String TEST_BANNER_ID = "Foo";

    /**
     * Test updating the cache with a new banner id.
     */
    @Test
    public void testUpdateBannerId()
    {
        sut = new UpdateCachedOrgBannerIdMapper(getOrgMapperMock);
        sut.setCache(cacheMock);

        final OrganizationModelView currentOrg = new OrganizationModelView();
        currentOrg.setEntityId(TEST_ORG_ID);
        currentOrg.setBannerId(TEST_BANNER_ID);

        final List<OrganizationModelView> orgs = new ArrayList<OrganizationModelView>();
        orgs.add(currentOrg);

        context.checking(new Expectations()
        {
            {
                oneOf(getOrgMapperMock).execute(Collections.singletonList(TEST_ORG_ID));
                will(returnValue(orgs));

                oneOf(cacheMock).set(CacheKeys.ORGANIZATION_BY_ID + TEST_ORG_ID, currentOrg);
            }
        });

        UpdateCachedBannerMapperRequest currentRequest = new UpdateCachedBannerMapperRequest("testbannerid",
                TEST_ORG_ID);
        sut.execute(currentRequest);

        assertEquals("testbannerid", currentOrg.getBannerId());

        context.assertIsSatisfied();
    }

    /**
     * Test nulling out the banner id in cache.
     */
    @Test
    public void testNullOutBannerId()
    {
        sut = new UpdateCachedOrgBannerIdMapper(getOrgMapperMock);
        sut.setCache(cacheMock);

        final OrganizationModelView currentOrg = new OrganizationModelView();
        currentOrg.setEntityId(TEST_ORG_ID);
        currentOrg.setBannerId(TEST_BANNER_ID);

        final List<OrganizationModelView> orgs = new ArrayList<OrganizationModelView>();
        orgs.add(currentOrg);

        context.checking(new Expectations()
        {
            {
                oneOf(getOrgMapperMock).execute(Collections.singletonList(TEST_ORG_ID));
                will(returnValue(orgs));

                oneOf(cacheMock).set(CacheKeys.ORGANIZATION_BY_ID + TEST_ORG_ID, currentOrg);
            }
        });

        UpdateCachedBannerMapperRequest currentRequest = new UpdateCachedBannerMapperRequest(null, TEST_ORG_ID);
        sut.execute(currentRequest);

        assertEquals(null, currentOrg.getBannerId());

        context.assertIsSatisfied();
    }
}
