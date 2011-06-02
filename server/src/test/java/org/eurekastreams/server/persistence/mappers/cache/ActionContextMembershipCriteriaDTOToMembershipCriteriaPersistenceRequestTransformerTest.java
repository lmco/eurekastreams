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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer.
 * 
 */
public class ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformerTest
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
     * System under test.
     */
    private ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer sut = //
    new ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer();

    /**
     * {@link ActionContext}.
     */
    private ActionContext ac = context.mock(ActionContext.class);

    /**
     * Criteria string used in test.
     */
    private String criteria = "criteria";

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final MembershipCriteriaDTO mcdto = new MembershipCriteriaDTO();
        mcdto.setCriteria(criteria);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(mcdto));

            }
        });

        PersistenceRequest<MembershipCriteria> result = sut.transform(ac);

        assertEquals(criteria, result.getDomainEnity().getCriteria());

        context.assertIsSatisfied();
    }

}
