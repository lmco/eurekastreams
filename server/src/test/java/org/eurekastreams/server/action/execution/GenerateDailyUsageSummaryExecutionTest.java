/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.Date;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.strategies.GetDateFromDaysAgoStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for GenerateDailyUsageSummaryExecution.
 */
public class GenerateDailyUsageSummaryExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Strategy to get a Date from a day ago.
     */
    private final GetDateFromDaysAgoStrategy daysAgoDateStrategy = context.mock(GetDateFromDaysAgoStrategy.class);

    /**
     * Mapper to get a single day's DailyUsageSummary.
     */
    private final DomainMapper<Date, DailyUsageSummary> getDailyUsageSummaryByDateMapper = context.mock(
            DomainMapper.class, "getDailyUsageSummaryByDateMapper");

    /**
     * Mapper to get a day's message count.
     */
    private final DomainMapper<Date, Long> getDailyMessageCountMapper = context.mock(DomainMapper.class,
            "getDailyMessageCountMapper");

    /**
     * Mapper to get a day's page view count.
     */
    private final DomainMapper<Date, Long> getDailyPageViewCountMapper = context.mock(DomainMapper.class,
            "getDailyPageViewCountMapper");

    /**
     * Mapper to get a day's stream contributor count.
     */
    private final DomainMapper<Date, Long> getDailyStreamContributorCountMapper = context.mock(DomainMapper.class,
            "getDailyStreamContributorCountMapper");

    /**
     * Mapper to get a day's stream view count.
     */
    private final DomainMapper<Date, Long> getDailyStreamViewCountMapper = context.mock(DomainMapper.class,
            "getDailyStreamViewCountMapper");

    /**
     * Mapper to get a day's stream viewer count.
     */
    private final DomainMapper<Date, Long> getDailyStreamViewerCountMapper = context.mock(DomainMapper.class,
            "getDailyStreamViewerCountMapper");

    /**
     * Mapper to get a day's unique visitor count.
     */
    private final DomainMapper<Date, Long> getDailyUniqueVisitorCountMapper = context.mock(DomainMapper.class,
            "getDailyUniqueVisitorCountMapper");

    /**
     * Mapper to insert the DailyUsageSummary entity.
     */
    private final InsertMapperFake insertMapper = new InsertMapperFake();

    /**
     * Mapper to delete old UsageMetric data.
     */
    private final DomainMapper<Serializable, Serializable> usageMetricDataCleanupMapper = context.mock(
            DomainMapper.class, "usageMetricDataCleanupMapper");

    /**
     * Input to mapper.
     */
    private final ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Test execute when we already have data from yesterday.
     */
    @Test
    public void testExecuteWithDataAlreadyExisting()
    {
        GenerateDailyUsageSummaryExecution sut = new GenerateDailyUsageSummaryExecution(daysAgoDateStrategy,
                getDailyUsageSummaryByDateMapper, getDailyMessageCountMapper, getDailyPageViewCountMapper,
                getDailyStreamContributorCountMapper, getDailyStreamViewCountMapper, getDailyStreamViewerCountMapper,
                getDailyUniqueVisitorCountMapper, insertMapper, usageMetricDataCleanupMapper);

        final DailyUsageSummary existingSummary = context.mock(DailyUsageSummary.class);
        final Date date = new Date();

        context.checking(new Expectations()
        {
            {
                oneOf(daysAgoDateStrategy).execute(with(1));
                will(returnValue(date));

                oneOf(getDailyUsageSummaryByDateMapper).execute(with(date));
                will(returnValue(existingSummary));
            }
        });

        Serializable result = sut.execute(actionContext);
        Assert.assertEquals(Boolean.FALSE, result);

        context.assertIsSatisfied();
    }

    /**
     * Test execute when we don't have data from yesterday.
     */
    @Test
    public void testExecuteWithNoDataAlreadyExisting()
    {
        GenerateDailyUsageSummaryExecution sut = new GenerateDailyUsageSummaryExecution(daysAgoDateStrategy,
                getDailyUsageSummaryByDateMapper, getDailyMessageCountMapper, getDailyPageViewCountMapper,
                getDailyStreamContributorCountMapper, getDailyStreamViewCountMapper, getDailyStreamViewerCountMapper,
                getDailyUniqueVisitorCountMapper, insertMapper, usageMetricDataCleanupMapper);

        final Date date = new Date();
        final long uniqueVisitorCount = 1L;
        final long pageViewCount = 2L;
        final long streamViewerCount = 3L;
        final long streamViewCount = 4L;
        final long streamContributorCount = 5L;
        final long messageCount = 6L;

        context.checking(new Expectations()
        {
            {
                oneOf(daysAgoDateStrategy).execute(with(1));
                will(returnValue(date));

                // no data found
                oneOf(getDailyUsageSummaryByDateMapper).execute(with(date));
                will(returnValue(null));

                oneOf(getDailyUniqueVisitorCountMapper).execute(with(date));
                will(returnValue(uniqueVisitorCount));

                oneOf(getDailyPageViewCountMapper).execute(with(date));
                will(returnValue(pageViewCount));

                oneOf(getDailyStreamViewerCountMapper).execute(with(date));
                will(returnValue(streamViewerCount));

                oneOf(getDailyStreamViewCountMapper).execute(with(date));
                will(returnValue(streamViewCount));

                oneOf(getDailyStreamContributorCountMapper).execute(with(date));
                will(returnValue(streamContributorCount));

                oneOf(getDailyMessageCountMapper).execute(with(date));
                will(returnValue(messageCount));

                oneOf(usageMetricDataCleanupMapper).execute(with(any(Serializable.class)));

            }
        });

        this.insertMapper.setRequest(null);
        Serializable result = sut.execute(actionContext);
        Assert.assertEquals(Boolean.TRUE, result);

        DailyUsageSummary ds = (DailyUsageSummary) insertMapper.getRequest().getDomainEnity();
        Assert.assertEquals(uniqueVisitorCount, ds.getUniqueVisitorCount());
        Assert.assertEquals(pageViewCount, ds.getPageViewCount());
        Assert.assertEquals(streamViewerCount, ds.getStreamViewerCount());
        Assert.assertEquals(streamViewCount, ds.getStreamViewCount());
        Assert.assertEquals(streamContributorCount, ds.getStreamContributorCount());
        Assert.assertEquals(messageCount, ds.getMessageCount());
        Assert.assertEquals(date, ds.getUsageDate());

        context.assertIsSatisfied();
    }

    /**
     * Fake class to hang onto the generated DailyUsageSummary so we can test the values.
     */
    private class InsertMapperFake implements DomainMapper<PersistenceRequest<DailyUsageSummary>, Boolean>
    {
        /**
         * The request.
         */
        private PersistenceRequest request;

        /**
         * execute.
         * 
         * @param inRequest
         *            the request to persist.
         * @return true
         */
        @Override
        public Boolean execute(final PersistenceRequest<DailyUsageSummary> inRequest)
        {
            request = inRequest;
            return Boolean.TRUE;
        }

        /**
         * @return the request
         */
        public PersistenceRequest getRequest()
        {
            return request;
        }

        /**
         * @param inRequest
         *            the request to set
         */
        public void setRequest(final PersistenceRequest inRequest)
        {
            request = inRequest;
        }

    }
}
