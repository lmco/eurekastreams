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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GetGadgetsByGadgetDefAndConsumerKeyRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetGadgetsByGadgetDefAndConsumerKey} mapper.
 * 
 */
public class GetGadgetsByGadgetDefAndConsumerKeyTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetGadgetsByGadgetDefAndConsumerKey sut;

    /**
     * Test oauth consumer key.
     */
    private static final String OAUTH_CONSUMERKEY_1 = "key0";

    /**
     * Test oauth user id.
     */
    private static final Long USER_1 = 99L;

    /**
     * Test user that does not have an app installed corresponding to OAUTH_CONSUMERKEY_1.
     */
    private static final Long USER_2 = 4507L;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetGadgetsByGadgetDefAndConsumerKey();
        sut.setEntityManager(getEntityManager());
    }
    /**
     * Simple to test to verify a scenario where the consumer key passed in matches with an application installed by the
     * user passed in.
     */
    @Test
    public void testSuccessfulExecution()
    {
        GetGadgetsByGadgetDefAndConsumerKeyRequest request = new GetGadgetsByGadgetDefAndConsumerKeyRequest(
                OAUTH_CONSUMERKEY_1, USER_1);
        Long returnValue = sut.execute(request);
        assertEquals(new Long(3), returnValue);
    }

    /**
     * Test that this mapper returns zero gadget count when no user is supplied.
     */
    @Test
    public void testFailureWithNoUserId()
    {
        GetGadgetsByGadgetDefAndConsumerKeyRequest request = new GetGadgetsByGadgetDefAndConsumerKeyRequest(
                OAUTH_CONSUMERKEY_1, null);
        Long returnValue = sut.execute(request);
        assertEquals(new Long(0), returnValue);
    }

    /**
     * This test verifies that the mapper returns zero gadget count when the supplied user does not have the app
     * corresponing to the consumer key installed on their start page.
     */
    @Test
    public void testFailureWithValidUserIdButNoAppInstalled()
    {
        GetGadgetsByGadgetDefAndConsumerKeyRequest request = new GetGadgetsByGadgetDefAndConsumerKeyRequest(
                OAUTH_CONSUMERKEY_1, USER_2);
        Long returnValue = sut.execute(request);
        assertEquals(new Long(0), returnValue);
    }

    /**
     * This test verifies that the mapper returns zero gadget count when the supplied consumer key is invalid.
     */
    @Test
    public void testFailureWithInvalidConsumerKey()
    {
        GetGadgetsByGadgetDefAndConsumerKeyRequest request = new GetGadgetsByGadgetDefAndConsumerKeyRequest(
                "badkey", USER_2);
        Long returnValue = sut.execute(request);
        assertEquals(new Long(0), returnValue);
    }
}
