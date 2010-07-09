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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the Enrollment Mapper interface. The tests contained
 * in here ensure proper interaction with the database.
 */
public class EnrollmentMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaEnrollmentMapper - system under test.
     */
    @Autowired
    private EnrollmentMapper jpaEnrollmentMapper;

    /**
     * The person mapper.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * Test inserting a enrollment.
     */
    @Test
    public void testInsert()
    {
        final long personId = 142L;
        Person person = jpaPersonMapper.findById(personId);
        Enrollment enrollment = new Enrollment(person, "school name", "degree", null, null, null, "desc");
        jpaEnrollmentMapper.insert(enrollment);
        long enrollmentId = enrollment.getId();
        jpaEnrollmentMapper.getEntityManager().clear();

        assertTrue("Inserting a Enrollment did not get a positive id.", jpaEnrollmentMapper.findById(enrollmentId)
                .getId() > 0);
    }

    /**
     * Test deleting a enrollment.
     */
    @Test(expected = NoResultException.class)
    public void testDelete()
    {
        final long enrollmentId = 2042L;
        jpaEnrollmentMapper.delete(enrollmentId);

        // if deleted, this should throw NoResultsException
        jpaEnrollmentMapper.findById(enrollmentId);
    }

    /**
     * Test finding a person's enrollment.
     */
    @Test
    public void testfindPersonEnrollments()
    {
        final String uuid = "2d359911-0977-418a-9490-57e8252b1a42";
        List<Enrollment> enrollments = jpaEnrollmentMapper.findPersonEnrollmentsByOpenSocialId(uuid);

        assertTrue("No Enrollments found for person with open social id 2d359911-0977-418a-9490-57e8252b1a42",
                enrollments.size() > 0);

        // verify loaded attributes of enrollment
        assertEquals("Incorrect school name returned", "school_name_1", enrollments.get(0).getSchoolName());
        assertEquals("Expected enrollment to contain activity", 1, enrollments.get(0).getActivities().size());
        assertEquals("Expected enrollment to contain area of study", 1, enrollments.get(0).getAreasOfStudy().size());
    }

    /**
     * Test finding a person's enrollment.
     */
    @Test
    public void testfindPersonEnrollmentsById()
    {
        final Long id = Long.valueOf("42");
        List<Enrollment> enrollments = jpaEnrollmentMapper.findPersonEnrollmentsById(id);
                
        assertTrue("No Enrollments found for person with id",
                enrollments.size() > 0);

        // verify loaded attributes of enrollment
        assertEquals("Incorrect school name returned", "school_name_1", enrollments.get(0).getSchoolName());
        assertEquals("Expected enrollment to contain activity", 1, enrollments.get(0).getActivities().size());
        assertEquals("Expected enrollment to contain area of study", 1, enrollments.get(0).getAreasOfStudy().size());
    }
    
    /**
     * Test that set/get of activities and areas of study update in db correctly.
     */
    @Test
    public void testUpdateEnrollmentCollections()
    {
        final long enrollmentId = 2042L;
        Enrollment enrollment = jpaEnrollmentMapper.findById(enrollmentId);
        ArrayList<BackgroundItem> activities = new ArrayList<BackgroundItem>(3);
        activities.add(new BackgroundItem("a", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("b", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("c", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        enrollment.setActivities(activities);

        ArrayList<BackgroundItem> areasOfStudy = new ArrayList<BackgroundItem>(3);
        areasOfStudy.add(new BackgroundItem("d", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("e", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("f", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("g", BackgroundItemType.AREA_OF_STUDY));
        enrollment.setAreasOfStudy(areasOfStudy);

        jpaEnrollmentMapper.flush();
        jpaEnrollmentMapper.getEntityManager().clear();

        enrollment = jpaEnrollmentMapper.findById(enrollmentId);

        assertEquals("Expected enrollment to contain activity", 3, enrollment.getActivities().size());
        assertEquals("Expected enrollment to contain area of study", 4, enrollment.getAreasOfStudy().size());

    }

    /**
     * Tests the flush and index method.
     */
    @Test
    public void voidTestFlushAndIndex()
    {
        final long enrollmentId = 2042L;
        Enrollment enrollment = jpaEnrollmentMapper.findById(enrollmentId);
        ArrayList<BackgroundItem> activities = new ArrayList<BackgroundItem>(3);
        activities.add(new BackgroundItem("a", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("b", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("c", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        enrollment.setActivities(activities);

        ArrayList<BackgroundItem> areasOfStudy = new ArrayList<BackgroundItem>(3);
        areasOfStudy.add(new BackgroundItem("d", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("e", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("f", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("g", BackgroundItemType.AREA_OF_STUDY));
        enrollment.setAreasOfStudy(areasOfStudy);

        jpaEnrollmentMapper.flush("2d359911-0977-418a-9490-57e8252b1142");
        
        jpaEnrollmentMapper.getEntityManager().clear();

        enrollment = jpaEnrollmentMapper.findById(enrollmentId);

        assertEquals("Expected enrollment to contain activity", 3, enrollment.getActivities().size());
        assertEquals("Expected enrollment to contain area of study", 4, enrollment.getAreasOfStudy().size());        
    }

}
