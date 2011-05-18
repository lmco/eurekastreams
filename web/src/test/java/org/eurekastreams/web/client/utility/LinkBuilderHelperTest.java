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
package org.eurekastreams.web.client.utility;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the link builder.
 */
public class LinkBuilderHelperTest
{
    /** SUT. */
    private LinkBuilderHelper sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new LinkBuilderHelper();
    }

    /**
     * Tests getting profile page mappings.
     */
    @Test
    public void testGetEntityProfilePage()
    {
        assertEquals(Page.PEOPLE, sut.getEntityProfilePage(EntityType.PERSON));
        assertEquals(Page.GROUPS, sut.getEntityProfilePage(EntityType.GROUP));
    }

}
