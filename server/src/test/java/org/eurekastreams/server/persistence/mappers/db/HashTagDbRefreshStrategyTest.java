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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for HashTagDbRefreshStrategy.
 */
public class HashTagDbRefreshStrategyTest extends MapperTest
{
    /**
     * Test refresh when the hashtag already exists.
     */
    @Test
    public void testRefreshWhenHashTagAlreadyExists()
    {
        HashTagDbRefreshStrategy sut = new HashTagDbRefreshStrategy();
        sut.setEntityManager(getEntityManager());

        List<String> existingHashTagContents = new ArrayList<String>();
        existingHashTagContents.add("#foo");
        existingHashTagContents.add("#bar");

        List<HashTag> existingHashTags = getEntityManager().createQuery("FROM HashTag WHERE id IN(1,2)")
                .getResultList();

        sut.refresh(existingHashTagContents, existingHashTags);

        // make sure all of the hashtags were replaced with database entities
        assertAllHashTagsHaveIds(existingHashTags);

        List<HashTag> resultsAfterRefresh = getEntityManager().createQuery("FROM HashTag").getResultList();
        assertEquals(4, resultsAfterRefresh.size());
    }

    /**
     * Test refresh when the hashtag doesn't exist.
     */
    @Test
    public void testRefreshWhenHashDoesntExist()
    {
        HashTagDbRefreshStrategy sut = new HashTagDbRefreshStrategy();
        sut.setEntityManager(getEntityManager());

        List<String> existingHashTagContents = new ArrayList<String>();
        existingHashTagContents.add("potato");
        existingHashTagContents.add("#rice");

        List<HashTag> existingHashTags = new ArrayList<HashTag>();
        existingHashTags.add(new HashTag("potato"));
        existingHashTags.add(new HashTag("#rice"));

        sut.refresh(existingHashTagContents, existingHashTags);

        // make sure all of the hashtags were replaced with database entities
        assertAllHashTagsHaveIds(existingHashTags);

        assertEquals(6, getEntityManager().createQuery("FROM HashTag").getResultList().size());

        assertEquals(2, getEntityManager().createQuery("FROM HashTag WHERE content IN ('#potato', '#rice')")
                .getResultList().size());
    }

    /**
     * Test refresh when the hashtag already exists.
     */
    @Test
    public void testRefreshWhenSomeTagsAlreadyExists()
    {
        HashTagDbRefreshStrategy sut = new HashTagDbRefreshStrategy();
        sut.setEntityManager(getEntityManager());

        List<String> existingHashTagContents = new ArrayList<String>();
        existingHashTagContents.add("#foo");
        existingHashTagContents.add("#bar");
        existingHashTagContents.add("potato");
        existingHashTagContents.add("#rice");

        List<HashTag> existingHashTags = new ArrayList<HashTag>();
        existingHashTags.add(new HashTag("potato"));
        existingHashTags.add(new HashTag("#rice"));
        existingHashTags.addAll(getEntityManager().createQuery("FROM HashTag WHERE id IN(1,2)").getResultList());

        sut.refresh(existingHashTagContents, existingHashTags);

        // make sure all of the hashtags were replaced with database entities
        assertAllHashTagsHaveIds(existingHashTags);

        assertEquals(6, getEntityManager().createQuery("FROM HashTag").getResultList().size());

        assertEquals(2, getEntityManager().createQuery("FROM HashTag WHERE content IN ('#potato', '#rice')")
                .getResultList().size());

    }

    /**
     * Loop through the collection of hashtags, making sure they each have ids.
     *
     * @param existingHashTags
     *            the hash tags to verify ids for
     */
    private void assertAllHashTagsHaveIds(final List<HashTag> existingHashTags)
    {
        for (HashTag ht : existingHashTags)
        {
            assertTrue(ht.getId() > 0);
        }
    }
}
