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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for FindSystemSettingsDbDomainMapperImpl class.
 */
public class FindSystemSettingsDbDomainMapperImplTest extends MapperTest
{
    /** System under test. */
    private DomainMapper<MapperRequest, SystemSettings> sut;

    /** Query optimizer. */
    @Autowired
    QueryOptimizer queryOptimizer;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new FindSystemSettingsDbDomainMapperImpl("HeaderTemplate", "FooterTemplate", "BannerTemplate");
        ((ReadMapper) sut).setQueryOptimizer(queryOptimizer);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
    }

    /**
     * Test execute with test data.
     */
    @Test
    public void testExecute()
    {
        final long settingsId = 1001L;

        SystemSettings settings = sut.execute(null);

        assertEquals(settingsId, settings.getId());
        assertEquals("some site label", settings.getSiteLabel());
        assertEquals("some terms of service", settings.getTermsOfService());
        assertEquals(1, settings.getTosPromptInterval());
        assertEquals("some content warning", settings.getContentWarningText());
        assertEquals(1, settings.getContentExpiration());
        assertTrue(settings.getSendWelcomeEmails());
        assertEquals("HeaderTemplate", settings.getHeaderTemplate());
        assertEquals("FooterTemplate", settings.getFooterTemplate());
        assertEquals("BannerTemplate", settings.getBannerTemplate());
    }
}
