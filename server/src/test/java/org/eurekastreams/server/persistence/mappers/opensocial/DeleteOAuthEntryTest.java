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
package org.eurekastreams.server.persistence.mappers.opensocial;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteOAuthEntry} mapper.
 * 
 */
public class DeleteOAuthEntryTest extends MapperTest
{
    /**
     * System under test.
     */
    private DeleteOAuthEntry sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteOAuthEntry();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test deleting an entry given the token.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteEntryByToken()
    {
        final long entryId = 103L;
        sut.execute("token3");

        Query q = getEntityManager().createQuery("from OAuthDomainEntry e where e.id = :tokenId").setParameter(
                "tokenId", entryId);

        List results = q.getResultList();
        Assert.assertEquals(0, results.size());
    }
}
