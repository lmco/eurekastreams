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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Testing the followed by key gen.
 *
 */
public class FollowedByKeyGeneratorTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * System under test.
     */
    private FollowedByKeyGenerator sut = new FollowedByKeyGenerator(personMapper);

    /**
     * Passing in an empty request triggers a return on everyone list.
     */
    @Test
    public void getKeys()
    {
        JSONObject request = new JSONObject();
        request.put("followedBy", "shawkings");

        final PersonModelView person = context.mock(PersonModelView.class);
        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchUniqueResult("shawkings");
                will(returnValue(person));

                oneOf(person).getId();
                will(returnValue(7L));
            }
        });

        List<String> keys = sut.getKeys(request);
        context.assertIsSatisfied();

        assertEquals(keys.get(0), CacheKeys.ACTIVITIES_BY_FOLLOWING + "7");
    }
}
