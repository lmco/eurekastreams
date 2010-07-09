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
package org.eurekastreams.server.persistence.mappers.opensocial;

import static org.junit.Assert.assertNotNull;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.GadgetUserPrefDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GadgetUserPrefRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple test for the read mapper.
 *
 */
public class GetGadgetUserPrefMapperTest extends MapperTest
{
    /**
     * Instance of the mapper to test.
     */
    @Autowired
    private GetGadgetUserPrefMapper mapper;

    /**
     * Test to retrieve user prefs for a gadget that has user preferences.
     */
    @Test
    public void testGetGadgetUserPrefWithResults()
    {
        final long gadgetId = 7841L;
        final String userPrefParam = "{userPref1:value1,userPref2:value2}";
        GadgetUserPrefRequest request = new GadgetUserPrefRequest(gadgetId, userPrefParam);
        GadgetUserPrefDTO userPref = mapper.execute(request);

        assertNotNull(userPref);
    }

    /**
     * Test to attempt to retrieve user prefs for a gadget that has no prefs.
     */
    @Test(expected = NoResultException.class)
    public void testGetGadgetUserPrefWithNoResults()
    {
        final long gadgetId = 7881L;
        final String userPrefParam = "{userPref1:value1,userPref2:value2}";
        GadgetUserPrefRequest request = new GadgetUserPrefRequest(gadgetId, userPrefParam);
        GadgetUserPrefDTO userPref = mapper.execute(request);

        assertNotNull(userPref);
    }
}
