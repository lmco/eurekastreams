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
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesByOrgRequest;
import org.junit.Test;

/**
 * Tests HasOrganizationIdToOrganizationIdRequestTransformer.
 */
public class HasOrganizationIdToOrganizationIdRequestTransformerTest
{
    /**
     * Tests transforming.
     */
    @SuppressWarnings("serial")
    @Test
    public void test()
    {
        ActionContext ctx = new ActionContext()
        {
            public Serializable getParams()
            {
                return new GetFlaggedActivitiesByOrgRequest(8L, 0, 0);
            }

            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

            }
        };

        Serializable result = new HasOrganizationIdToOrganizationIdRequestTransformer().transform(ctx);
        assertEquals("8", result);
    }
}
