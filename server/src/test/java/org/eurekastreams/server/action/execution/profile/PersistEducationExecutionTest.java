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
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.action.validation.profile.PersistEducationValidation;
import org.eurekastreams.server.domain.Enrollment;
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
 * Test for education execution.
 * 
 */
public class PersistEducationExecutionTest extends MapperTest
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
     * Enrollment mapper.
     */
    @Autowired
    private FindByIdMapper<Enrollment> enrollmentFindMapper;

    /**
     * Enrollment mapper.
     */
    @Autowired
    private InsertMapper<Enrollment> enrollmentInsertMapper;

    /**
     * Person mapper.
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
    PersistEducationExecution sut;

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

        sut = new PersistEducationExecution(personMapper, enrollmentFindMapper, enrollmentInsertMapper, entityIndexer);

        formdata = new HashMap<String, Serializable>();
        formdata.put(PersistEducationValidation.ADDITIONAL_DETAILS_KEY, // \n
                ValidationTestHelper.generateString(fairlyLong));
        formdata.put(PersistEducationValidation.GRADDATE_KEY, "2008");
        formdata.put(PersistEducationValidation.AREAS_OF_STUDY_KEY, "area of study one, area of study 2, aos 3;aos4");
        formdata.put(PersistEducationValidation.DEGREE_KEY, "UNDERGRAD");
        formdata.put(PersistEducationValidation.SCHOOL_NAME_KEY, "UNDERGRAD");
    }

    /**
     * good update input test.
     */
    @Test
    public void testexecutionupdate()
    {
        final String testId = "fordp";

        formdata.put(PersistEducationValidation.ENROLLMENT_ID_KEY, Long.valueOf("2042"));

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
     * good input test.
     */
    @Test
    public void testgoodvalidationInsert()
    {
        final String testId = "fordp";
        formdata.put(PersistEducationValidation.ACTIVITIES_KEY, "act1, act 2, act 3; act4");
        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(testId));

                oneOf(entityIndexer).execute(with(any(Person.class)));

            }
        });

        Enrollment output = (Enrollment) sut.execute(currentContext);
        context.assertIsSatisfied();
        assertEquals(3, output.getActivities().size());
        assertEquals("act 2", output.getActivities().get(1).getName());
    }

}
