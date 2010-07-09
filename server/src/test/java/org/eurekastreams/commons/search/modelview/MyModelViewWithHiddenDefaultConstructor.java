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
package org.eurekastreams.commons.search.modelview;

/**
 * Test model view class for unit testing.
 */
public class MyModelViewWithHiddenDefaultConstructor extends ModelView
{
    /**
     * serial uid.
     */
    private static final long serialVersionUID = 3293819522845199377L;

    /**
     * Constructor with two params.
     *
     * @param foo
     *            param 1
     * @param bar
     *            param 2
     */
    public MyModelViewWithHiddenDefaultConstructor(final int foo, final int bar)
    {
    }

    /**
     * Constructor with zero params, private to test our reflection.
     */
    @SuppressWarnings("unused")
    private MyModelViewWithHiddenDefaultConstructor()
    {
    }

    /**
     * Constructor with three params.
     *
     * @param foo
     *            param 1
     * @param bar
     *            param 2
     * @param foobar
     *            param 3
     */
    public MyModelViewWithHiddenDefaultConstructor(final int foo, final int bar, final int foobar)
    {
    }

    /**
     * Get the entity name.
     *
     * @return entity name
     */
    @Override
    protected String getEntityName()
    {
        return "TestModelView";
    }

}
