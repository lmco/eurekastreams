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
package org.eurekastreams.server.action.authorization;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link IsFormSubmitterUserAuthorization} class.
 *
 */
public class IsFormSubmitterUserAuthorizationTest
{
    /**
     * System under test.
     */
    private IsFormSubmitterUserAuthorization sut;

    /**
     * Fields to test with.
     */
    private HashMap<String, Serializable> fields;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new IsFormSubmitterUserAuthorization("accountid");
    }

    /**
     * Test successful authorization.
     */
    @Test
    public void testAuthorize()
    {
        fields = new HashMap<String, Serializable>();
        fields.put("accountid", "testaccount");
        DefaultPrincipal currentPrincipal = new DefaultPrincipal("testaccount", null, null);
        ServiceActionContext currentContext = new ServiceActionContext(fields, currentPrincipal);
        sut.authorize(currentContext);
    }

    /**
     * Test failure authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailure()
    {
        fields = new HashMap<String, Serializable>();
        fields.put("accountid", "testaccount");
        DefaultPrincipal currentPrincipal = new DefaultPrincipal("coolaccount", null, null);
        ServiceActionContext currentContext = new ServiceActionContext(fields, currentPrincipal);
        sut.authorize(currentContext);
    }

    /**
     * Test failure authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFailureMissingKey()
    {
        fields = new HashMap<String, Serializable>();
        fields.put("badkey", "testaccount");
        DefaultPrincipal currentPrincipal = new DefaultPrincipal("coolaccount", null, null);
        ServiceActionContext currentContext = new ServiceActionContext(fields, currentPrincipal);
        sut.authorize(currentContext);
    }
}
