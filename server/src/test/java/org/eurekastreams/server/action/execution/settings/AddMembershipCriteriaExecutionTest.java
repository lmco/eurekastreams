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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for AddMembershipCriteriaExecution.
 * 
 */
public class AddMembershipCriteriaExecutionTest
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
     * The system settings update mapper.
     */
    private UpdateMapper<SystemSettings> updateMapper = context.mock(UpdateMapper.class);

    /**
     * The system settings finder mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> finder = context.mock(DomainMapper.class);

    /**
     * The strategy used to set the system settings.
     */
    private UpdaterStrategy updater = context.mock(UpdaterStrategy.class);

    /**
     * The criteria insert mapper.
     */
    private InsertMapper<MembershipCriteria> criteriaMapper = context.mock(InsertMapper.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link MembershipCriteria}.
     */
    private MembershipCriteria mc = context.mock(MembershipCriteria.class);

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings ss = context.mock(SystemSettings.class);

    /**
     * System under test.
     */
    private AddMembershipCriteriaExecution sut = new AddMembershipCriteriaExecution(finder, updater, updateMapper,
            criteriaMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final List<MembershipCriteria> criteria = new ArrayList<MembershipCriteria>();

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(mc));

                allowing(mc).getCriteria();
                will(returnValue("criteria"));

                oneOf(criteriaMapper).execute(with(any(PersistenceRequest.class)));
                oneOf(criteriaMapper).flush();

                oneOf(finder).execute(null);
                will(returnValue(ss));

                oneOf(ss).getMembershipCriteria();
                will(returnValue(criteria));

                oneOf(ss).setMembershipCriteria(criteria);

                oneOf(updater).setProperties(ss, new HashMap<String, Serializable>());

                oneOf(updateMapper).execute(null);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
