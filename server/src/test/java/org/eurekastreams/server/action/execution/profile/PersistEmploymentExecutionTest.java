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

package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.action.validation.profile.PersistEmploymentValidation;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for job execution.
 * 
 */
public class PersistEmploymentExecutionTest extends MapperTest
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
     * Job mapper.
     */
    @Autowired
    private FindByIdMapper<Job> findMapper;

    /**
     * job insert mapper.
     */
    @Autowired
    private InsertMapper<Job> insertMapper;

    /**
     * job mapper.
     */
    @Autowired
    private PersonMapper personMapper;

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * {@link IndexEntity}.
     */
    private IndexEntity entityIndexer = context.mock(IndexEntity.class);

    /**
     * subject under test.
     */
    PersistEmploymentExecution sut;

    /**
     * Test form data.
     */
    HashMap<String, Serializable> formdata;

    /**
     * Test setup.
     */
    @Before
    public void setup()
    {
        final int fairlyLong = 1000;

        sut = new PersistEmploymentExecution(personMapper, findMapper, insertMapper, entityIndexer);

        formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEmploymentValidation.COMPANY_NAME_KEY, ValidationTestHelper
                .generateString(Job.MAX_COMPANY_NAME_LENGTH));
        formdata.put(PersistEmploymentValidation.DESCRIPTION_KEY, ValidationTestHelper.generateString(fairlyLong));
        formdata.put(PersistEmploymentValidation.INDUSTRY_KEY, "tech");
        formdata.put(PersistEmploymentValidation.TITLE_KEY, ValidationTestHelper.generateString(Job.MAX_TITLE_LENGTH));
        formdata.put(PersistEmploymentValidation.DATE_KEY, "08/2008;09/2009");
    }

    /**
     * good update input test.
     */
    @Test
    public void testexecutionupdateoneDate()
    {
        final String testId = "fordp";

        formdata.remove(PersistEmploymentValidation.DATE_KEY);
        formdata.put(PersistEmploymentValidation.DATE_KEY, "08/2008");
        formdata.put(PersistEmploymentValidation.JOB_ID_KEY, Long.valueOf("2042"));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(testId));

                oneOf(entityIndexer).execute(with(any(Person.class)));
            }
        });
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * good update input test.
     * 
     * @throws ParseException
     *             should not throw.
     */
    @Test
    public void testexecutionupdate() throws ParseException
    {
        final String testId = "fordp";

        formdata.put(PersistEmploymentValidation.JOB_ID_KEY, Long.valueOf("2042"));

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(testId));

                oneOf(entityIndexer).execute(with(any(Person.class)));
            }
        });
        Job job = (Job) sut.execute(currentContext);
        context.assertIsSatisfied();
        DateFormat df = new SimpleDateFormat("MM/yyyy");

        Date startDate;
        Date endDate;

        startDate = df.parse("08/2008");

        endDate = df.parse("09/2009");

        assertEquals(startDate, job.getDateFrom());

        assertEquals(endDate, job.getDateTo());
    }

    /**
     * good input test.
     */
    @Test
    public void testgoodvalidationInsert()
    {
        final String testId = "fordp";

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(testId));

                oneOf(entityIndexer).execute(with(any(Person.class)));

            }
        });

        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

}
