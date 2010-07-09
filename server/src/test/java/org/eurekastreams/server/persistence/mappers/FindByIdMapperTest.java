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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for FindByIdMapper class.
 *
 */
public class FindByIdMapperTest
{
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
        final QueryOptimizer queryOptimizer = context.mock(QueryOptimizer.class);
        final FindByIdRequest req = context.mock(FindByIdRequest.class);
        final Query query = context.mock(Query.class);
        final List<Activity> activities = context.mock(List.class);
        final Activity activity = context.mock(Activity.class);

        FindByIdMapper<Activity> sut = new FindByIdMapper();
        sut.setEntityManager(entityManager);
        sut.setQueryOptimizer(queryOptimizer);

        context.checking(new Expectations()
        {
            {
                oneOf(req).getEntityName();
                will(returnValue("Activity"));

                oneOf(req).getEntityId();
                will(returnValue(1L));

                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));

                allowing(query).setParameter(with(any(String.class)), with(any(Object.class)));
                will(returnValue(query));

                oneOf(query).getResultList();
                will(returnValue(activities));

                oneOf(activities).isEmpty();
                will(returnValue(false));

                oneOf(activities).size();
                will(returnValue(1));

                oneOf(activities).get(0);
                will(returnValue(activity));
            }
        });

        assertTrue(sut.execute(req) == activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNoneFound()
    {
        final EntityManager entityManager = context.mock(EntityManager.class);
        final QueryOptimizer queryOptimizer = context.mock(QueryOptimizer.class);
        final FindByIdRequest req = context.mock(FindByIdRequest.class);
        final Query query = context.mock(Query.class);
        final List<Activity> activities = context.mock(List.class);

        FindByIdMapper<Activity> sut = new FindByIdMapper();
        sut.setEntityManager(entityManager);
        sut.setQueryOptimizer(queryOptimizer);

        context.checking(new Expectations()
        {
            {
                oneOf(req).getEntityName();
                will(returnValue("Activity"));

                oneOf(req).getEntityId();
                will(returnValue(1L));

                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));

                allowing(query).setParameter(with(any(String.class)), with(any(Object.class)));
                will(returnValue(query));

                oneOf(query).getResultList();
                will(returnValue(activities));

                oneOf(activities).isEmpty();
                will(returnValue(true));
            }
        });

        assertTrue(sut.execute(req) == null);
        context.assertIsSatisfied();
    }

}
