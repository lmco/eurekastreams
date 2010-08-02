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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetTermsOfServiceExecution class.
 *
 */
public class GetTermsOfServiceExecutionTest
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
     * The settings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapperMock = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private GetTermsOfServiceExecution sut = new GetTermsOfServiceExecution(settingsMapperMock);

    /**
     * SystemSettings mock.
     */
    private final SystemSettings settings = context.mock(SystemSettings.class);

    /**
     * Terms of service.
     */
    private final String tos = "this is the TOS";

    /**
     * Test performAction method.
     *
     */
    @Test
    public final void textExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(settingsMapperMock).execute(null);
                will(returnValue(settings));

                oneOf(settings).getTermsOfService();
                will(returnValue(tos));
            }
        });

        TermsOfServiceDTO tosDto = sut.execute(null);
        assertEquals(tos, tosDto.getTermsOfService());
        context.assertIsSatisfied();
    }

}
