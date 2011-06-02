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
package org.eurekastreams.server.action.execution.notification.inapp;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.notification.InAppNotificationsByUserMapperRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ModifyInAppNotificationsExecution.
 */
@SuppressWarnings("rawtypes")
public class ModifyInAppNotificationsExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to modify (update or delete) notifications. */
    private final DomainMapper modifyNotifsMapper = context.mock(DomainMapper.class, "modifyNotifsMapper");

    /** Mapper to sync database and cache unread alert count. */
    private final DomainMapper syncMapper = context.mock(DomainMapper.class, "syncMapper");

    /** SUT. */
    private ExecutionStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ModifyInAppNotificationsExecution(modifyNotifsMapper, syncMapper);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final Collection<Long> items = Arrays.asList(1L, 2L, 3L);
        final long userId = 77L;
        final UnreadInAppNotificationCountDTO newCount = new UnreadInAppNotificationCountDTO(4, 6);

        final InAppNotificationsByUserMapperRequest expectedRequest = new InAppNotificationsByUserMapperRequest(items,
                userId);

        context.checking(new Expectations()
        {
            {
                oneOf(modifyNotifsMapper).execute(with(equalInternally(expectedRequest)));
                oneOf(syncMapper).execute(userId);
                will(returnValue(newCount));
            }
        });

        PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext((Serializable) items, "whomever",
                userId);

        assertTrue(IsEqualInternally.areEqualInternally(newCount, sut.execute(ctx)));

        context.assertIsSatisfied();
    }
}
