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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Tests ReorderStreamsDbMapper class.
 */
public class ReorderStreamsDbMapperTest extends MapperTest
{
    /**
     * Test user id.
     */
    private static final long FORDP_ID = 42L;

    /**
     * New hiddden line index.
     */
    private static int newHiddenLineIndex = 1;

    // TODO: create a test where we have items [1, 2, 3, 4, 5] and move #2 to index 0

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        ReorderStreamsDbMapper sut = new ReorderStreamsDbMapper();
        sut.setEntityManager(getEntityManager());

        // Check the initial order
        String query = "from PersonStream where personId=:personId order by streamIndex";
        Query q = getEntityManager().createQuery(query).setParameter("personId", FORDP_ID);

        List<PersonStream> result = q.getResultList();
        assertEquals(2L, result.get(0).getStreamId());
        assertEquals(1L, result.get(1).getStreamId());

        List<PersonStream> newOrder = new ArrayList<PersonStream>();
        newOrder.add(result.get(1));
        newOrder.add(result.get(0));

        sut.execute(FORDP_ID, newOrder, newHiddenLineIndex);

        getEntityManager().flush();

        // Check the resulting order
        q = getEntityManager().createQuery(query).setParameter("personId", FORDP_ID);
        result = q.getResultList();
        assertEquals(1L, result.get(0).getStreamId());
        assertEquals(2L, result.get(1).getStreamId());
    }
}
