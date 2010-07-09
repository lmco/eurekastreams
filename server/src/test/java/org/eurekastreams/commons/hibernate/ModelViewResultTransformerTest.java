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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.junit.Test;

/**
 * Test fixture for ModelViewResultTransformer.
 */
public class ModelViewResultTransformerTest
{
    /**
     * System under test.
     */
    private ModelViewResultTransformer<MyModelView> sut = new ModelViewResultTransformer<MyModelView>(
            new MyModelViewFactory());

    /**
     * Test transformList().
     */
    @Test
    public void testTransformList()
    {
        List<MyModelView> list = new ArrayList<MyModelView>();
        assertSame(list, sut.transformList(list));
    }

    /**
     * Test transformTuple().
     */
    public void testTransformTuple()
    {
        MyModelView myModelView = sut.transformTuple(new Object[] { "bar" },
                new String[] { "foo" });
        assertEquals("bar", myModelView.getFoo());
    }

    /**
     * Test model view.
     */
    public class MyModelView extends ModelView
    {
        /**
         * The serial version uid.
         */
        private static final long serialVersionUID = 5914678227805929423L;

        /**
         * Return the name of the model.
         *
         * @return the name of the entity
         */
        @Override
        protected String getEntityName()
        {
            return "Stanley";
        }

        /**
         * Stores foo.
         */
        private String foo;

        /**
         * Get Foo.
         *
         * @return foo!
         */
        public String getFoo()
        {
            return foo;
        }

        /**
         * Load any properties that may have been returned from the search
         * query.
         *
         * @param properties
         *            a Map of properties returned by the search - injest any
         *            that this class handles.
         */
        @Override
        public void loadProperties(final Map<String, Object> properties)
        {
            if (properties.containsKey("foo"))
            {
                foo = (String) properties.get("foo");
            }
        }
    }

    /**
     * Test factory to build MyModelView.
     */
    public class MyModelViewFactory extends ModelViewFactory<MyModelView>
    {
        /**
         * Return a new instance of MyModelView.
         *
         * @return a new instance of MyModelView
         */
        @Override
        public MyModelView buildModelView()
        {
            return new MyModelView();
        }

    }
}
