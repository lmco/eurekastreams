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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyMessageCountDbMapper.
 */
public class GetDailyMessageCountDbMapperTest extends MapperTest
{
    /**
     * April 4th, 2011 in ticks.
     */
    private final long april4th2011 = 1301944331000L;

    /**
     * System under test.
     */
    private GetDailyMessageCountDbMapper sut;

    /**
     * Setup - set 2 activities and 1 comment to april 4th, 2011.
     */
    @Before
    public void setup()
    {
        Activity act;
        Comment comment;
        Date april4th = new Date(april4th2011);

        sut = new GetDailyMessageCountDbMapper();
        sut.setEntityManager(getEntityManager());

        final long actId1 = 6789L;
        act = getEntityManager().find(Activity.class, actId1);
        act.setPostedTime(april4th);
        getEntityManager().persist(act);

        final long actId2 = 6790L;
        act = getEntityManager().find(Activity.class, actId2);
        act.setPostedTime(april4th);
        getEntityManager().persist(act);

        final long commentId = 9;
        comment = getEntityManager().find(Comment.class, commentId);
        comment.setTimeSent(april4th);
        getEntityManager().persist(comment);

        getEntityManager().flush();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        Date april4th = new Date(april4th2011 + 8); // change the date a little bit
        assertEquals(3, (long) sut.execute(april4th));
    }
}
