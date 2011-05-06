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
package org.eurekastreams.server.service.actions.strategies;

import java.util.ArrayList;

import junit.framework.Assert;

import org.eurekastreams.server.search.modelview.SharedResourceDTO;
import org.junit.Test;

/**
 * Test fixture for SharedResourceDTOToSharerPeopleIdsTransformer.
 */
public class SharedResourceDTOToSharerPeopleIdsTransformerTest
{
    /**
     * System under test.
     */
    private SharedResourceDTOToSharerPeopleIdsTransformer sut = new SharedResourceDTOToSharerPeopleIdsTransformer();

    /**
     * Test transform.
     */
    @Test
    public void testTransform()
    {
        Assert.assertEquals(0, sut.transform(null).size());

        SharedResourceDTO dto = new SharedResourceDTO();
        dto.setSharerPersonIds(null);
        Assert.assertEquals(0, sut.transform(dto).size());

        ArrayList<Long> peopleIds = new ArrayList<Long>();
        dto.setSharerPersonIds(peopleIds);
        Assert.assertSame(peopleIds, sut.transform(dto));
    }

}
