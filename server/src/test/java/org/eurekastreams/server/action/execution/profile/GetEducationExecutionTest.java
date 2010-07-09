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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.EnrollmentMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test get education execution.
 *
 */
public class GetEducationExecutionTest extends MapperTest
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
     * mapper.
     */
    @Autowired
    private EnrollmentMapper jpaEnrollmentMapper;

    /**
     * System under test.
     */
    private GetEducationExecution sut;

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Test finding a person's enrollment.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testfindPersonEnrollmentsById()
    {
        sut = new GetEducationExecution(jpaEnrollmentMapper);

        final Long id = Long.valueOf("42");
        final ServiceActionContext currentContext = new ServiceActionContext(id, principalMock);

        List<Enrollment> enrollments = (List<Enrollment>) sut.execute(currentContext);

        assertTrue("No Enrollments found for person with id", enrollments.size() > 0);

        // verify loaded attributes of enrollment
        assertEquals("Incorrect school name returned", "school_name_1", enrollments.get(0).getSchoolName());
        assertEquals("Expected enrollment to contain activity", 1, enrollments.get(0).getActivities().size());
        assertEquals("Expected enrollment to contain area of study", 1, enrollments.get(0).getAreasOfStudy().size());
    }
}
