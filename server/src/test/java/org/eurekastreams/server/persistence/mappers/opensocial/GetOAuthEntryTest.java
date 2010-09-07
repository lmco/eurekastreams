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
package org.eurekastreams.server.persistence.mappers.opensocial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetOAuthEntry} mapper.
 * 
 */
public class GetOAuthEntryTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetOAuthEntry sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetOAuthEntry();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test finding an entry.
     */
    @Test
    public void testFindEntry()
    {
        OAuthDomainEntry entry = sut.execute("token1");

        assertTrue("No Entry found for token of 'token1'", entry != null);

        // verify loaded attributes of consumer
        assertEquals("Incorrect callback URL returned", "http://localhost:8080/gadgets/oauthcallback", entry
                .getCallbackUrl());
        assertEquals("Incorrect app id returned", "application1", entry.getAppId());
        assertEquals("Incorrect container returned", "container1", entry.getContainer());
    }

    /**
     * Test not finding an entry.
     */
    @Test
    public void testFindNullEntry()
    {
        OAuthDomainEntry entry = sut.execute("tokenX");
        assertTrue("Entry found for token of 'tokenX'", entry == null);
    }
}
