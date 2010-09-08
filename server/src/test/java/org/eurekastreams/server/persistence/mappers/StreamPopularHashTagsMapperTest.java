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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReport;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StreamPopularHashTagsMapper.
 */
public class StreamPopularHashTagsMapperTest
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
     * Mapper to get the popular hashtags for a stream.
     */
    private final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport>
    // line break
    popularHashTagsMapper = context.mock(DomainMapper.class);

    /**
     * Number of minutes to expire the cached content.
     */
    private final Integer expireInMinutes = 300;

    /**
     * System under test.
     */
    private final StreamPopularHashTagsMapper sut = new StreamPopularHashTagsMapper(popularHashTagsMapper,
            expireInMinutes);

    /**
     * Test execute when the hashtags are not found in cache.
     */
    @Test
    public void testExecuteNotInCache()
    {
        final StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest(ScopeType.PERSON, "foobar");

        context.checking(new Expectations()
        {
            {
                oneOf(popularHashTagsMapper).execute(request);
                will(returnValue(null));
            }
        });

        assertNull(sut.execute(request));
        context.assertIsSatisfied();
    }

    /**
     * Test execute when the hashtags are found in cache and are current.
     */
    @Test
    public void testExecuteFoundInCacheAndCurrent()
    {
        final StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest(ScopeType.PERSON, "foobar");
        ArrayList<String> hashTags = new ArrayList<String>();
        hashTags.add("#foo");
        hashTags.add("#bar");
        Calendar cal = Calendar.getInstance();
        final StreamPopularHashTagsReport response = new StreamPopularHashTagsReport(hashTags, cal.getTime());

        context.checking(new Expectations()
        {
            {
                oneOf(popularHashTagsMapper).execute(request);
                will(returnValue(response));
            }
        });
        List<String> returnedHashTags = sut.execute(request).getPopularHashTags();
        assertEquals(2, returnedHashTags.size());
        assertTrue(returnedHashTags.containsAll(hashTags));
        context.assertIsSatisfied();
    }

    /**
     * Test execute when the hashtags are found in cache and expired.
     */
    @Test
    public void testExecuteFoundInCacheAndExpired()
    {
        final StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest(ScopeType.PERSON, "foobar");
        ArrayList<String> hashTags = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        final int twentyDaysAgo = -480;
        cal.add(Calendar.HOUR, twentyDaysAgo);
        final StreamPopularHashTagsReport response = new StreamPopularHashTagsReport(hashTags, cal.getTime());

        context.checking(new Expectations()
        {
            {
                oneOf(popularHashTagsMapper).execute(request);
                will(returnValue(response));
            }
        });
        assertNull(sut.execute(request));
        context.assertIsSatisfied();
    }
}
