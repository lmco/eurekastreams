/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.notification;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetInAppNotificationsByUserId.
 */
public class GetExistingInAppNotificationForAggregationTest extends MapperTest
{
    /** SUT. */
    private DomainMapper<InAppNotificationEntity, InAppNotificationEntity> sut;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetExistingInAppNotificationForAggregation();
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        // test
        InAppNotificationEntity searchCriteria = new InAppNotificationEntity();
        final Person recipient = context.mock(Person.class);
        context.checking(new Expectations()
        {
            {
                oneOf(recipient).getId();
                will(returnValue(42L));
            }
        });
        searchCriteria.setRecipient(recipient);
        searchCriteria.setUrl("#activity/6789");
        InAppNotificationEntity result = sut.execute(searchCriteria);

        // verify
        assertEquals(6L, result.getId());
    }

}
