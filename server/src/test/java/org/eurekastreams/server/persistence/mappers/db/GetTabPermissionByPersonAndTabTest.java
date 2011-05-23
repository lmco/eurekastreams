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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link GetTabPermissionByPersonAndTab} class.
 */
public class GetTabPermissionByPersonAndTabTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetTabPermissionByPersonAndTab sut;
    
    /**
     * Test person id.
     */
    private static final String TEST_PERSON_ACCOUNTID = "fordp";
    
    /**
     * Test tab id.
     */
    private static final Long TEST_TAB_ID = 1097L;
    
    /**
     * Test person id.
     */
    private static final String TEST_PERSON_ACCOUNTID2 = "fordp2";
    
    /**
     * Test tab id.
     */
    private static final Long TEST_TAB_ID2 = 28L;
    
    /**
     * Startup method to be run before any tests that sets up the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetTabPermissionByPersonAndTab();
        sut.setEntityManager(getEntityManager());
    }
    
    /**
     * Test the successful path.
     */
    @Test
    public void testExecute()
    {
        boolean result = sut.execute(TEST_PERSON_ACCOUNTID, TEST_TAB_ID);
        
        assertTrue(result);
    }
    
    /**
     * Test the unsuccessful path.
     */
    @Test
    public void testExecuteInvalidPerms()
    {
        boolean result = sut.execute(TEST_PERSON_ACCOUNTID2, TEST_TAB_ID2);
        
        assertFalse(result);
    }
}
