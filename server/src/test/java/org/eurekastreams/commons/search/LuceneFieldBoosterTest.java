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
package org.eurekastreams.commons.search;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Lucene field booster.
 */
public class LuceneFieldBoosterTest
{
    /**
     * The list of allowable fields to boost.
     */
    private List<String> allowableFields;

    /**
     * System under test.
     */
    private LuceneFieldBooster sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        allowableFields = new ArrayList<String>();
        allowableFields.add("somefield");
        allowableFields.add("anotherfield");
        allowableFields.add("fieldwithweight");
        allowableFields.add("superfield");
        sut = new LuceneFieldBooster();
        sut.setAllowableBoostFields(allowableFields);
    }

    /**
     * Tests setting the weight of a field that doesn't exist.
     */
    @Test
    public final void testSetFieldWeightWithoutField()
    {
        String searchFormat = "somefield:(%1$s)^5";
        String updatedSearchFormat = sut.boostField(searchFormat, "anotherfield", Integer.parseInt("10"));

        assertEquals("somefield:(%1$s)^5 anotherfield:(%1$s)^10", updatedSearchFormat);
    }

    /**
     * Tests setting field weights.
     */
    @Test
    public final void testSetFieldWeight()
    {
        String searchFormat = "somefield:(%1$s) fieldwithweight:(%1$s)^5 superfield:(%1$s)^8797";
        String updatedSearchFormat = sut.boostField(searchFormat, "somefield", Integer.parseInt("10"));
        updatedSearchFormat = sut.boostField(updatedSearchFormat, "fieldwithweight", Integer.parseInt("1"));
        updatedSearchFormat = sut.boostField(updatedSearchFormat, "superfield", Integer.parseInt("10000"));

        assertEquals("somefield:(%1$s)^10 fieldwithweight:(%1$s)^1 superfield:(%1$s)^10000", updatedSearchFormat);
    }

    /**
     * Test boosting a field that's not in the list of allowed fields.
     */
    @Test(expected = InvalidParameterException.class)
    public final void testDisallowedFieldBoost()
    {
        String searchFormat = "somefield:(%1$s) fieldwithweight:(%1$s)^5 superfield:(%1$s)^8797";
        sut.boostField(searchFormat, "notAllowed", Integer.parseInt("10"));
    }
}
