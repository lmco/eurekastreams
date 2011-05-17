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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.DeleteStreamRequest;
import org.junit.Test;

/**
 * Test for DeleteStreamDbMapper.
 * 
 */
public class DeleteStreamDbMapperTest extends MapperTest
{
    /**
     * Test user id.
     */
    private static final long FORDP_ID = 42L;

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        DeleteStreamDbMapper sut = new DeleteStreamDbMapper();
        sut.setEntityManager(getEntityManager());

        sut.execute(new DeleteStreamRequest(FORDP_ID, 2L));

        getEntityManager().flush();
        getEntityManager().clear();

        String query = "from PersonStream where personId=:personId";
        Query q = getEntityManager().createQuery(query).setParameter("personId", FORDP_ID);
        List<PersonStream> result = q.getResultList();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStreamId());
    }

}
