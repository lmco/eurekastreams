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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.junit.Test;

/**
 * Test fixture for PartialHashTagDbMapper.
 */
public class PartialHashTagDbMapperTest extends MapperTest
{
    /**
     * Test execute with no results.
     */
    @Test
    public void testExecuteNoResults()
    {
        PartialHashTagDbMapper sut = new PartialHashTagDbMapper();
        sut.setEntityManager(getEntityManager());

        List<String> hashTags = new ArrayList<String>();
        hashTags.add("#heynow");
        hashTags.add("#foobar");

        PartialMapperResponse<Collection<String>, Collection<HashTag>> results = sut.execute(hashTags);

        assertEquals(0, results.getResponse().size());
        assertEquals(2, results.getUnhandledRequest().size());
        assertTrue(results.getUnhandledRequest().contains("#heynow"));
        assertTrue(results.getUnhandledRequest().contains("#foobar"));
    }

    /**
     * Test execute with all results.
     */
    @Test
    public void testExecuteAllResults()
    {
        final long fooTagId = 1L;
        final long barTagId = 2L;
        final long developmentTagId = 3L;
        final long javaTagId = 4L;

        PartialHashTagDbMapper sut = new PartialHashTagDbMapper();
        sut.setEntityManager(getEntityManager());

        List<String> hashTags = new ArrayList<String>();
        hashTags.add("#foo");
        hashTags.add("#bAR");
        hashTags.add("#java");
        hashTags.add("#javA");
        hashTags.add("#dEvelopMent");

        PartialMapperResponse<Collection<String>, Collection<HashTag>> results = sut.execute(hashTags);

        // check the found responses
        assertEquals(4, results.getResponse().size());
        boolean fooFound = false;
        boolean barFound = false;
        boolean javaFound = false;
        boolean developmentFound = false;
        for (HashTag ht : results.getResponse())
        {
            fooFound = fooFound || ht.getId() == fooTagId;
            barFound = barFound || ht.getId() == barTagId;
            javaFound = javaFound || ht.getId() == javaTagId;
            developmentFound = developmentFound || ht.getId() == developmentTagId;
        }
        assertTrue(fooFound);
        assertTrue(barFound);
        assertTrue(developmentFound);
        assertTrue(javaFound);

        // check the unhandled responses
        assertFalse(results.hasUnhandledRequest());
    }

    /**
     * Test execute with partial results.
     */
    @Test
    public void testExecutePartialResults()
    {
        final long javaTagId = 4L;
        final long developmentTagId = 3L;

        PartialHashTagDbMapper sut = new PartialHashTagDbMapper();
        sut.setEntityManager(getEntityManager());

        List<String> hashTags = new ArrayList<String>();
        hashTags.add("#heynow");
        hashTags.add("#foobar");
        hashTags.add("#java");
        hashTags.add("#dEvelopMent");

        PartialMapperResponse<Collection<String>, Collection<HashTag>> results = sut.execute(hashTags);

        // check the found responses
        assertEquals(2, results.getResponse().size());
        boolean javaFound = false;
        boolean developmentFound = false;
        for (HashTag ht : results.getResponse())
        {
            javaFound = javaFound || ht.getId() == javaTagId;
            developmentFound = developmentFound || ht.getId() == developmentTagId;
        }
        assertTrue(javaFound);
        assertTrue(developmentFound);

        // check the unhandled responses
        assertEquals(2, results.getUnhandledRequest().size());
        assertTrue(results.getUnhandledRequest().contains("#heynow"));
        assertTrue(results.getUnhandledRequest().contains("#foobar"));
    }
}
