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
import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Testing the followed by key gen.
 * 
 */
public class FollowedByPersistenceRequestTransformerTest
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
     * Mapper to get a Person id by account id.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private FollowedByPersistenceRequestTransformer sut = new FollowedByPersistenceRequestTransformer(
            getPersonIdByAccountIdMapper);

    /**
     * Passing in an empty request triggers a return on everyone list.
     */
    @Test
    public void transform()
    {
        final Long id = 7L;
        JSONObject request = new JSONObject();
        request.put("followedBy", "shawkings");

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("shawkings");
                will(returnValue(id));
            }
        });

        Long result = sut.transform(request, 0L);
        context.assertIsSatisfied();

        assertEquals(id, result);
    }
}
