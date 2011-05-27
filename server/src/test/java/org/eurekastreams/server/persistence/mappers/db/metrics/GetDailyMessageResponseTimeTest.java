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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import java.util.Date;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetDailyMessageResponseTime.
 * 
 */
@SuppressWarnings("deprecation")
public class GetDailyMessageResponseTimeTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetDailyMessageResponseTime sut;

    /**
     * Year.
     */
    private final int year = 2011;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetDailyMessageResponseTime();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        // set up activity with two comments posted same day, quickest comment is 10 min
        Activity a1 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = 6789").getSingleResult();
        Comment a1c1 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 1").getSingleResult();
        Comment a1c2 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 2").getSingleResult();

        a1.setPostedTime(new Date(year, 4, 1, 7, 0, 0));
        a1c1.setTimeSent(new Date(year, 4, 1, 7, 2, 0));
        a1c2.setTimeSent(new Date(year, 4, 1, 7, 4, 0));

        // set up another activity with two comments post same day, quickest comment is 20 min.
        Activity a2 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = 6790").getSingleResult();
        Comment a2c1 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 5").getSingleResult();
        Comment a2c2 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 6").getSingleResult();

        a2.setPostedTime(new Date(year, 4, 1, 8, 0, 0));
        a2c1.setTimeSent(new Date(year, 4, 1, 8, 6, 0));
        a2c2.setTimeSent(new Date(year, 4, 1, 8, 8, 0));

        getEntityManager().flush();
        getEntityManager().clear();

        // execute mapper assert avg response time is 15 min.
        Assert.assertEquals(4, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(year, 4, 1), null)));
    }

    /**
     * Test execute(). Verify date selection is working.
     */
    @Test
    public void testExecuteDifferentDays()
    {
        // set up activity with two comments posted same day, quickest comment is 10 min
        Activity a1 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = 6789").getSingleResult();
        Comment a1c1 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 1").getSingleResult();
        Comment a1c2 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 2").getSingleResult();

        a1.setPostedTime(new Date(year, 4, 1, 7, 0, 0));
        a1c1.setTimeSent(new Date(year, 4, 1, 7, 2, 0));
        a1c2.setTimeSent(new Date(year, 4, 1, 7, 4, 0));

        // set up another activity with two comments post different day, quickest comment is 20 min.
        Activity a2 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = 6790").getSingleResult();
        Comment a2c1 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 5").getSingleResult();
        Comment a2c2 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 6").getSingleResult();

        a2.setPostedTime(new Date(year, 4, 2, 8, 0, 0));
        a2c1.setTimeSent(new Date(year, 4, 2, 8, 6, 0));
        a2c2.setTimeSent(new Date(year, 4, 2, 8, 8, 0));

        getEntityManager().flush();
        getEntityManager().clear();

        // execute mapper assert avg response time is 15 min.
        Assert.assertEquals(2, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(year, 4, 1), null)));
    }

    /**
     * Test execute(). Make sure activities with comments not posted same day are not calculated.
     */
    @Test
    public void testExecuteActivityDayOld()
    {
        Activity a1 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id = 6789").getSingleResult();
        Comment a1c1 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 1").getSingleResult();
        Comment a1c2 = (Comment) getEntityManager().createQuery("FROM Comment WHERE id = 2").getSingleResult();

        a1.setPostedTime(new Date(year, 4, 1, 7, 0, 0));
        a1c1.setTimeSent(new Date(year, 4, 2, 7, 2, 0));
        a1c2.setTimeSent(new Date(year, 4, 2, 7, 4, 0));

        getEntityManager().flush();
        getEntityManager().clear();

        Assert.assertEquals(0, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(year, 4, 2), null)));
    }
}