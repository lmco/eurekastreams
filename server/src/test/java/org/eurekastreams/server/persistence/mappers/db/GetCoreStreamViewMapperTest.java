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

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link GetCoreStreamViewMapper}.
 *
 */
public class GetCoreStreamViewMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetCoreStreamViewMapper sut;

    /**
     * The actual value of the PeopleFollow Stream View Id in the test dataset.
     */
    private static final long PEOPLEFOLLOW_STREAM_VIEW_ID = 5000L;

    /**
     * The actual value of the ParentOrg Stream View Id in the test dataset.
     */
    private static final long PARENTORG_STREAM_VIEW_ID = 5001L;

    /**
     * The actual value of the Everyone Stream View Id in the test dataset.
     */
    private static final long EVERYONE_STREAM_VIEW_ID = 5002L;

    /**
     * The actual value of the Starred Stream View Id in the test dataset.
     */
    private static final long STARRED_STREAM_VIEW_ID = 5003L;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new GetCoreStreamViewMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test that the StreamView returned matches the dataset.
     */
    @Test
    public void testRetrieval()
    {
        StreamView peopleFollowStreamView = sut.execute(Type.PEOPLEFOLLOW);
        assertEquals(PEOPLEFOLLOW_STREAM_VIEW_ID, peopleFollowStreamView.getId());

        StreamView parentOrgStreamView = sut.execute(Type.PARENTORG);
        assertEquals(PARENTORG_STREAM_VIEW_ID, parentOrgStreamView.getId());

        StreamView everyoneStreamView = sut.execute(Type.EVERYONE);
        assertEquals(EVERYONE_STREAM_VIEW_ID, everyoneStreamView.getId());

        StreamView starredStreamView = sut.execute(Type.STARRED);
        assertEquals(STARRED_STREAM_VIEW_ID, starredStreamView.getId());
    }

    /**
     * Test that retrieving the NOTSET StreamView type fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailedRetrieval()
    {
        sut.execute(Type.NOTSET);
    }
}
