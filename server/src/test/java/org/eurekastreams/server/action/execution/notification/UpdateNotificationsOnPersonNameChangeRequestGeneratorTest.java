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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.UpdateNotificationsOnNameChangeRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for UpdateNotificationsOnPersonNameChangeRequestGenerator.
 * 
 */
public class UpdateNotificationsOnPersonNameChangeRequestGeneratorTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper entityFinder = context.mock(FindByIdMapper.class);

    /**
     * {@link Person}.
     */
    private Person person = context.mock(Person.class);

    /**
     * Id used for test.
     */
    private Long id = 1L;

    /**
     * key used for test.
     */
    private String key = "key";

    /**
     * Name used for test.
     */
    private String name = "name";

    /**
     * EntityType used for test.
     */
    private EntityType type = EntityType.PERSON;

    /**
     * System under test.
     */
    private UpdateNotificationsOnPersonNameChangeRequestGenerator sut = // \n
    new UpdateNotificationsOnPersonNameChangeRequestGenerator(entityFinder);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(entityFinder).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));

                allowing(person).getDisplayName();
                will(returnValue(name));

                allowing(person).getAccountId();
                will(returnValue(key));

            }
        });

        UpdateNotificationsOnNameChangeRequest request = sut.getUpdateNotificationsOnNameChangeRequest(id);

        assertEquals(EntityType.PERSON.toString(), request.getType().toString());
        assertEquals(name, request.getName());
        assertEquals(key, request.getUniqueKey());

        context.assertIsSatisfied();
    }
}
