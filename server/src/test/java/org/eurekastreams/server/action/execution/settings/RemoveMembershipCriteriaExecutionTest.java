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
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for RemoveMembershipCriteriaExecution class.
 *
 */
public class RemoveMembershipCriteriaExecutionTest
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
     * The finder to be used by the action.
     */
    private DomainMapper<MapperRequest, SystemSettings> findSystemSettings = context.mock(DomainMapper.class);

    /**
     * The object update strategy to be used by the action.
     */
    private UpdaterStrategy updaterStrategy = context.mock(UpdaterStrategy.class);

    /**
     * The update mapper to be used by the action.
     */
    private UpdateMapper<SystemSettings> updateSystemSettings = context.mock(UpdateMapper.class);

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * The subject under test.
     */
    private RemoveMembershipCriteriaExecution sut;

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new RemoveMembershipCriteriaExecution(findSystemSettings, updaterStrategy, updateSystemSettings);
    }

    /**
     * Test action.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testExecute() throws Exception
    {
        final MembershipCriteria criterion = new MembershipCriteria();
        criterion.setCriteria("testCriterion2");

        final List<MembershipCriteria> criterias = new ArrayList<MembershipCriteria>(1);
        criterias.add(criterion);

        final Map<String, Serializable> emptyMap = new HashMap<String, Serializable>(0);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(criterion));

                allowing(findSystemSettings).execute(null);
                will(returnValue(systemSettings));

                allowing(systemSettings).getMembershipCriteria();
                will(returnValue(criterias));

                allowing(systemSettings).setMembershipCriteria(with(any(List.class)));

                allowing(updaterStrategy).setProperties(systemSettings, emptyMap);

                allowing(updateSystemSettings).execute(null);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
