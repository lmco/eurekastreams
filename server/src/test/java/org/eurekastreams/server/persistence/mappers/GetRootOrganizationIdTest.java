/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetRootOrganizationId.
 */
public class GetRootOrganizationIdTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    private GetRootOrganizationIdAndShortName sut;

    /**
     * Setup.
     *
     * @throws Exception
     *             on error
     */
    @Before
    public void setup() throws Exception
    {
        super.setUpOnce();
        sut = new GetRootOrganizationIdAndShortName();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test getting the root org id from SQL.
     */
    @Test
    public void testGetRootOrganizationIdWithEmptyCache()
    {
        assertEquals(new Long(5L), sut.getRootOrganizationId());
    }

    /**
     * Test getting the root org id from SQL.
     */
    @Test
    public void testGetRootOrganizationShortNameWithEmptyCache()
    {
        assertEquals("tstorgname", sut.getRootOrganizationShortName());
    }

    /**
     * Test that getting the root org id stores it in cache.
     */
    @Test
    public void testGetRootOrganizationIdStoresInCache()
    {
        assertSame(sut.getRootOrganizationId(), sut.getRootOrganizationId());
    }

    /**
     * Test that getting the root org id stores it in cache.
     */
    @Test
    public void testGetRootOrganizationShortNameStoresInCache()
    {
        assertSame(sut.getRootOrganizationShortName(), sut.getRootOrganizationShortName());
    }
}
