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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.server.UserActionRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for UpdatePersonAvatarCaches.
 */
public class UpdatePersonAvatarCachesTest
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
    private UpdatePersonAvatarCaches sut;

    /**
     * Person id.
     */
    private final Long personId = 83271L;

    /**
     * Mocked user.
     */
    private Principal user = context.mock(Principal.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new UpdatePersonAvatarCaches();

        context.checking(new Expectations()
        {
            {
                allowing(user).getId();
                will(returnValue(personId));
            }
        });
    }

    /**
     * Test updateCache().
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testUpdateCache() throws Exception
    {
        // perform sut
        List<UserActionRequest> results = sut.getUpdateCacheRequests(user, personId);

        assertEquals(1, results.size());

        UserActionRequest kickedOffRequest = results.get(0);

        // now take a look at the action that was kicked off
        assertEquals("personActivityAvatarUpdaterAsyncAction", kickedOffRequest.getActionKey());
        Long requestParams = (Long) kickedOffRequest.getParams();
        assertEquals(personId, requestParams);
        assertNull(kickedOffRequest.getUser());

        context.assertIsSatisfied();
    }
}
