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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.junit.Test;

/**
 * Test fixture for StreamScope.
 */
public class StreamScopeTest
{
    /**
     * Test the constructor.
     */
    public void testLoadedConstructor()
    {
        StreamScope scope = new StreamScope(ScopeType.GROUP, "grouPKey", 5L);
        assertEquals(ScopeType.GROUP, scope.getScopeType());
        assertEquals("groupkey", scope.getUniqueKey());
        assertEquals(5L, scope.getId());
    }

    /**
     * Test the constructor.
     */
    public void testDisplayNameConstructor()
    {
        StreamScope scope = new StreamScope("displayName", ScopeType.GROUP, "GROUpKEY", 5L);
        assertEquals(ScopeType.GROUP, scope.getScopeType());
        assertEquals("groupkey", scope.getUniqueKey());
        assertEquals("displayName", scope.getDisplayName());
        assertEquals(5L, scope.getId());
    }

    /**
     * Test the properties.
     */
    @Test
    public void testProperties()
    {
        StreamScope scope = new StreamScope();
        scope.setScopeType(ScopeType.GROUP);
        scope.setUniqueKey("UNIQUEKeY");

        assertEquals("uniquekey", scope.getUniqueKey());
        assertEquals(ScopeType.GROUP, scope.getScopeType());

        scope.setScopeType(ScopeType.GROUP);
        assertEquals(ScopeType.GROUP, scope.getScopeType());

        scope.setScopeType(ScopeType.PERSON);
        assertEquals(ScopeType.PERSON, scope.getScopeType());

        scope.setScopeType(ScopeType.ALL);
        assertEquals(ScopeType.ALL, scope.getScopeType());

        scope.setScopeType(ScopeType.PERSONS_FOLLOWED_STREAMS);
        assertEquals(ScopeType.PERSONS_FOLLOWED_STREAMS, scope.getScopeType());

        scope.setDisplayName("displayName");
        assertEquals("displayName", scope.getDisplayName());

    }
}
