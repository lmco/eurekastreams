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
package org.eurekastreams.server.action.validation.stream;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetActivitiesByCompositeStreamIdValidation}.
 *
 */
public class GetActivitiesByCompositeStreamIdValidationTest
{
    /**
     * System under test.
     */
    private GetActivitiesByCompositeStreamIdValidation sut;

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
     * Mocked instance of the {@link CompositeStreamActivityIdsMapper}.
     */
    private CompositeStreamActivityIdsMapper idsMapperMock = context.mock(CompositeStreamActivityIdsMapper.class);

    /**
     * Mocked instance of the {@link PrincipalActionContext}.
     */
    private PrincipalActionContext contextMock = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the {@link Principal} object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetActivitiesByCompositeStreamIdValidation(idsMapperMock);
    }

    /**
     * Test successful validation.
     */
    @Test
    public void testSuccessfulValidation()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(idsMapperMock).execute(with(any(Long.class)), with(any(Long.class)));
            }
        });

        sut.validate(new ServiceActionContext(new GetActivitiesByCompositeStreamRequest(0L, 0), new DefaultPrincipal(
                "", "", 0L)));

        context.assertIsSatisfied();
    }

    /**
     * Test failing validation.
     */
    @Test(expected = ValidationException.class)
    public void testFailureValidation()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(idsMapperMock).execute(with(any(Long.class)), with(any(Long.class)));
                will(throwException(new RuntimeException()));
            }
        });

        sut.validate(new ServiceActionContext(new GetActivitiesByCompositeStreamRequest(0L, 0), new DefaultPrincipal(
                "", "", 0L)));

        context.assertIsSatisfied();
    }
}
