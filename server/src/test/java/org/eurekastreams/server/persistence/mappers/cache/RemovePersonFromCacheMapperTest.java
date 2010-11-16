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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for RemovePersonFromCacheMapper.
 */
public class RemovePersonFromCacheMapperTest
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
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Cache cache = context.mock(Cache.class);
        final Person person = context.mock(Person.class);
        final String accountId = "lslkkldl";
        final Long personId = 2983L;
        final Long scopeId = 8848L;
        final StreamScope scope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {
                allowing(person).getAccountId();
                will(returnValue(accountId));

                oneOf(person).getStreamScope();
                will(returnValue(scope));

                allowing(scope).getId();
                will(returnValue(scopeId));

                allowing(person).getId();
                will(returnValue(personId));

                oneOf(cache).delete(CacheKeys.PERSON_BY_ID + personId);
                oneOf(cache).delete(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + personId);
                oneOf(cache).delete(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + personId);
                oneOf(cache).delete(CacheKeys.STREAM_BY_ID + scopeId);
            }
        });

        RemovePersonFromCacheMapper sut = new RemovePersonFromCacheMapper();
        sut.setCache(cache);

        sut.execute(person);

        context.assertIsSatisfied();
    }
}
