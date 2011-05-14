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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReportDTO;
import org.junit.Test;

/**
 * Test fixture for StreamPopularHashTagsDbMapper.
 */
public class StreamPopularHashTagsDbMapperTest extends MapperTest
{
    /**
     * Test execute for a person.
     */
    @Test
    public void testExecuteForPerson()
    {
        getEntityManager().createQuery("UPDATE StreamHashTag SET activityDate=:activityDate").setParameter(
                "activityDate", Calendar.getInstance().getTime()).executeUpdate();

        StreamPopularHashTagsDbMapper sut = new StreamPopularHashTagsDbMapper(9, 9);
        sut.setEntityManager(getEntityManager());

        StreamPopularHashTagsReportDTO tagsReport = sut.execute(new StreamPopularHashTagsRequest(ScopeType.PERSON,
                "smithers"));
        List<String> tags = tagsReport.getPopularHashTags();
        assertEquals(2, tags.size());

        // Smithers has tags #soda(5), one of #foo(1)
        assertEquals("#soda", tags.get(0));
        assertEquals("#foo", tags.get(1));
    }
}
