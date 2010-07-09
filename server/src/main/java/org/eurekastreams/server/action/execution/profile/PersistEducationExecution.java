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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.validation.profile.PersistEducationValidation;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Execution for persisting education information.
 * 
 */
public class PersistEducationExecution implements ExecutionStrategy<ServiceActionContext>
{

    /**
     * Person Mapper to lookup current user.
     */
    private final PersonMapper personMapper;

    /**
     * The mapper to get the enrollment.
     */
    private final FindByIdMapper<Enrollment> enrollmentFindMapper;

    /**
     * mapper to insert a new enrollment.
     */
    private final InsertMapper<Enrollment> enrollmentInsertMapper;

    /**
     * Mapper to reindex person in search index.
     */
    private final IndexEntity<Person> personIndexer;

    /**
     * @param inPersonMapper
     *            person mapper to use.
     * @param inEnrollmentFindMapper
     *            the find mapper for the enrollments.
     * @param inEnrollmentInsertMapper
     *            the insert mapper for the enrollments.
     * @param inPersonIndexer
     *            {@link IndexEntity}.
     */
    public PersistEducationExecution(final PersonMapper inPersonMapper,
            final FindByIdMapper<Enrollment> inEnrollmentFindMapper,
            final InsertMapper<Enrollment> inEnrollmentInsertMapper, final IndexEntity<Person> inPersonIndexer)
    {
        personMapper = inPersonMapper;
        enrollmentFindMapper = inEnrollmentFindMapper;
        enrollmentInsertMapper = inEnrollmentInsertMapper;
        personIndexer = inPersonIndexer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        Enrollment enrollment;

        HashMap<String, Serializable> formdata = (HashMap<String, Serializable>) inActionContext.getParams();
        DateFormat df = new SimpleDateFormat("yyyy");

        Person currentPerson = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
        Date gradDate = null;
        try
        {
            if (formdata.get(PersistEducationValidation.GRADDATE_KEY) != null
                    && !formdata.get(PersistEducationValidation.GRADDATE_KEY).equals(""))
            {
                gradDate = df.parse(String.valueOf(formdata.get(PersistEducationValidation.GRADDATE_KEY)));
            }
        }
        catch (ParseException e)
        {
            // This should never happen since validation should caught this.
            return new ExecutionException("Validation did not caught exception: " + e);
        }
        String schoolName = ((String) formdata.get(PersistEducationValidation.SCHOOL_NAME_KEY));
        List<BackgroundItem> areasOfStudy = convertStringToBackgroundItems((String) formdata
                .get(PersistEducationValidation.AREAS_OF_STUDY_KEY), BackgroundItemType.AREA_OF_STUDY);

        List<BackgroundItem> activities = new LinkedList<BackgroundItem>();
        String details = "";

        // If not require check first since it could be valid but comind from a different form.
        if (formdata.containsKey(PersistEducationValidation.ACTIVITIES_KEY))
        {
            activities = convertStringToBackgroundItems((String) formdata
                    .get(PersistEducationValidation.ACTIVITIES_KEY), BackgroundItemType.ACTIVITY_OR_SOCIETY);
        }

        if (formdata.containsKey(PersistEducationValidation.ADDITIONAL_DETAILS_KEY))
        {
            details = ((String) formdata.get(PersistEducationValidation.ADDITIONAL_DETAILS_KEY));
        }

        String degree = ((String) formdata.get(PersistEducationValidation.DEGREE_KEY));

        if (formdata.containsKey(PersistEducationValidation.ENROLLMENT_ID_KEY))
        {
            Long id = ((Long) formdata.get(PersistEducationValidation.ENROLLMENT_ID_KEY));

            if (inActionContext.getState().containsKey("EnrollmentToUpdate"))
            {
                enrollment = (Enrollment) inActionContext.getState().get("EnrollmentToUpdate");
            }
            else
            {
                enrollment = enrollmentFindMapper.execute(new FindByIdRequest("Enrollment", id));
                inActionContext.getState().put("EnrollmentToUpdate", enrollment);
            }

            enrollment.setActivities(activities);
            enrollment.setAdditionalDetails(details);
            enrollment.setAreasOfStudy(areasOfStudy);
            enrollment.setDegree(degree);
            enrollment.setGradDate(gradDate);
            enrollment.setSchoolName(schoolName);

        }
        else
        {
            enrollment = new Enrollment(currentPerson, schoolName, degree, areasOfStudy, gradDate, activities, details);
            enrollmentInsertMapper.execute(new PersistenceRequest<Enrollment>(enrollment));
        }

        personIndexer.execute(currentPerson);

        return enrollment;
    }

    /**
     * Convert string to a list of BackgroundItems of provided type.
     * 
     * @param bgItems
     *            "," delimited input from user.
     * @param type
     *            BackgroundItemType for the newly created BackgroundItems.
     * @return List of BackgroundItems of provided type.
     */
    private List<BackgroundItem> convertStringToBackgroundItems(final String bgItems, final BackgroundItemType type)
    {

        ArrayList<BackgroundItem> results = new ArrayList<BackgroundItem>();

        String[] bgItemsArray = bgItems.split(",");

        for (String bgItem : bgItemsArray)
        {
            // if it tokenized the ending , then don't set a blank long.
            if (bgItem != "")
            {
                results.add(new BackgroundItem(bgItem.trim(), type));
            }
        }

        return results;
    }
}
