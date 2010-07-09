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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.GetPendingDomainGroupsForOrg;
import org.eurekastreams.server.persistence.mappers.requests.GetPendingDomainGroupsForOrgRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetPendingGroupsExecutionStrategy.
 */
public class GetPendingGroupsExecutionStrategyTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked mapper.
     */
    private final GetPendingDomainGroupsForOrg actionMapper = context.mock(GetPendingDomainGroupsForOrg.class);

    /**
     * System under test.
     */
    private GetPendingGroupsExecutionStrategy sut = new GetPendingGroupsExecutionStrategy(actionMapper);

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final String orgShortName = "abcdefg";
        final Integer startIndex = 324;
        final Integer endIndex = 898;

        final PagedSet<DomainGroupModelView> expectedReturnValue = new PagedSet<DomainGroupModelView>();

        context.checking(new Expectations()
        {
            {
                oneOf(actionMapper).execute(
                        with(IsEqualInternally.equalInternally(new GetPendingDomainGroupsForOrgRequest(orgShortName,
                                startIndex, endIndex))));
                will(returnValue(expectedReturnValue));
            }
        });

        Serializable returnValue = sut.execute(new ActionContext()
        {
            /**
             * Serial version uid.
             */
            private static final long serialVersionUID = -2804552268718221598L;

            /**
             * Get the request params.
             *
             * @return the request param
             */
            @Override
            public GetPendingGroupsRequest getParams()
            {
                return new GetPendingGroupsRequest(orgShortName, startIndex, endIndex);
            }

            /**
             * Get the map.
             *
             * @return the map.
             */
            @Override
            public Map<String, Object> getState()
            {
                return new HashMap<String, Object>();
            }
        });

        assertSame(expectedReturnValue, returnValue);
    }
}
