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
package org.eurekastreams.commons.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for UserActionRequest.
 */
public class UserActionRequestTest
{
    /** Test data. */
    private static final String ACTION_KEY = "actionKey";

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: params. */
    private final Serializable params = mockery.mock(Serializable.class, "params");

    /** Fixture: user. */
    private final Principal user = mockery.mock(Principal.class, "user");

    /**
     * Test.
     */
    @Test
    public void testToStringNoUser()
    {
        UserActionRequest sut = new UserActionRequest(ACTION_KEY, null, params);
        assertNotNull(sut.toString());
    }

    /**
     * Test.
     */
    @Test
    public void testToStringUser()
    {
        UserActionRequest sut = new UserActionRequest(ACTION_KEY,
                new DefaultPrincipal("accountid", "opensocialid", 1L), params);
        assertNotNull(sut.toString());
    }

    /**
     * Test the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters()
    {
        UserActionRequest sut = new UserActionRequest(ACTION_KEY, user, params);
        assertEquals(ACTION_KEY, sut.getActionKey());
        assertSame(user, sut.getUser());
        assertSame(params, sut.getParams());
    }
}
