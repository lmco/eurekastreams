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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetPeopleWhoSharedResourceDbMapper.
 */
public class GetPeopleWhoSharedResourceDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetPeopleWhoSharedResourceDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetPeopleWhoSharedResourceDbMapper();
        sut.setEntityManager(getEntityManager());

        // set 2 activities to point to shared resource with id=5 - fordp & smithers
        SharedResource sr = (SharedResource) getEntityManager().createQuery("FROM SharedResource WHERE id=5")
                .getSingleResult();
        Activity act1 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id=6789").getSingleResult();
        Activity act2 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id=6791").getSingleResult();
        Activity act3 = (Activity) getEntityManager().createQuery("FROM Activity WHERE id=6790").getSingleResult();
        act1.setSharedLink(sr);
        act2.setSharedLink(sr);
        act3.setSharedLink(sr);
        getEntityManager().flush();
        getEntityManager().clear();
    }

    /**
     * Test execute, expecting results.
     */
    @Test
    public void testExecuteWithResults()
    {
        final long personId1 = 98;
        final long personId2 = 42;
        getEntityManager().clear();
        List<Long> peopleIds = sut.execute(new SharedResourceRequest("http://foo.com/foo.html"));
        assertEquals(2, peopleIds.size());
        assertTrue(peopleIds.contains(personId1));
        assertTrue(peopleIds.contains(personId2));
    }

    /**
     * Test execute, expecting no results.
     */
    @Test
    public void testExecuteWithNoResults()
    {
        List<Long> peopleIds = sut.execute(new SharedResourceRequest("http://foo.foo.com/foo.html"));
        assertEquals(0, peopleIds.size());
    }

    /**
     * Test execute, with bad shared resource.
     */
    @Test
    public void testExecuteWithBadSharedResource()
    {
        assertEquals(0, sut.execute(new SharedResourceRequest("http://blah.com")).size());
    }
}
