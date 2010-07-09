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
package org.eurekastreams.server.service.actions.strategies.ldap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for DirContextAdapterMapper class.
 *
 */
public class DirContextAdapterMapperTest
{

    /**
     * Best test ever, ensure that object returned is same as object passed in. Woot.
     */
    @Test
    public void testMapFromContext()
    {
        Object foo = new Object();
        DirContextAdapterMapper sut = new DirContextAdapterMapper();
        assertEquals(foo, sut.mapFromContext(foo));
    }

}
