/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.junit.Test;

/**
 * Tests WrapInListRequestTransformer.
 */
public class WrapInListRequestTransformerTest
{
    /**
     * Tests transforming.
     */
    @Test
    public void test()
    {
        WrapInListRequestTransformer sut = new WrapInListRequestTransformer();
        ServiceActionContext currentContext = new ServiceActionContext(1L, null);

        Serializable result = sut.transform(currentContext);

        assertNotNull(result);

        List list = (List) result;
        assertEquals(1, list.size());
        assertEquals(1L, list.get(0));
    }
}
