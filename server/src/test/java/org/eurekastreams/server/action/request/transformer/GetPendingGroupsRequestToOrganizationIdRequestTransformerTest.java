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
import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the transformer.
 */
public class GetPendingGroupsRequestToOrganizationIdRequestTransformerTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private GetPendingGroupsRequestToOrganizationIdRequestTransformer sut;

    /** Fixture: mapper. */
    private GetOrganizationsByShortNames orgMapper = context.mock(GetOrganizationsByShortNames.class);

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetPendingGroupsRequestToOrganizationIdRequestTransformer(orgMapper);
    }

    /**
     * Tests transforming.
     */
    @Test
    public void test()
    {
        ActionContext ctx = new ActionContext()
        {
            public Serializable getParams()
            {
                return new GetPendingGroupsRequest("orgShortname", 0, 1);
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

        context.checking(new Expectations()
        {
            {
                allowing(orgMapper).fetchId("orgShortname");
                will(returnValue(9L));
            }
        });

        Serializable result = sut.transform(ctx);

        context.assertIsSatisfied();

        assertEquals("9", result);
    }

}
