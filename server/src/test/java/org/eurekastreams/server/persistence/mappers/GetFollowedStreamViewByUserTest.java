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

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for the GetFollowedStreamViewByUser mapper class.
 *
 */
public class GetFollowedStreamViewByUserTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetFollowedStreamViewByUser sut;
    
    /**
     * Id of the Following stream that all users should have.
     */
    private static final Long FOLLOWING_STREAM_ID = 5000L;
    
    /**
     * Id of a test user.
     */
    private static final Long TEST_USER_1 = 42L;
    
    /**
     * Id of a test user.
     */
    private static final Long TEST_USER_2 = 142L;
    
    /**
     * Ensure that the value pulled back for a given user is correct based
     * on the dataset.
     */
    @Test
    public void testExecute()
    {
        /**
         * All users should have the same id returned for this mapper.
         */
        assertEquals(new Long(FOLLOWING_STREAM_ID), sut.execute(TEST_USER_1));
        assertEquals(new Long(FOLLOWING_STREAM_ID), sut.execute(TEST_USER_2));
    }
}
