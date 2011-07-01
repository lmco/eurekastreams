/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for UpdatePersonDisplayNameCaches.
 */
public class UpdatePersonDisplayNameCachesTest
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
     * Mapper to get people by ids.
     */
    private final DomainMapper<List<Long>, List<PersonModelView>> getPeopleByIdsMapper = // \n
    context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private final UpdatePersonDisplayNameCaches sut = new UpdatePersonDisplayNameCaches(getPeopleByIdsMapper);

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        Principal principal = context.mock(Principal.class);
        final Long personId = 293842L;
        final String personAccountId = "sldkfjsd";
        final PersonModelView person = context.mock(PersonModelView.class);

        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        people.add(person);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByIdsMapper).execute(with(any(List.class)));
                will(returnValue(people));

                oneOf(person).getAccountId();
                will(returnValue(personAccountId));
            }
        });

        List<UserActionRequest> userActionRequests = sut.getUpdateCacheRequests(principal, personId);
        assertEquals(2, userActionRequests.size());

        // action 1
        UserActionRequest actionRequest = userActionRequests.get(0);
        assertEquals("personDisplayNameUpdaterAsyncAction", actionRequest.getActionKey());
        assertNull(actionRequest.getUser());
        assertEquals(personId, actionRequest.getParams());

        // action 3
        actionRequest = userActionRequests.get(1);
        assertEquals("activityRecipientPersonNameUpdaterAsyncAction", actionRequest.getActionKey());
        assertNull(actionRequest.getUser());
        assertEquals(personAccountId, actionRequest.getParams());

        context.assertIsSatisfied();
    }
}
