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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteStreamViewAndRelatedSearches.
 */
public class DeleteStreamViewAndRelatedSearchesTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteStreamViewAndRelatedSearches sut;

    /**
     * Id of the streamview.
     */
    private static final long STREAMVIEW_ID = 18;

    /**
     * Id of streamview with an associated streamsearch.
     */
    private static final long STREAMVIEW_ID_WITH_SEARCH = 19;

    /**
     * Id of streamview with an associated streamsearch.
     */
    private static final long SEARCH_ID = 3;

    /**
     * Test execute with just a streamview.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        assertTrue(sut.execute(STREAMVIEW_ID));

        List<Long> results = getEntityManager().createQuery("from StreamView where id = :id").setParameter("id",
                STREAMVIEW_ID).getResultList();
        assertEquals(0, results.size());
    }

    /**
     * Test execute without a streamview associated to a streamsearch.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithSearch()
    {
        assertTrue(sut.execute(STREAMVIEW_ID_WITH_SEARCH));

        List<Long> results = getEntityManager().createQuery("from StreamView where id = :id").setParameter("id",
                STREAMVIEW_ID_WITH_SEARCH).getResultList();
        assertEquals(0, results.size());

        results = getEntityManager().createQuery("from StreamSearch where id = :id").setParameter("id", SEARCH_ID)
                .getResultList();
        assertEquals(0, results.size());
    }
}
