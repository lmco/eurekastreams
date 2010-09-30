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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for GetReadOnlyStreamsDbMapper.
 */
public class GetReadOnlyStreamsDbMapperTest extends MapperTest
{
    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        GetReadOnlyStreamsDbMapper sut = new GetReadOnlyStreamsDbMapper();
        sut.setEntityManager(getEntityManager());

        List<Stream> streams = sut.execute();
        assertEquals(4, streams.size());

        boolean followingFound = false;
        boolean everyoneFound = false;
        boolean parentOrgFound = false;
        boolean savedFound = false;

        for(Stream s : streams)
        {
            followingFound = followingFound || s.getName().equals("Following");
            everyoneFound = everyoneFound || s.getName().equals("EUREKA:PARENT_ORG_TAG");
            parentOrgFound = everyoneFound || s.getName().equals("Everyone");
            savedFound = everyoneFound || s.getName().equals("My saved items");
        }

        assertTrue(followingFound);
        assertTrue(everyoneFound);
        assertTrue(parentOrgFound);
        assertTrue(savedFound);
    }
}
