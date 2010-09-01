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
package org.eurekastreams.server.service.security.userdetails;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for TermsOfServiceAcceptanceStrategyImpl class.
 *
 */
public class TermsOfServiceAcceptanceStrategyImplTest
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
     * The system settings mapper mock.
     */
    private final DomainMapper<MapperRequest, SystemSettings> systemSettingsDAO = context.mock(DomainMapper.class);

    /**
     * SystemSettings mock.
     */
    private final SystemSettings systemSettings = context.mock(SystemSettings.class);

    /**
     * System under test.
     */
    private TermsOfServiceAcceptanceStrategyImpl sut;

    /**
     * The number of milliseconds in a day.
     */
    private static final long MILLISECONDS_IN_A_DAY = 86400000L;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new TermsOfServiceAcceptanceStrategyImpl(systemSettingsDAO);
    }

    /**
     * Test the isValidTermsOfServiceAcceptanceDate method will null ToS.
     */
    @Test
    public void testIsValidTermsOfServiceAcceptanceDateNoToS()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getTermsOfService();
                will(returnValue(null));
            }
        });

        assertTrue(sut.isValidTermsOfServiceAcceptanceDate(new Date()));
        context.assertIsSatisfied();
    }

    /**
     * Test the isValidTermsOfServiceAcceptanceDate method with null Input date.
     */
    @Test
    public void testIsValidTermsOfServiceAcceptanceDateNullInputDate()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getTermsOfService();
                will(returnValue("ToS"));
            }
        });

        assertTrue(!sut.isValidTermsOfServiceAcceptanceDate(null));
        context.assertIsSatisfied();
    }

    /**
     * Test the isValidTermsOfServiceAcceptanceDate method with date check passing.
     */
    @Test
    public void testIsValidTermsOfServiceAcceptanceDateCheckDatePass()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getTermsOfService();
                will(returnValue("ToS"));

                oneOf(systemSettings).getTosPromptInterval();
                will(returnValue(1));
            }
        });

        assertTrue(sut.isValidTermsOfServiceAcceptanceDate(new Date()));
        context.assertIsSatisfied();
    }

    /**
     * Test the isValidTermsOfServiceAcceptanceDate method with date check failing.
     */
    @Test
    public void testIsValidTermsOfServiceAcceptanceDateCheckDateFail()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getTermsOfService();
                will(returnValue("ToS"));

                oneOf(systemSettings).getTosPromptInterval();
                will(returnValue(1));
            }
        });

        // Create date representing 2 days ago.
        Date lastAcceptedDate = new Date(Calendar.getInstance().getTimeInMillis() - (2 * MILLISECONDS_IN_A_DAY));

        assertTrue(!sut.isValidTermsOfServiceAcceptanceDate(lastAcceptedDate));
        context.assertIsSatisfied();
    }

}
