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

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for RemoveDomainGroupFromCacheMapper.
 */
public class RemoveDomainGroupFromCacheMapperTest
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
        final DomainGroup group = context.mock(DomainGroup.class);
        final Long groupId = 2983L;
        final String groupShortName = "sdlkfjs";
        final StreamScope scope = context.mock(StreamScope.class);
        final Long scopeId = 2384L;

        context.checking(new Expectations()
        {
            {
                allowing(group).getId();
                will(returnValue(groupId));

                oneOf(group).getStreamScope();
                will(returnValue(scope));

                allowing(group).getShortName();
                will(returnValue(groupShortName));

                allowing(scope).getId();
                will(returnValue(scopeId));

                oneOf(cache).delete(CacheKeys.GROUP_BY_ID + groupId);
                oneOf(cache).delete(CacheKeys.FOLLOWERS_BY_GROUP + groupId);
                oneOf(cache).delete(CacheKeys.STREAM_BY_ID + scopeId);
            }
        });

        RemoveDomainGroupFromCacheMapper sut = new RemoveDomainGroupFromCacheMapper();
        sut.setCache(cache);

        sut.execute(group);
    }

}
