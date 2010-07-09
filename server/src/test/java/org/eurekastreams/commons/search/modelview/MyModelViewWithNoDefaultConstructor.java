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
 * Test class for other unit tests - no default constructor.
 */
public class MyModelViewWithNoDefaultConstructor extends ModelView
{
    /**
     * Serial uid.
     */
    private static final long serialVersionUID = -8506151395693615831L;

    /**
     * Constructor with one param.
     *
     * @param param1
     *            the single constructor param
     */
    public MyModelViewWithNoDefaultConstructor(final int param1)
    {

    }

    /**
     * Get the entity name.
     *
     * @return the entity name
     */
    @Override
    protected String getEntityName()
    {
        return "RandomName";
    }

}
