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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.junit.Test;

/**
 * Test fixture for GetStreamScopesByStreamViewId.
 */
public class GetStreamScopesByStreamViewIdTest extends MapperTest
{
    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        GetStreamScopesByStreamViewId sut = new GetStreamScopesByStreamViewId();
        sut.setEntityManager(getEntityManager());

        final long streamViewId = 382L;
        List<StreamScope> scopes = sut.execute(streamViewId);

        assertEquals(3, scopes.size());

        // make sure the following ids exist: 345, 346, 347
        final long id874 = 874L;
        final long id837433 = 837433L;
        final long id347 = 347L;

        boolean was874Found = false;
        boolean was837433Found = false;
        boolean was347Found = false;
        for (StreamScope scope : scopes)
        {
            was874Found = was874Found || scope.getId() == id874;
            was837433Found = was837433Found || scope.getId() == id837433;
            was347Found = was347Found || scope.getId() == id347;
        }

        assertTrue(was874Found);
        assertTrue(was837433Found);
        assertTrue(was347Found);
    }
}
