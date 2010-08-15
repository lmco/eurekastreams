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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.stream.HashTag;
import org.junit.Test;

/**
 * Test fixture for HashTagBuilderMapper.
 */
public class HashTagBuilderMapperTest
{
    /**
     * System under test.
     */
    private HashTagBuilderMapper sut = new HashTagBuilderMapper();

    /**
     * Test execute() with none.
     */
    @Test
    public void testExecuteWithNone()
    {
        Collection<HashTag> results = sut.execute(new ArrayList<String>());
        assertEquals(0, results.size());
    }

    /**
     * Test execute() with dupes.
     */
    @Test
    public void testExecuteWithDupes()
    {
        List<String> htContents = new ArrayList<String>();
        htContents.add("hi");
        htContents.add("#hi");
        htContents.add("HI");
        htContents.add("HI");
        htContents.add("There");

        Collection<HashTag> results = sut.execute(htContents);
        assertEquals(2, results.size());

        boolean hiFound = false;
        boolean thereFound = false;

        for (HashTag ht : results)
        {
            hiFound = hiFound || ht.getContent().equals("#hi");
            thereFound = thereFound || ht.getContent().equals("#there");
        }

        assertTrue(hiFound && thereFound);
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        List<String> htContents = new ArrayList<String>();
        htContents.add("#hi");
        htContents.add("#there");

        Collection<HashTag> results = sut.execute(htContents);
        assertEquals(2, results.size());

        boolean hiFound = false;
        boolean thereFound = false;

        for (HashTag ht : results)
        {
            hiFound = hiFound || ht.getContent().equals("#hi");
            thereFound = thereFound || ht.getContent().equals("#there");
        }

        assertTrue(hiFound && thereFound);
    }

}
