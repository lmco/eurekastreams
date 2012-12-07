/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;

/**
 * Tests IsGroupVisibleInSearchClassBridge.
 */
public class IsGroupVisibleInSearchClassBridgeTest
{
    /**
     * System under test.
     */
    private final IsGroupVisibleInSearchClassBridge sut = new IsGroupVisibleInSearchClassBridge();

    /**
     * Test objectToString when passed null input.
     */
    @Test
    public void testObjectToStringNullInput()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString when passed an invalid type.
     */
    @Test
    public void testObjectToStringWrongType()
    {
        assertNull(sut.objectToString(new Person()));
    }

    /**
     * Test objectToString when passed a pending group.
     */
    @Test
    public void testObjectToStringPending()
    {
        DomainGroup group = new DomainGroup();
        group.setPending(true);
        assertEquals("f", sut.objectToString(group));
    }

    /**
     * Test objectToString when passed a normal group.
     */
    @Test
    public void testObjectToStringNormal()
    {
        DomainGroup group = new DomainGroup();
        group.setPending(false);
        assertEquals("t", sut.objectToString(group));
    }
}
