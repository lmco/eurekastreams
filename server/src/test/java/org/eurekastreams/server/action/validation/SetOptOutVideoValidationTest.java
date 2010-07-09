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
package org.eurekastreams.server.action.validation;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.TutorialVideo;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing SetOptoutVideoValidation class.
 * 
 */
@SuppressWarnings("unchecked")
public class SetOptOutVideoValidationTest
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
    private SetOptOutVideoValidation sut;

    /**
     * FindByIdMappeer Mock.
     */
   private  final FindByIdMapper<TutorialVideo> tutorialVideoMapperMock = context.mock(FindByIdMapper.class);

    /**
     * TutorialVideo Mock.
     */
    private final TutorialVideo tutorialVideoMock = context.mock(TutorialVideo.class);

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new SetOptOutVideoValidation(tutorialVideoMapperMock);
    }

    /**
     * Test to validate video validation.
     */
    @Test
    public void testValidate()
    {
        final ServiceActionContext currentContext = new ServiceActionContext(1L, principalMock);

        context.checking(new Expectations()
        {
            {
                oneOf(tutorialVideoMapperMock).execute(with(equalInternally(new FindByIdRequest("TutorialVideo", 1L))));
                will(returnValue(tutorialVideoMock));
            }
        });
        sut.validate(currentContext);
        context.assertIsSatisfied();

    }

    /**
     * Test to validate video validation.
     */
    @Test(expected = ValidationException.class)
    public void testNotValid()
    {
        final ServiceActionContext currentContext = new ServiceActionContext(1L, principalMock);

        context.checking(new Expectations()
        {
            {
                oneOf(tutorialVideoMapperMock).execute(with(equalInternally(new FindByIdRequest("TutorialVideo", 1L))));
                will(returnValue(null));
            }
        });
        sut.validate(currentContext);
        context.assertIsSatisfied();

    }

}
