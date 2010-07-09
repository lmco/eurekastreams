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
import java.util.Date;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.JobMapper;

/**
 * REST abstract endpoint class for an job resource.
 */
public abstract class JobsResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(JobsResource.class);

    /**
     * Mapper to retrieve a Job.
     */
    private JobMapper jobMapper;
    
    /**
     * The key used in the JSON string. 
     */
    protected static final String JOBS_KEY = "jobs"; 

    /**
     * The key used in the JSON string. 
     */
    protected static final String JOB_ID_KEY = "id";

    /**
     * The company name key used in the JSON string. 
     */
    protected static final String COMPANY_NAME_KEY = "companyname"; 

    /**
     * The industry key used in the JSON string. 
     */
    protected static final String INDUSTRY_KEY = "industry"; 

    /**
     * The title key used in the JSON string. 
     */
    protected static final String TITLE_KEY = "title"; 

    /**
     * The date from key used in the JSON string. 
     */
    protected static final String DATE_FROM_KEY = "datefrom"; 

    /**
     * The date from key used in the JSON string. 
     */
    protected static final String DATE_FROM_YEAR_KEY = "datefromyear"; 

    /**
     * The date from key used in the JSON string. 
     */
    protected static final String DATE_FROM_MONTH_KEY = "datefrommonth"; 

    /**
     * The date to key used in the JSON string. 
     */
    protected static final String DATE_TO_KEY = "dateto"; 

    /**
     * The date to key used in the JSON string. 
     */
    protected static final String DATE_TO_YEAR_KEY = "datetoyear"; 

    /**
     * The date to key used in the JSON string. 
     */
    protected static final String DATE_TO_MONTH_KEY = "datetomonth"; 

    /**
     * The title key used in the JSON string. 
     */
    protected static final String DESCRIPTION_KEY = "description"; 

    /** for extracting strings from Dates. */
    private DateFormat yearExtractor = new SimpleDateFormat("yyyy");
    
    /** for extracting strings from Dates. */
    private DateFormat monthExtractor = new SimpleDateFormat("MM");
    
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
     * The Job Mapper.  Used by tests.
     * 
     * @param inJobMapper
     *            mapper.
     */
    public void setJobMapper(final JobMapper inJobMapper)
    {
        jobMapper = inJobMapper;
    }

    /**
     * @return the jobMapper
     */
    protected JobMapper getJobMapper()
    {
        return jobMapper;
    }

    /**
     * Converts a JSONObject into a job object.
     * @param inJsonJob - json representation of a job to try and parse.
     * @param inPerson - person to whom the job belongs
     * @return - Job object.
     * @throws ParseException - if a dateformat parsing error occurs.
     */
    protected Job convertJSONObjectToJob(final JSONObject inJsonJob, 
            final Person inPerson) throws ParseException
    {
        DateFormat df = new SimpleDateFormat("MM/yyyy");
        
        String companyName = inJsonJob.getString(COMPANY_NAME_KEY);
        String industry = inJsonJob.getString(INDUSTRY_KEY);
        String title = inJsonJob.getString(TITLE_KEY);
        String dateFromString =
            inJsonJob.getString(DATE_FROM_MONTH_KEY) + "/" + inJsonJob.getString(DATE_FROM_YEAR_KEY);
        Date dateFrom = df.parse(dateFromString);
        Date dateTo = null;
        if (!inJsonJob.getString(DATE_TO_YEAR_KEY).equals(""))
        {
            String dateToString =
                inJsonJob.getString(DATE_TO_MONTH_KEY) + "/" + inJsonJob.getString(DATE_TO_YEAR_KEY);
            dateTo = df.parse(dateToString);
        }
        String description = inJsonJob.getString(DESCRIPTION_KEY);

        Job job = new Job(inPerson, companyName, industry, title, dateFrom, dateTo, description);

        return job;
    }

    /**
     * Convert the passed in Job object to JSON.
     * @param inJob - job object to convert to JSON.
     * @return JSON representation of the passed in Job object.
     */
    protected JSONObject convertJobToJSON(final Job inJob)
    {
        JSONObject jsonJobObject = new JSONObject();
        jsonJobObject.put(JOB_ID_KEY, inJob.getId());
        jsonJobObject.put(COMPANY_NAME_KEY, inJob.getCompanyName());
        jsonJobObject.put(INDUSTRY_KEY, inJob.getIndustry());
        jsonJobObject.put(TITLE_KEY, inJob.getTitle());
        
        Date dateFrom = inJob.getDateFrom();
        String year = getYear(dateFrom);
        String month = getMonth(dateFrom);
        jsonJobObject.put(DATE_FROM_YEAR_KEY, year);
        jsonJobObject.put(DATE_FROM_MONTH_KEY, month);
        
        // if null, the string is empty (presumably this is Present Date)
        Date dateTo = inJob.getDateTo();
        year = getYear(dateTo);
        month = getMonth(dateTo);
        jsonJobObject.put(DATE_TO_YEAR_KEY, year);
        jsonJobObject.put(DATE_TO_MONTH_KEY, month);    
        
        jsonJobObject.put(DESCRIPTION_KEY, inJob.getDescription());
        
        return jsonJobObject;
    }
    
    /**
     * 
     * @param input the date
     * @return 4-digit year string
     */
    protected String getYear(final Date input)
    {
        String result = (input == null ? "" : yearExtractor.format(input));
        return result;
    }
    
    /**
     * 
     * @param input the date
     * @return 2-digit month string
     */
    protected String getMonth(final Date input)
    {
        String result = (input == null ? "" : monthExtractor.format(input));
        return result;
    }

    /**
     * Validates the Enrollment object and throws an exception if it does not validate.
     * @param inJsonObject - json object containing the Enrollment to be persisted.
     * @throws ParseException - occurs if the JSON cannot be parsed correctly.
     * @throws ResourceException - thrown if the Enrollment is invalid.
     */
    protected void validateJobHistory(final JSONObject inJsonObject) throws ParseException, ResourceException
    {
        JSONObject validationErrors = new JSONObject();
        JSONArray validationErrorsList = new JSONArray();
        boolean valid = true;
 
        if (inJsonObject.getString(COMPANY_NAME_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Company Name is required.");
        }
        
        if (inJsonObject.getString(INDUSTRY_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Industry is required.");
        }
        
        if (inJsonObject.getString(TITLE_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Job Title is required.");
        }
        
        if (inJsonObject.getString(DESCRIPTION_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Job Description is required.");
        }

        if (inJsonObject.getString(DATE_FROM_MONTH_KEY).length() == 0 
                || inJsonObject.getString(DATE_FROM_YEAR_KEY).length() == 0)
        {
            valid = false;
            validationErrorsList.add("Job Start Date is required.");
        }
        
        try
        {
            //testing for valid date.  A date is not required.
            String dateFromYear = inJsonObject.getString(DATE_FROM_YEAR_KEY);
            String dateFromMonth = inJsonObject.getString(DATE_FROM_MONTH_KEY);
            String dateToYear = inJsonObject.getString(DATE_FROM_YEAR_KEY);
            String dateToMonth = inJsonObject.getString(DATE_FROM_MONTH_KEY);
            
            if (!(testDate(dateFromMonth, dateFromYear)
                    && testDate(dateToMonth, dateToYear)))
            {
                valid = false;
                validationErrorsList.add("Job Time Period is invalid, " 
                        + "please verify that the dates are correct and in chronological order.");
            }
        }
        catch (ParseException pex)
        {
            //Not a parseable date string.
            valid = false;
            validationErrorsList.add("Job Time Period is invalid, " 
                    + "please verify that the dates are correct and in chronological order.");
        }
                
        if (!valid)
        {
            log.debug("Job Resource validation: invalid " + validationErrorsList.toString());
            validationErrors.put(VALIDATION_ERRORS_KEY, validationErrorsList);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, validationErrors.toString());
        }
    }
    
    /**
     * Helper method for validating the date formatting.
     * @param dateMonth - string month to test validation.
     * @param dateYear - string year to test validation.
     * @return - false if the validation doesn't succeed.
     * @throws ParseException - if a date parsing exception occurs.
     */
    private boolean testDate(final String dateMonth, final String dateYear) throws ParseException
    {
        boolean validDate = true;

        if (dateMonth.length() > 0 && dateYear.length() > 0)
        {
            //Incorrect number of characters in the date.
            if (!Pattern.matches("[0-9]{4}", dateYear))
            {
                validDate = false;
            }                
            DateFormat df = new SimpleDateFormat("MM/yyyy");
            Date testDate = df.parse(dateMonth + "/" + dateYear);
        }
        
        return validDate;
    }
    
}
