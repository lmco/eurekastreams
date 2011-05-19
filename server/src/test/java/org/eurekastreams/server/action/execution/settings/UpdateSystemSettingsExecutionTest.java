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
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for UpdateSystemSettingsExecution.
 * 
 */
public class UpdateSystemSettingsExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link UpdateMapper}.
     * 
     */
    private UpdateMapper<SystemSettings> updateMapper = context.mock(UpdateMapper.class);

    /**
     * Updater strategy.
     */
    private UpdaterStrategy updaterStrategy = context.mock(UpdaterStrategy.class);

    /**
     * {@link FindSystemSettings}.
     * 
     */
    private DomainMapper<MapperRequest, SystemSettings> finder = context.mock(DomainMapper.class);

    /**
     * action context.
     */
    private ServiceActionContext actionContext = context.mock(ServiceActionContext.class);

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * System under test.
     */
    private UpdateSystemSettingsExecution sut;

    /**
     * Mocked person for admin 1.
     */
    private final Person admin1 = context.mock(Person.class, "p1");

    /**
     * Mapper to set the system administrators by account ids.
     */
    private DomainMapper<List<String>, Boolean> setSystemAdministratorsMapper = context.mock(DomainMapper.class,
            "setSystemAdministratorsMapper");

    /**
     * Mapper to get the system administrator ids.
     */
    private DomainMapper<Serializable, List<Long>> getSystemAdministratorIdsMapper = context.mock(DomainMapper.class,
            "getSystemAdministratorIdsMapper");

    /**
     * Mapper to get person ids by account ids.
     */
    private DomainMapper<List<String>, List<Long>> peopleIdsByAccountIdsMapper = context.mock(DomainMapper.class,
            "peopleIdsByAccountIdsMapper");

    /**
     * The cache.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Set up before each test.
     */
    @Before
    public void setup()
    {
        sut = new UpdateSystemSettingsExecution(finder, updaterStrategy, updateMapper, setSystemAdministratorsMapper,
                getSystemAdministratorIdsMapper, peopleIdsByAccountIdsMapper, cache);
    }

    /**
     * Test method.
     */
    @Test
    public void testExecute()
    {
        final List<Long> existingSystemAdmins = new ArrayList<Long>();
        existingSystemAdmins.add(5L);

        final HashSet<Person> admins = new HashSet<Person>();
        admins.add(admin1);

        final List<Long> newAdminIds = new ArrayList<Long>();
        newAdminIds.add(7L);

        final ArrayList<String> adminAccountIds = new ArrayList<String>();
        adminAccountIds.add("admin1");

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("admins", admins);

        context.checking(new Expectations()
        {
            {
                oneOf(admin1).getAccountId();
                will(returnValue("admin1"));

                oneOf(peopleIdsByAccountIdsMapper).execute(with(IsEqualInternally.equalInternally(adminAccountIds)));
                will(returnValue(newAdminIds));

                allowing(actionContext).getParams();
                will(returnValue(formData));

                allowing(finder).execute(null);
                will(returnValue(systemSettings));

                oneOf(updaterStrategy).setProperties(systemSettings, formData);

                allowing(updateMapper).execute(null);

                oneOf(setSystemAdministratorsMapper).execute(with(IsEqualInternally.equalInternally(adminAccountIds)));

                oneOf(getSystemAdministratorIdsMapper).execute(null);
                will(returnValue(existingSystemAdmins));

                oneOf(cache).delete(CacheKeys.PERSON_BY_ID + 5);
                oneOf(cache).delete(CacheKeys.PERSON_BY_ID + 7);
                oneOf(cache).delete(CacheKeys.SYSTEM_ADMINISTRATOR_IDS);
            }
        });

        ArrayList<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

        TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerActionContext // 
        = new TaskHandlerActionContext<PrincipalActionContext>(actionContext, userActionRequests);

        Assert.assertEquals(systemSettings, sut.execute(currentTaskHandlerActionContext));
        context.assertIsSatisfied();

        Assert.assertEquals(1, userActionRequests.size());
        Assert.assertEquals("deleteCacheKeysAction", userActionRequests.get(0).getActionKey());
        Assert.assertSame(3, ((HashSet<String>) userActionRequests.get(0).getParams()).size());
        Assert.assertTrue(((HashSet<String>) userActionRequests.get(0).getParams())
                .contains(CacheKeys.PERSON_BY_ID + 5));
        Assert.assertTrue(((HashSet<String>) userActionRequests.get(0).getParams())
                .contains(CacheKeys.PERSON_BY_ID + 7));
        Assert.assertTrue(((HashSet<String>) userActionRequests.get(0).getParams())
                .contains(CacheKeys.SYSTEM_ADMINISTRATOR_IDS));
    }
}
