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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation for biography.
 * 
 */
public class PersistEducationValidation implements ValidationStrategy<ServiceActionContext>
{
    /**
     * FindbyId mapper for enrollments.
     */
    FindByIdMapper<Enrollment> enrollmentMapper;

    /**
     * @param inMapper FindbyId mapper for enrollments.
     */
    public PersistEducationValidation(final FindByIdMapper<Enrollment> inMapper)
    {
        enrollmentMapper = inMapper;
    }

    
    //TODO these keys should be moved to a DTO/ModelView then referenced from there.
    
    /**
     * The key used from the form.
     */
    public static final String ENROLLMENT_ID_KEY = "id";

    /**
     * The school name key.
     */
    public static final String SCHOOL_NAME_KEY = "nameOfSchool";

    /**
     * The degree key used in the hash map from the form.
     */
    public static final String DEGREE_KEY = "degree";

    /**
     * The areas of study key used in the hash map from the form.
     */
    public static final String AREAS_OF_STUDY_KEY = "areasOfStudy";

    /**
     * The grad date key used in the hash map from the form.
     */
    public static final String GRADDATE_KEY = "yearGraduated";

    /**
     * The title key used in the hash map from the form.
     */
    public static final String ACTIVITIES_KEY = "activities";

    /**
     * The title key used in the hash map from the form.
     */
    public static final String ADDITIONAL_DETAILS_KEY = "additionalDetails";
    
    /**
     * Max number of characters for details field.
     */
    private static final int DETAILS_MAX = 200;

    /**
     * main validation method for biography.
     * 
     * @param inActionContext
     *            content for action.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final ServiceActionContext inActionContext)
    {
        HashMap<String, Serializable> education = (HashMap<String, Serializable>) inActionContext.getParams();

        ValidationException ve = new ValidationException();

        if (!education.containsKey(SCHOOL_NAME_KEY) || ((String) education.get(SCHOOL_NAME_KEY)).length() < 1)
        {
            ve.addError(SCHOOL_NAME_KEY, "School Name is required");
        }

        if (!education.containsKey(DEGREE_KEY) || ((String) education.get(DEGREE_KEY)).length() < 1
                || ((String) education.get(DEGREE_KEY)).equals("Select"))
        {
            ve.addError(DEGREE_KEY, "Degree is required");
        }

        if (!education.containsKey(AREAS_OF_STUDY_KEY) || ((String) education.get(AREAS_OF_STUDY_KEY)).length() < 1)
        {
            ve.addError(AREAS_OF_STUDY_KEY, "Area of Study is required");
        }

        if (education.containsKey(ADDITIONAL_DETAILS_KEY)
                && ((String) education.get(ADDITIONAL_DETAILS_KEY)).length() > DETAILS_MAX)
        {
            ve.addError(ADDITIONAL_DETAILS_KEY, "Additional Details supports up to " + DETAILS_MAX + " characters");
            
        }

        if (education.containsKey(GRADDATE_KEY))
        {
            try
            {
                // testing for valid date. A date is not required.
                if (education.get(GRADDATE_KEY) != null)
                {
                    String inputDate = String.valueOf(education.get(GRADDATE_KEY));
                    if (inputDate != null && inputDate.length() > 0)
                    {
                        // Incorrect number of characters in the year.
                        if (!Pattern.matches("[0-9]{4}", inputDate))
                        {
                            ve.addError(GRADDATE_KEY, "Year Graduated supports 4 numeric characters");
                        }
                        @SuppressWarnings("unused")
                        Date testDate = getDateObjectFromDateString(inputDate);
                    }
                }
            }

            catch (ParseException pex)
            {
                ve.addError(GRADDATE_KEY, "Year Graduated supports 4 numeric characters");
            }
        }

        if (education.containsKey(ENROLLMENT_ID_KEY))
        {
            Enrollment enrollment = enrollmentMapper.execute(new FindByIdRequest("Enrollment", (Long) education
                    .get(ENROLLMENT_ID_KEY)));
            if (enrollment == null)
            {
                ve.addError(ENROLLMENT_ID_KEY, "Can not update - no such record");
            }
            else
            {
                inActionContext.getState().put("EnrollmentToUpdate", enrollment);
            }
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }

    }

    /**
     * Returns a date object from provided string representation of a date (expecting "yyyy") or null if date string is
     * null or empty string after trim operation.
     * 
     * @param inDateString
     *            The date string (yyyy).
     * @return date object representing the string.
     * @throws ParseException
     *             If unable to parse a Date object from the string.
     */
    private Date getDateObjectFromDateString(final String inDateString) throws ParseException
    {
        if (null == inDateString || inDateString.trim().isEmpty())
        {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy");
        return df.parse(inDateString);
    }
}
