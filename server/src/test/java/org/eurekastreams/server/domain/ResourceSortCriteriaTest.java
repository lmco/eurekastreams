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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test fixture for ResourceSortCriteria.
 */
public class ResourceSortCriteriaTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor()
    {
        // test the empty constructor just for coverage
        ResourceSortCriteria sut = new ResourceSortCriteria();

        // now test the constructor with the critiera
        List<ResourceSortCriterion> criteriaList = new ArrayList<ResourceSortCriterion>();
        sut = new ResourceSortCriteria(criteriaList);
        assertSame(criteriaList, sut.getCriteria());
    }

    /**
     * Test the properties.
     */
    @Test
    public void testProperties()
    {
        List<ResourceSortCriterion> criteriaList1 = new ArrayList<ResourceSortCriterion>();
        ResourceSortCriteria sut = new ResourceSortCriteria(criteriaList1);
        assertSame(criteriaList1, sut.getCriteria());

        List<ResourceSortCriterion> criteriaList2 = new ArrayList<ResourceSortCriterion>();
        sut.setCriteria(criteriaList2);
        assertSame(criteriaList2, sut.getCriteria());
    }
}
