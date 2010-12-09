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

import static junit.framework.Assert.assertEquals;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests GetTabTemplatesWithOwnersForGadgetDefinition.
 */
public class GetTabTemplatesWithOwnersForGadgetDefinitionTest extends MapperTest
{
    /** System under test. */
    @Autowired
    private GetTabTemplatesWithOwnersForGadgetDefinition sut;

    /**
     * Test a multi-row case.
     */
    @Test
    public void testExecuteMultipleRows()
    {
        final long id = 1831L;
        final long template1 = 1097L;
        final long person1 = 4507L;
        final long template2 = 1097L;
        final long person2 = 98L;
        final long template3 = 3253L;
        final long person3 = 98L;

        Collection<CompressGadgetZoneRequest> list = sut.execute(id);

        assertEquals(3, list.size());
        CompressGadgetZoneRequest expected = new CompressGadgetZoneRequest(template1, 0, person1);
        assertTrue(Matchers.hasItem(equalInternally(expected)).matches(list));
        expected = new CompressGadgetZoneRequest(template2, 0, person2);
        assertTrue(Matchers.hasItem(equalInternally(expected)).matches(list));
        expected = new CompressGadgetZoneRequest(template3, 0, person3);
        assertTrue(Matchers.hasItem(equalInternally(expected)).matches(list));
    }
}
