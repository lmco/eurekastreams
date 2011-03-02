/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.EnrollmentMapper;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * REST abstract end-point class for an enrollment resource.
 */
public abstract class EnrollmentsResource extends WritableResource
{
    
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(EnrollmentsResource.class);
    
    /**
     * Mapper to retrieve a Enrollment.
     */
    private EnrollmentMapper enrollmentMapper;
    
    /**
     * The key used in the JSON string. 
     */
    protected static final String ENROLLMENTS_KEY = "enrollments"; 

    /**
     * The key used in the JSON string. 
     */
    protected static final String ENROLLMENT_ID_KEY = "id";

    /**
     * The school name key used in the JSON string. 
     */
    protected static final String SCHOOL_NAME_KEY = "schoolname"; 

    /**
     * The degree key used in the JSON string. 
     */
    protected static final String DEGREE_KEY = "degree"; 

    /**
     * The areas of study key used in the JSON string. 
     */
    protected static final String AREAS_OF_STUDY_KEY = "areasofstudy"; 

    /**
     * The grad date key used in the JSON string. 
     */
    protected static final String GRADDATE_KEY = "graddate";  

    /**
     * The title key used in the JSON string. 
     */
    protected static final String ACTIVITIES_KEY = "activities"; 

    /**
     * The title key used in the JSON string. 
     */
    protected static final String ADDITIONAL_DETAILS_KEY = "additionaldetails"; 

    /**
     * Error message for date validation.
     */
    protected static final String DATE_VALIDATION_ERROR = "From date needs to be before End date.";
    
    /**
     * JSON key for validation errors that are returned from an invalid request.
     */
    protected static final String VALIDATION_ERRORS_KEY = "validationErrors";
    
    /**
    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected abstract void initParams(final Request request);

    /**
     * 
     * The Enrollment Mapper.  Used by tests.
     * 
     * @param inEnrollmentMapper
     *            mapper.
     */
    public void setEnrollmentMapper(final EnrollmentMapper inEnrollmentMapper)
    {
        enrollmentMapper = inEnrollmentMapper;
    }
    
    /**
     * Getter for EnrollmentMapper.
     * @return The EnrollmentMapper.
     */
    public EnrollmentMapper getEnrollmentMapper()
    {
        return enrollmentMapper;
    }
    
    /**
     * Convert Enrollment object to JSONObject.
     * @param inEnrollment Enrollment object to convert.
     * @return JSON object representing the original Enrollment object.
     */
    public JSONObject convertEnrollmentToJSONObject(final Enrollment inEnrollment)
    {
        JSONObject jsonEnrollmentObject = new JSONObject();
        
        jsonEnrollmentObject.put(ENROLLMENT_ID_KEY, inEnrollment.getId());
        jsonEnrollmentObject.put(SCHOOL_NAME_KEY, inEnrollment.getSchoolName());
        jsonEnrollmentObject.put(DEGREE_KEY, inEnrollment.getDegree());                      
        jsonEnrollmentObject.put(AREAS_OF_STUDY_KEY, 
                convertBackgroundItemListToJSONArray(inEnrollment.getAreasOfStudy()));        
        jsonEnrollmentObject.put(ACTIVITIES_KEY, 
                convertBackgroundItemListToJSONArray(inEnrollment.getActivities())); 
        jsonEnrollmentObject.put(ADDITIONAL_DETAILS_KEY, inEnrollment.getAdditionalDetails());  
        jsonEnrollmentObject.put(GRADDATE_KEY, getStringDateFromDateObject(inEnrollment.getGradDate()));
                     
        return jsonEnrollmentObject;
    }
    
    /**
     * Convert JSONArray of strings to a list of BackgroundItems of provided type.
     * @param jAarray The JSONArray of strings.
     * @param type BackgroundItemType for the newly created BackgroundItems.
     * @return List of BackgroundItems of provided type.
     */
    public List<BackgroundItem> convertJSONArrayToBackgroundItems(final JSONArray jAarray, 
            final BackgroundItemType type)
    {
        ArrayList<BackgroundItem> results = new ArrayList<BackgroundItem>(jAarray.size());        
        for (Object o : jAarray)
        {
            String name = (String) o;
            results.add(new BackgroundItem(name, type));
        }
        return results;
    }
    
    /**
     * Converts a List of BackgroundItems to JSONArray object.
     * @param items The list of BackgroundItems
     * @return JSONArray representing that list of BackgroundItems.
     */
    private JSONArray convertBackgroundItemListToJSONArray(final List<BackgroundItem> items)
    {
        JSONArray resultArray = new JSONArray();
        for (BackgroundItem b : items)
        {
            resultArray.add(b.getName());
        }     
        return resultArray;
    }
    
    /**
     * Returns string 4 digit year from provided date object, or empty string if date
     * object is null.
     * @param inDate The date object to extract the year from.
     * @return String 4 digit year from provided date object, or empty string if date
     * object is null.
     */
    private String getStringDateFromDateObject(final Date inDate)
    {
        if (null == inDate)
        {
            return "";
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(inDate);
        return String.valueOf(calendar.get(Calendar.YEAR));        
    }
    
    /**
     * Returns a date object from provided string representation of a date (expecting "yyyy")
     * or null if date string is null or empty string after trim operation.
     * @param inDateString The date string (yyyy).
     * @return date object representing the string.
     * @throws ParseException If unable to parse a Date object from the string.
     */
    protected Date getDateObjectFromDateString(final String inDateString) throws ParseException
    {
        if (null == inDateString || inDateString.trim().isEmpty())
        {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy");
        
        return df.parse(inDateString);      
    }
    
    /**
     * Validates the Enrollment object and throws an exception if it does not validate.
     * @param inJsonObject - json object containing the Enrollment to be persisted.
     * @throws ParseException - occurs if the JSON cannot be parsed correctly.
     * @throws ResourceException - thrown if the Enrollment is invalid.
     */
    protected void validateEnrollment(final JSONObject inJsonObject) throws ParseException, ResourceException
    {
        JSONObject validationErrors = new JSONObject();
        JSONArray validationErrorsList = new JSONArray();
        boolean valid = true;
 
        if (inJsonObject.getString(SCHOOL_NAME_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("School Name is required.");
        }
        
        if (inJsonObject.getString(DEGREE_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Degree is required.");
        }
        
        if (inJsonObject.getJSONArray(AREAS_OF_STUDY_KEY).size() == 0)
        {
            valid = false;
            validationErrorsList.add("Areas of Study is required.");
        }
        
        try
        {
            //testing for valid date.  A date is not required.
            String inputDate = inJsonObject.getString(GRADDATE_KEY);
            if (inputDate.length() > 0)
            {
                //Incorrect number of characters in the date.
                if (!Pattern.matches("[0-9]{4}", inputDate))
                {
                    valid = false;
                }                
                
                Date testDate = getDateObjectFromDateString(inputDate);
            }
        }
        catch (ParseException pex)
        {
            //Not a parseable date string.
            valid = false;
        }
                
        if (!valid)
        {
            log.debug("Enrollments Resource validation: invalid " + validationErrorsList.toString());
            validationErrors.put(VALIDATION_ERRORS_KEY, validationErrorsList);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, validationErrors.toString());
        }
    }
}
