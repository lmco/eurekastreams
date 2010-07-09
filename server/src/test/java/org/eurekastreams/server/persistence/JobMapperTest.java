/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the Job Mapper interface. The tests contained in here
 * ensure proper interaction with the database.
 */
public class JobMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaJobMapper - system under test.
     */
    @Autowired
    private JobMapper jpaJobMapper;

    /**
     * The PersonMapper is used to look up a person's test data.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * Test inserting a job.
     */
    @Test
    public void testInsert()
    {
        final long personId = 142L;
        Person person = jpaPersonMapper.findById(personId);
        Job job = new Job(person, "company name", "industry", "title", null, null, "desc");
        jpaJobMapper.insert(job);
        long jobId = job.getId();
        jpaJobMapper.getEntityManager().clear();

        jpaJobMapper.flush("2d359911-0977-418a-9490-57e8252b1a42");

        assertTrue("Inserting a Job did not get a positive id.", jpaJobMapper.findById(jobId).getId() > 0);
    }

    /**
     * Test deleting a job.
     */
    @Test(expected = NoResultException.class)
    public void testDelete()
    {
        Job job = null;
        final long jobId = 2042L;
        jpaJobMapper.delete(jobId);

        job = jpaJobMapper.findById(jobId);

        assertTrue("The job was not deleted", job == null);
    }

    /**
     * Test finding a person's job.
     */
    @Test
    public void testfindPersonJobs()
    {
        final String uuid = "2d359911-0977-418a-9490-57e8252b1a42";
        List<Job> jobs = jpaJobMapper.findPersonJobsByOpenSocialId(uuid);

        assertTrue("No Jobs found for person with open" + " social id 2d359911-0977-418a-9490-57e8252b1a42", jobs
                .size() > 0);
    }

    /**
     * Test finding a person's job by id.
     */
    @Test
    public void testfindPersonJobsById()
    {
        final Long id = Long.valueOf("42");
        List<Job> jobs = jpaJobMapper.findPersonJobsById(id);

        assertTrue("No Jobs found for person with id " + id, jobs.size() > 0);
    }

    /**
     * Test looking up companies.
     */
    @Test
    public void testFindCompaniesByPrefix()
    {
        List<String> companies = jpaJobMapper.findCompaniesByPrefix("ex", 5);

        assertEquals(2, companies.size());

        assertTrue(companies.get(0).equals("Example"));
        assertTrue(companies.get(1).equals("example2"));
    }

    /**
     * Test looking up companies and limiting the number of results.
     */
    @Test
    public void testFindCompaniesByPrefixWithLimit()
    {
        List<String> companies = jpaJobMapper.findCompaniesByPrefix("ex", 1);

        assertEquals(1, companies.size());

        assertTrue(companies.get(0).equals("Example"));
    }
}
