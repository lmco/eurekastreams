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

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.persistence.JobMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test the execution of the get employment action.
 *
 */
public class GetEmploymentExecutionTest extends MapperTest
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
     * Job Mapper.
     */
    @Autowired
    private JobMapper jpaJobMapper;

    /**
     * system under test.
     */
    private GetEmploymentExecution sut;

    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Test finding a person's enrollment.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testfindPersonJobsById()
    {
        sut = new GetEmploymentExecution(jpaJobMapper);

        final Long id = Long.valueOf("42");
        final ServiceActionContext currentContext = new ServiceActionContext(id, principalMock);

        LinkedList<Job> jobs = (LinkedList<Job>) sut.execute(currentContext);

        assertTrue("No Jobs found for person with id " + id, jobs.size() > 0);
    }
}
