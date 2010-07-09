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

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.NoResultException;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;

/**
 * REST end-point for an enrollment entry.
 */
public class EnrollmentsEntryResource extends EnrollmentsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(EnrollmentsEntryResource.class);
    
    /**
     * The id of the enrollment that is being posted.
     */
    private Long enrollmentId;

    /**
     * The uuid of the person.
     */
    private String uuid;
    
    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        enrollmentId = Long.valueOf((String) request.getAttributes().get("enrollmentId"));
        uuid = (String) request.getAttributes().get("uuid");
    }

    /**
     * Handles GET request.
     * 
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the enrollment description
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        Enrollment enrollment = null;
        
        try
        {
            enrollment = getEnrollmentMapper().findById(enrollmentId);
        }
        catch (NoResultException nre)
        {
            String msg = "Unable to find enrollment with id: "
                + enrollmentId;
            log.error(msg, nre);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);            
        }        

        JSONObject jsonEnrollmentObject = convertEnrollmentToJSONObject(enrollment);        
        if (log.isTraceEnabled())
        {
            log.trace("EnrollmentsEntryResource: json =   " + jsonEnrollmentObject.toString());
        }
        
        Representation rep = new StringRepresentation(jsonEnrollmentObject.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    /**
     * Handle PUT requests.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {        
        try
        {
            Enrollment enrollment = getEnrollmentMapper().findById(enrollmentId);
            if (null == enrollment)
            {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
            }
            
            JSONObject jsonObject = JSONObject.fromObject(entity.getText());
            
            log.debug("EnrollmentsEntryResource storeRepresentation: json = " + jsonObject.toString()); 
            
            validateEnrollment(jsonObject);
            
            enrollment.setSchoolName(jsonObject.getString(SCHOOL_NAME_KEY));
            enrollment.setDegree(jsonObject.getString(DEGREE_KEY));
            
            enrollment.setAreasOfStudy(
                    convertJSONArrayToBackgroundItems(jsonObject.getJSONArray(AREAS_OF_STUDY_KEY),
                            BackgroundItemType.AREA_OF_STUDY));
            
            enrollment.setActivities(
                    convertJSONArrayToBackgroundItems(jsonObject.getJSONArray(ACTIVITIES_KEY),
                            BackgroundItemType.ACTIVITY_OR_SOCIETY));
            
            enrollment.setGradDate(getDateObjectFromDateString(
                    jsonObject.getString(GRADDATE_KEY)));                      
            
            enrollment.setAdditionalDetails(jsonObject.getString(ADDITIONAL_DETAILS_KEY));
            
            getEnrollmentMapper().flush(uuid);

            getAdaptedResponse().setEntity(convertEnrollmentToJSONObject(enrollment).toString(),
                    MediaType.APPLICATION_JSON);
        }
        catch (IOException ioe)
        {            
            String msg = "Unable to parse enrollment JSON passed in with id: " + enrollmentId;
            log.error(msg, ioe);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }
        catch (ParseException pe)
        {
            String msg = "Unable to parse date for enrollment update " + enrollmentId;
            log.error(msg, pe);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }
    }
    
    /**
     * Handle DELETE requests.
     * 
     */
    @Override
    public void removeRepresentations()
    {
            getEnrollmentMapper().delete(enrollmentId);
            getEnrollmentMapper().flush(uuid);
    }
}
