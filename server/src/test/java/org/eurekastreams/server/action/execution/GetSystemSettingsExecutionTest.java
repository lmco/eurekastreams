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
package org.eurekastreams.server.action.execution;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetSystemSettingsExecution class.
 * 
 */
public class GetSystemSettingsExecutionTest
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
     * Subject under test.
     */
    private GetSystemSettingsExecution sut = null;

    /**
     * Mocked mapper for the action to look up the SystemSettings.
     */
    private DomainMapper<MapperRequest, SystemSettings> systemSettingDAO = context.mock(DomainMapper.class);

    /**
     * Mocked mapper for the SystemSettings.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * Mapper to get the system administrator ids.
     */
    private DomainMapper<Serializable, List<PersonModelView>> systemAdminsMapper = context.mock(DomainMapper.class,
            "systemAdminsMapper");

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetSystemSettingsExecution(systemSettingDAO, systemAdminsMapper);
    }

    /**
     * Check that the action correctly returns the system settings, without admins.
     */
    @Test
    public final void testExecuteWithoutSystemAdmins()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingDAO).execute(null);
                will(returnValue(systemSettings));

                allowing(actionContext).getParams();
                will(returnValue(null));
            }
        });

        assertEquals(systemSettings, sut.execute(actionContext));
        context.assertIsSatisfied();
    }

    /**
     * Check that the action correctly returns the system settings, with admins.
     */
    @Test
    public final void testExecuteWithSystemAdmins()
    {
        PersonModelView admin = new PersonModelView();
        final List<PersonModelView> adminsList = new ArrayList<PersonModelView>();
        adminsList.add(admin);

        final Set<PersonModelView> adminsSet = new HashSet<PersonModelView>();
        adminsSet.add(admin);

        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).setSystemAdministrators(with(IsEqualInternally.equalInternally(adminsSet)));

                allowing(systemSettings).getSystemAdministrators();
                will(returnValue(adminsSet));

                allowing(actionContext).getParams();
                will(returnValue(new Boolean(true)));

                oneOf(systemAdminsMapper).execute(null);
                will(returnValue(adminsList));
            }
        });

        assertEquals(systemSettings, sut.execute(actionContext));
        assertEquals(1, systemSettings.getSystemAdministrators().size());
        assertTrue(systemSettings.getSystemAdministrators().contains(admin));
        context.assertIsSatisfied();
    }
}
