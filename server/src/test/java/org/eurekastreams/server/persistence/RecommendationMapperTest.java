/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.Recommendation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for JpaRecommendationMapper.
 */
public class RecommendationMapperTest extends DomainEntityMapperTest
{
    /**
     * Subject under test.
     */
    @Autowired
    private RecommendationMapper jpaRecommendationMapper;

    /**
     * Mr. Burns' open social id.
     */
    private static final String MRBURNS_OPENSOCIAL_ID = "2d359911-0977-418a-9490-57e8252b1a99";

    /**
     * Try to get the recommendations for Mr. Burns.
     */
    @Test
    public void findBySubjectOpenSocialId()
    {
        List<Recommendation> actual = jpaRecommendationMapper.findBySubjectOpenSocialId(MRBURNS_OPENSOCIAL_ID);

        assertEquals(2, actual.size());

        // check that the first recommendation is the most recent valid one
        assertEquals(2, actual.get(0).getId());

        // check that the second recommendation the next more recent valid id
        assertEquals(1, actual.get(1).getId());
    }

    /**
     * Try to get the recommendations for Mr. Burns maxxed at 1.
     */
    @Test
    public void findBySubjectOpenSocialIdMaxResults()
    {
        List<Recommendation> actual = jpaRecommendationMapper.findBySubjectOpenSocialId(MRBURNS_OPENSOCIAL_ID, 1);

        assertEquals(1, actual.size());
    }

    /**
     * Make sure we get a null back when there are no results.
     */
    @Test
    public void findBySubjectOpenSocialIdWithNoResults()
    {
        List<Recommendation> actual = jpaRecommendationMapper.findBySubjectOpenSocialId("no-such-id");

        assertEquals(0, actual.size());
    }

    /**
     * Test delete.
     */
    @Test(expected = NoResultException.class)
    public void delete()
    {
        long id = 3L;
        jpaRecommendationMapper.delete(id);
        jpaRecommendationMapper.findById(id);
    }

    /**
     * Test the domain name.
     */
    @Test
    public void getDomainEntityName()
    {
        assertEquals("Recommendation", jpaRecommendationMapper.getDomainEntityName());
    }
}
