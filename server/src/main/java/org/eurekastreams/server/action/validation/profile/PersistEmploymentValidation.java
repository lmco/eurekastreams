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
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation to persist employment.
 * 
 */
public class PersistEmploymentValidation implements ValidationStrategy<ServiceActionContext>
{

    /**
     * Mapper used to lookup Job.
     */
    private final FindByIdMapper<Job> jobMapper;

    // TODO these keys should be moved to a DTO/ModelView then referenced from there.

    /**
     * The key used in the JSON string.
     */
    public static final String JOB_ID_KEY = "id";

    /**
     * The company name key used in the JSON string.
     */
    public static final String COMPANY_NAME_KEY = "companyName";

    /**
     * The industry key used in the JSON string.
     */
    public static final String INDUSTRY_KEY = "industry";

    /**
     * The title key used in the JSON string.
     */
    public static final String TITLE_KEY = "title";

    /**
     * The date from key used in the JSON string.
     */
    public static final String DATE_KEY = "dates";

    /**
     * The title key used in the JSON string.
     */
    public static final String DESCRIPTION_KEY = "description";
    
    /**
     * The minimum 4-digit year.
     */
    private static final int YEAR_MINIMUM = 1000;

    /**
     * @param inJobMapper
     *            Mapper used to lookup Job.
     */
    public PersistEmploymentValidation(final FindByIdMapper<Job> inJobMapper)
    {
        jobMapper = inJobMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        HashMap<String, Serializable> employment = (HashMap<String, Serializable>) inActionContext.getParams();

        ValidationException ve = new ValidationException();

        if (((String) employment.get(COMPANY_NAME_KEY)).length() == 0)
        {
            ve.addError(COMPANY_NAME_KEY, "Company name is required");
        }
        else if (((String) employment.get(COMPANY_NAME_KEY)).length() > Job.MAX_COMPANY_NAME_LENGTH)
        {
            ve.addError(COMPANY_NAME_KEY, Job.MAX_COMPANY_LENGTH_ERROR_MESSAGE);
        }

        if (((String) employment.get(INDUSTRY_KEY)).length() == 0)
        {
            ve.addError(INDUSTRY_KEY, "Industry is required");
        }

        if (((String) employment.get(TITLE_KEY)).length() == 0)
        {
            ve.addError(TITLE_KEY, "Title is required");
        }
        else if (((String) employment.get(TITLE_KEY)).length() > Job.MAX_TITLE_LENGTH)
        {
            ve.addError(TITLE_KEY, Job.MAX_TITLE_LENGTH_ERROR_MESSAGE);
        }

        if (((String) employment.get(DESCRIPTION_KEY)).length() > Job.MAX_DESCRIPTION_LENGTH)
        {
            ve.addError(DESCRIPTION_KEY, "Description supports up to " + Job.MAX_DESCRIPTION_LENGTH + " characters");
        }

        if (((String) employment.get(DATE_KEY)).length() == 0)
        {
            ve.addError(DATE_KEY, "Employment start year is required");
        }
        else if (!testDate(((String) employment.get(DATE_KEY))))
        {
            ve.addError(DATE_KEY, "Time period is invalid");
        }

        if (employment.containsKey(JOB_ID_KEY))
        {
            Job job = jobMapper.execute(new FindByIdRequest("Job", (Long) employment.get(JOB_ID_KEY)));
            if (job == null)
            {
                ve.addError(JOB_ID_KEY, "Can not update, there is no such record");
            }
            else
            {
                inActionContext.getState().put("JobToUpdate", job);
            }
        }

        // check for valid dates

        if (!ve.getErrors().isEmpty())
        {
            System.out.println(ve.getErrors().keySet());
            throw ve;
        }
    }

    /**
     * Helper method for validating the date formatting.
     * 
     * @param dates
     *            String in "MM/yyyy;MM/yyyy" format.
     * @return true if the date can be parsed.
     */
    private boolean testDate(final String dates)
    {
        DateFormat df = new SimpleDateFormat("MM/yyyy");
        String[] datearray = dates.split(";");

        for (String dateString : datearray)
        {
            try
            {
                @SuppressWarnings("unused")
                Date testDate = df.parse(dateString);
                
                //check for valid month selection
                if (dateString.startsWith("00"))
                {
                    return false;
                }
                
                // Incorrect number of characters in the year.
                if (!Pattern.matches("[0-9]{2}/[0-9]{4}", dateString))
                {
                    return false;
                }

            }
            catch (ParseException e)
            {
                return false;
            }
        }

        return true;
    }
}
