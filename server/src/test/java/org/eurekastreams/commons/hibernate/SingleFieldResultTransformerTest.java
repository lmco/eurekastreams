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
package org.eurekastreams.commons.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test fixture for SingleFieldResultTransformer.
 */
public class SingleFieldResultTransformerTest
{
    /**
     * Test transformTuple with no field name.
     */
    @Test(expected = RuntimeException.class)
    public void testTransformTupleWithNoFieldName()
    {
        SingleFieldResultTransformer sut = new SingleFieldResultTransformer();
        sut.transformTuple(new Object[] {}, new String[] {});
    }

    /**
     * Test transformTuple with a valid field name.
     */
    @Test
    public void testTransformTupleWithValidFieldName()
    {
        SingleFieldResultTransformer sut = new SingleFieldResultTransformer();
        sut.setFieldName("two");

        assertEquals(new Long(4L), sut.transformTuple(new Object[] {
                new Long(3L), new Long(4L) }, new String[] { "one", "two" }));
    }

    /**
     * Test transformTuple with a valid field name but no match.
     */
    @Test
    public void testTransformTupleWithValidFieldNameAndNoMatch()
    {
        SingleFieldResultTransformer sut = new SingleFieldResultTransformer();
        sut.setFieldName("three");

        assertNull(sut.transformTuple(
                new Object[] { new Long(3L), new Long(4L) }, new String[] {
                        "one", "two" }));
    }
}
