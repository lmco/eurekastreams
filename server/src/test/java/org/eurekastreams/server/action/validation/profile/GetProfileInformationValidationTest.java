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
package org.eurekastreams.server.action.validation.profile;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test validation for get a person information.
 * 
 */
@SuppressWarnings("unchecked")
public class GetProfileInformationValidationTest
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
     * mapper to look up person.
     */
    private FindByIdMapper<Person> pMapper = context.mock(FindByIdMapper.class);

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * subject under test.
     */
    GetProfileInformationValidation sut;

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new GetProfileInformationValidation(pMapper);
    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidation()
    {
        Long id = 1L;

        final Person personMock = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                oneOf(pMapper).execute(with(equalInternally(new FindByIdRequest("Person", 1L))));
                will(returnValue(personMock));
            }
        });

        final ServiceActionContext currentContext = new ServiceActionContext(id, principalMock);

        sut.validate(currentContext);
    }

    /**
     * no such person.
     */
    @Test(expected = ValidationException.class)
    public void testNoSuchPersonvalidation()
    {
        Long id = 1L;

        context.checking(new Expectations()
        {
            {
                oneOf(pMapper).execute(with(equalInternally(new FindByIdRequest("Person", 1L))));
                will(returnValue(null));
            }
        });

        final ServiceActionContext currentContext = new ServiceActionContext(id, principalMock);

        sut.validate(currentContext);
    }

    /**
     * bad param.
     */
    @Test(expected = ValidationException.class)
    public void testBadParamvalidation()
    {
        final ServiceActionContext currentContext = new ServiceActionContext("Bad param", principalMock);

        sut.validate(currentContext);
    }
}
