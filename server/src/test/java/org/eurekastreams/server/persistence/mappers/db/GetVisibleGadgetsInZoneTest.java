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

import java.util.List;

import org.eurekastreams.server.action.request.gallery.CompressGadgetZoneRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests GetVisibleGadgetsInZone.
 */
public class GetVisibleGadgetsInZoneTest extends MapperTest
{
    /** System under test. */
    @Autowired
    private GetVisibleGadgetsInZone sut;

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final long templateId = 3253L;
        CompressGadgetZoneRequest request = new CompressGadgetZoneRequest(templateId, 2, null);
        List<Gadget> results = sut.execute(request);

        assertEquals(3, results.size());

        final long id1 = 3759L;
        final long id2 = 3751L;
        final long id3 = 3781L;

        assertEquals(id1, results.get(0).getId());
        assertEquals(id2, results.get(1).getId());
        assertEquals(id3, results.get(2).getId());
    }
}
