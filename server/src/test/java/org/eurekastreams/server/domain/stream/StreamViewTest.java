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
import static org.junit.Assert.assertSame;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Test fixture for StreamView.
 */
public class StreamViewTest
{
    /**
     * The scopes to use.
     */
    private Set<StreamScope> scopes = new HashSet<StreamScope>();

    /**
     * Test the properties.
     */
    @Test
    public void testProperties()
    {
        StreamView view = new StreamView();
        view.setIncludedScopes(scopes);
        view.setName("test");
        view.setType(StreamView.Type.EVERYONE);

        assertSame(scopes, view.getIncludedScopes());
        assertSame("test", view.getName());
        assertEquals(StreamView.Type.EVERYONE, view.getType());
    }
}
