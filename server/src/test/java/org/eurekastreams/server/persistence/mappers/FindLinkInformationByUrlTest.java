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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.persistence.mappers.requests.UniqueStringRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Find link information by URL test.
 */
public class FindLinkInformationByUrlTest
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
     * System under test.
     */
    FindLinkInformationByUrl sut = new FindLinkInformationByUrl();

    /**
     * Test find a link that exists.
     */
    @Test
    public final void findLinkTest()
    {
        final EntityManager entityManager = context.mock(EntityManager.class);
        final QueryOptimizer queryOptimizer = context.mock(QueryOptimizer.class);
        final Query query = context.mock(Query.class);

        final UniqueStringRequest req = new UniqueStringRequest("http://www.someurl.com");
        sut.setEntityManager(entityManager);
        sut.setQueryOptimizer(queryOptimizer);

        final LinkInformation link = new LinkInformation();
        link.setCreated(new Date());

        final List<LinkInformation> results = new ArrayList<LinkInformation>();
        results.add(link);

        final long timeOut = 7200000L;
        sut.setExpirationInMilliseconds(timeOut);

        context.checking(new Expectations()
        {
            {
                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));

                oneOf(query).setParameter("url", "http://www.someurl.com");
                will(returnValue(query));

                oneOf(query).getResultList();
                will(returnValue(results));
            }
        });

        sut.execute(req);

        context.assertIsSatisfied();
    }

    /**
     * Test find a link that exists butis expired.
     */
    @Test
    public final void findExpiredLinkTest()
    {
        final EntityManager entityManager = context.mock(EntityManager.class);
        final QueryOptimizer queryOptimizer = context.mock(QueryOptimizer.class);
        final Query query = context.mock(Query.class);

        final UniqueStringRequest req = new UniqueStringRequest("http://www.someurl.com");
        sut.setEntityManager(entityManager);
        sut.setQueryOptimizer(queryOptimizer);

        final LinkInformation link = new LinkInformation();
        link.setCreated(new Date(0));

        sut.setExpirationInMilliseconds(0L);

        final List<LinkInformation> results = new ArrayList<LinkInformation>();
        results.add(link);

        context.checking(new Expectations()
        {
            {
                oneOf(entityManager).createQuery(with(any(String.class)));
                will(returnValue(query));

                oneOf(query).setParameter("url", "http://www.someurl.com");
                will(returnValue(query));

                oneOf(query).getResultList();
                will(returnValue(results));

                oneOf(entityManager).remove(link);
            }
        });

        sut.execute(req);

        context.assertIsSatisfied();
    }
}
