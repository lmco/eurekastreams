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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceListRequest;
import org.hibernate.Session;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Insert bulk mapper test.
 *
 */
public class InsertBulkMapperTest
{
    /**
     * The number of activities to bulk insert.
     */
    private static final int NUMOFACTIVITY = 100;
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final EntityManager entityManager = context.mock(EntityManager.class);
        final PersistenceListRequest req = context.mock(PersistenceListRequest.class);
        final Session session = context.mock(Session.class);
        final EntityTransaction transaction = context.mock(EntityTransaction.class);

        final List<Activity> activities = new LinkedList<Activity>();

        for (int i = 0; i < NUMOFACTIVITY; i++)
        {
            activities.add(context.mock(Activity.class, "a" + String.valueOf(i)));
        }

        InsertBulkMapper sut = new InsertBulkMapper();
        sut.setEntityManager(entityManager);

        context.checking(new Expectations()
        {
            {
                oneOf(req).getDomainEnities();
                will(returnValue(activities));

                oneOf(entityManager).getDelegate();
                will(returnValue(session));

                exactly(NUMOFACTIVITY).of(session).save(with(any(Activity.class)));
                exactly(5).of(session).flush();
                exactly(5).of(session).clear();

                oneOf(entityManager).getTransaction();
                will(returnValue(transaction));

                oneOf(transaction).commit();
                oneOf(session).close();

            }
        });

        assertTrue(sut.execute(req));
        context.assertIsSatisfied();
    }
}
