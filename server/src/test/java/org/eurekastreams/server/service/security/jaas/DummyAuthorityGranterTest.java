/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.jaas;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

/**
 * Tests for DummyAuthorityGranter class.
 *
 */
public class DummyAuthorityGranterTest
{
    /**
     * Verify grant() returns null.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGrant()
    {
        DummyAuthorityGranter sut = new DummyAuthorityGranter();
        Set results = sut.grant(null);
        assertNull(results);
    }

}
