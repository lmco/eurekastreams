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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for FindOrInsertSharedResourceByUniqueKeyDbMapper.
 */
public class FindOrInsertSharedResourceByUniqueKeyDbMapperTest extends MapperTest
{
    /**
     * Test finding a shared resource that doesn't exist finds and inserts it.
     */
    @Test
    public void testExecuteWithNoMatch()
    {
        FindOrInsertSharedResourceByUniqueKeyDbMapper sut = new FindOrInsertSharedResourceByUniqueKeyDbMapper();
        sut.setEntityManager(getEntityManager());

        SharedResource sr = sut.execute(new SharedResource(BaseObjectType.BOOKMARK, "http://foofff.com/foo.html"));
        assertNotNull(sr);
        assertTrue(sr.getId() > 0);
    }

    /**
     * Test finding a shared resource with bad inputs.
     */
    @Test
    public void testExecuteWithBadInput()
    {
        FindOrInsertSharedResourceByUniqueKeyDbMapper sut = new FindOrInsertSharedResourceByUniqueKeyDbMapper();
        sut.setEntityManager(getEntityManager());

        assertNull(sut.execute(new SharedResource(BaseObjectType.BOOKMARK, null)));
        assertNull(sut.execute(new SharedResource(null, "http://foofff.com/foo.html")));
    }

    /**
     * Test executing with a match.
     */
    @Test
    public void testExecuteWithMatch()
    {
        FindOrInsertSharedResourceByUniqueKeyDbMapper sut = new FindOrInsertSharedResourceByUniqueKeyDbMapper();
        sut.setEntityManager(getEntityManager());

        SharedResource sr = sut.execute(new SharedResource(BaseObjectType.BOOKMARK, "http://foo.com/foo.html"));
        assertNotNull(sr);
        assertEquals(BaseObjectType.BOOKMARK, sr.getResourceType());
        assertEquals(5L, sr.getId());
    }

    /**
     * Test executing with a match of a different case.
     */
    @Test
    public void testExecuteWithMatchWithDifferentCase()
    {
        FindOrInsertSharedResourceByUniqueKeyDbMapper sut = new FindOrInsertSharedResourceByUniqueKeyDbMapper();
        sut.setEntityManager(getEntityManager());

        SharedResource sr = sut.execute(new SharedResource(BaseObjectType.BOOKMARK, "http://FOO.com/foo.html"));
        assertNotNull(sr);
        assertEquals(BaseObjectType.BOOKMARK, sr.getResourceType());
        assertEquals(5L, sr.getId());
    }
}
