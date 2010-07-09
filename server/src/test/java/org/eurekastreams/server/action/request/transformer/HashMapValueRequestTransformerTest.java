/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.request.transformer;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.junit.Test;

/**
 * Unit test for the SECOND simplest class ever.
 *
 */
public class HashMapValueRequestTransformerTest
{
    /**
     * If I give you key, you give me value.
     */
    @Test
    public void transform()
    {
        HashMap<String, Serializable> hash = new HashMap<String, Serializable>();
        hash.put("key", "value");

        HashMapValueRequestTransformer sut = new HashMapValueRequestTransformer("key");
        ServiceActionContext currentContext = new ServiceActionContext(hash, null);
        assertEquals("value", sut.transform(currentContext));
    }
}
