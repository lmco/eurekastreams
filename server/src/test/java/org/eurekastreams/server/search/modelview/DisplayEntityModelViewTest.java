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
package org.eurekastreams.server.search.modelview;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.junit.Test;

/**
 * Unit test for {@link DisplayEntityModelView}.
 *
 */
public class DisplayEntityModelViewTest
{
    /**
     * Test getters/setters.
     */
    @Test
    public void testIt()
    {
        DisplayEntityModelView sut = new DisplayEntityModelView();
        sut.setDisplayName("DisplayName");
        sut.setType(EntityType.PERSON);
        sut.setUniqueKey("UniqueKey");

        assertEquals("DisplayName", sut.getDisplayName());
        assertEquals(EntityType.PERSON, sut.getType());
        assertEquals("UniqueKey", sut.getUniqueKey());
    }

}
