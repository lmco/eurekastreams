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
import org.eurekastreams.server.domain.Job;
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
public class PersistEmploymentValidationTest
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
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * subject under test.
     */
    private PersistEmploymentValidation sut;

    /**
     * JobMapper.
     */
    private FindByIdMapper<Job> jobMapper = context.mock(FindByIdMapper.class);

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {

        sut = new PersistEmploymentValidation(jobMapper);
    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidation()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "10/2008");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Job.MAX_DESCRIPTION_LENGTH));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, ValidationTestHelper.generateString(4));
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper.generateString(Job.MAX_TITLE_LENGTH));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        sut.validate(currentContext);
    }

    /**
     * unhandle exception because it should never happen.
     */
    @Test(expected = Exception.class)
    public void testnokeyvalidation()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put("notBIO", "this is my biography it is fun it is neat.");

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        sut.validate(currentContext);
    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidation2()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "10/2008;10/2009");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper.generateString(9));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, ValidationTestHelper.generateString(5));
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper.generateString(Job.MAX_TITLE_LENGTH));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        sut.validate(currentContext);
    }

    /**
     * good input test.
     */
    @Test
    public void testbadvalidation()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH + 1));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "not a valid date");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(Job.MAX_DESCRIPTION_LENGTH + 1));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, "");
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper
                .generateString(Job.MAX_TITLE_LENGTH + 1));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            assertEquals(5, e.getErrors().size());
        }
    }

    /**
     * good input test.
     */
    @Test(expected = ValidationException.class)
    public void testbadInsertValidation()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "10/2008");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper.generateString(9));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, ValidationTestHelper.generateString(4));
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper.generateString(Job.MAX_TITLE_LENGTH));
        formdata.put(PersistEmploymentValidation.JOB_ID_KEY, 2L);

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(null));
            }
        });

        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            context.assertIsSatisfied();
            assertEquals(1, e.getErrors().size());
            throw e;
        }
    }

    /**
     * good input test.
     */
    @Test
    public void testGoodInsertValidation()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "10/2008");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper.generateString(9));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, ValidationTestHelper.generateString(4));
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper.generateString(Job.MAX_TITLE_LENGTH));
        formdata.put(PersistEmploymentValidation.JOB_ID_KEY, 2L);

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        final Job job = context.mock(Job.class);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(job));
            }
        });

        sut.validate(currentContext);
        context.assertIsSatisfied();

    }

    /**
     * good input test.
     */
    @Test
    public void testbadvalidation2()
    {
        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, "");
        formdata.put(PersistEmploymentValidation.DATE_KEY, "");
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, "");
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, "");
        formdata.put(PersistEmploymentValidation.TITLE_KEY, "");

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        try
        {
            sut.validate(currentContext);

        }
        catch (ValidationException e)
        {
            assertEquals(4, e.getErrors().size());
        }
    }

}
