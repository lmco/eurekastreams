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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.eurekastreams.commons.reflection.ReflectiveInstantiator;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.commons.search.modelview.MyModelView;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ProjectionToModelViewTransformer.
 */
public class ProjectionToModelViewTransformerTest
{
    /**
     * System under test.
     */
    private ProjectionToModelViewTransformer sut;

    /**
     * Search score.
     */
    private final float searchScore = 0.89F;

    /**
     * Search description.
     */
    private Explanation explanation = new ComplexExplanation(true, searchScore, "Some Description");

    /**
     * Setup the sut.
     */
    @Before
    public void setup()
    {
        Map<Class< ? >, Class< ? >> modelToViewMap = new HashMap<Class< ? >, Class< ? >>();
        modelToViewMap.put(String.class, MyModelView.class);

        sut = new ProjectionToModelViewTransformer(modelToViewMap, new ReflectiveInstantiator());
    }

    /**
     * Test transformList() method, which does nothing but return the input list.
     */
    @Test
    public void testTransformList()
    {
        List<ModelView> list = new ArrayList<ModelView>();
        assertSame(list, sut.transformList(list));
    }

    /**
     * Unit test the getMapFromTuplesAndAliases() method.
     */
    @Test
    public void testGetMapFromTuplesAndAliases()
    {
        String[] aliases = new String[] { "foo", "bar" };
        Object[] tuple = new Object[] { 3, 8 };

        Map<String, Object> map = sut.getMapFromTuplesAndAliases(tuple, aliases);

        assertEquals(3, map.get("foo"));
        assertEquals(8, map.get("bar"));
    }

    /**
     * Test the transformTuple() method with unhandled type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTransformTupleWithUnhandledType()
    {
        String[] aliases = new String[] { "_hibernate_class", "foo", "bar" };
        Object[] tuple = new Object[] { Integer.class, "Hi there!", "Hello right back at ya, buddy!" };

        sut.transformTuple(tuple, aliases);
    }

    /**
     * Test the transformTuple() method.
     */
    @Test
    public void testTransformTuple()
    {
        String[] aliases = new String[] { "_hibernate_class", "foo", "bar" };
        Object[] tuple = new Object[] { String.class, "Hi there!", "Hello right back at ya, buddy!" };

        MyModelView myModelView = (MyModelView) sut.transformTuple(tuple, aliases);
        assertEquals("Hi there!", myModelView.getFoo());
        assertEquals("Hello right back at ya, buddy!", myModelView.getBar());
    }

    /**
     * Test the transformTuple() method with included search explanation.
     */
    @Test
    public void testTransformTupleWithSearchExplanation()
    {
        String[] aliases = new String[] { "__HSearch_Explanation", "_hibernate_class", "foo", "bar" };
        Object[] tuple = new Object[] { explanation, String.class, "Hi there!", "Hello right back at ya, buddy!" };

        MyModelView myModelView = (MyModelView) sut.transformTuple(tuple, aliases);
        assertEquals("Hi there!", myModelView.getFoo());
        assertEquals("Hello right back at ya, buddy!", myModelView.getBar());
        assertEquals(explanation.toString(), myModelView.getSearchIndexExplanationString());
    }
}
