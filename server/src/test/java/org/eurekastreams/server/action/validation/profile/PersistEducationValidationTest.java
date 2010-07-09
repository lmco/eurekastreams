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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for bio update validation.
 *
 */
@SuppressWarnings("unchecked")
public class PersistEducationValidationTest
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
     * mapper to see if enrollment exist.
     */
    private FindByIdMapper<Enrollment> findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * subject under test.
     */
    private PersistEducationValidation sut;

    /**
     * Test form data.
     */
    private HashMap<String, Serializable> formdata;

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new PersistEducationValidation(findByIdMapper);

        formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEducationValidation.ADDITIONAL_DETAILS_KEY,
                ValidationTestHelper.generateString(9));
        formdata.put(PersistEducationValidation.GRADDATE_KEY, "2008");
        formdata.put(PersistEducationValidation.ACTIVITIES_KEY, "act1, act 2, act 3; act4");
        formdata.put(PersistEducationValidation.AREAS_OF_STUDY_KEY, "area of study one, area of study 2, aos 3;aos4");
        formdata.put(PersistEducationValidation.DEGREE_KEY, "UNDERGRAD");
        formdata.put(PersistEducationValidation.SCHOOL_NAME_KEY, "UNDERGRAD");
    }

    /**
     * good update input test.
     */
    @Test
    public void testGoodvalidationupdate()
    {

        formdata.put(PersistEducationValidation.ENROLLMENT_ID_KEY, 1L);

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        final Enrollment enrollment = context.mock(Enrollment.class);
        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(enrollment));
            }
        });

        sut.validate(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * bad update input test.
     */
    @Test(expected = ValidationException.class)
    public void testBadvalidationupdate()
    {

        formdata.put(PersistEducationValidation.ENROLLMENT_ID_KEY, 1L);

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(null));
            }
        });
        sut.validate(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidationInsert()
    {
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        sut.validate(currentContext);
    }

    /**
     * bad test if value are invalid.
     */
    @Test(expected = ValidationException.class)
    public void testBadvalidationInsert()
    {
        formdata.clear();
        formdata.put(PersistEducationValidation.ADDITIONAL_DETAILS_KEY, "");
        formdata.put(PersistEducationValidation.GRADDATE_KEY, "sda/2008");
        formdata.put(PersistEducationValidation.AREAS_OF_STUDY_KEY, "");
        formdata.put(PersistEducationValidation.DEGREE_KEY, "");
        formdata.put(PersistEducationValidation.SCHOOL_NAME_KEY, "");

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        try
        {
            sut.validate(currentContext);
        }
        catch (ValidationException ve)
        {
            assertEquals(4, ve.getErrors().size());
            throw ve;
        }
    }
}
