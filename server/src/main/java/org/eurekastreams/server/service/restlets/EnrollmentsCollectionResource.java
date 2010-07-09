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
package org.eurekastreams.server.service.restlets;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST endpoint for a enrollment collection.
 */
public class EnrollmentsCollectionResource extends EnrollmentsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(EnrollmentsCollectionResource.class);

    /**
     * Mapper to retrieve a person.
     */
    private PersonMapper personMapper;

    /**
     * The account name of the person whose enrollments are being requested.
     */
    private String uuid;

    /**
     * Initialize parameters from the request object. the context of the request
     *
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        log.trace("Entered init");
        uuid = (String) request.getAttributes().get("uuid");
        log.trace("Exit init");
    }

    /**
     * The Person Mapper. Used by tests.
     *
     * @param inPersonMapper
     *            mapper.
     */
    public void setPersonMapper(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Handles GET request.
     *
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the enrollments additionalDetails
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        List<Enrollment> enrollments = getEnrollmentMapper().findPersonEnrollmentsByOpenSocialId(uuid);
        log.debug("EnrollmentsCollectionResource: number of enrollments = " + enrollments.size());

        JSONObject json = new JSONObject();

        JSONArray jsonEnrollments = new JSONArray();
        for (Enrollment enrollment : enrollments)
        {
            jsonEnrollments.add(convertEnrollmentToJSONObject(enrollment));
            log.debug("Added enrollment(id: " + enrollment.getId() + ") to json array.");
        }
        json.put(ENROLLMENTS_KEY, jsonEnrollments);
        if (log.isTraceEnabled())
        {
            log.trace("EnrollmentsCollectionResource: json = " + json.toString());
        }
        Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    /**
     * Handle POST requests to create new entity.
     *
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException
    {
        log.trace("Entering AcceptRepresentation");
        try
        {
            String jsonText = entity.getText();
            log.debug("POST text to EnrollmentsCollectionResource: " + jsonText);
            Person person = personMapper.findByOpenSocialId(uuid);
            JSONObject jsonObject = JSONObject.fromObject(jsonText);

            validateEnrollment(jsonObject);

            String schoolName = jsonObject.getString(SCHOOL_NAME_KEY);
            String degree = jsonObject.getString(DEGREE_KEY);

            List<BackgroundItem> areasOfStudy =
                convertJSONArrayToBackgroundItems(jsonObject.getJSONArray(AREAS_OF_STUDY_KEY),
                        BackgroundItemType.AREA_OF_STUDY);
            log.debug("Areas of Study list has " + areasOfStudy.size()
                    + " items in it - raw: " + areasOfStudy.toString());

            List<BackgroundItem> activities =
                convertJSONArrayToBackgroundItems(jsonObject.getJSONArray(ACTIVITIES_KEY),
                        BackgroundItemType.ACTIVITY_OR_SOCIETY);
            log.debug("Activity or Society list has " + activities.size()
                    + " items in it - raw: " + activities.toString());

            String additionalDetails = jsonObject.getString(ADDITIONAL_DETAILS_KEY);

            Date gradDate = getDateObjectFromDateString(jsonObject.getString(GRADDATE_KEY));

            Enrollment enrollment = new Enrollment(
                    person,
                    schoolName,
                    degree,
                    areasOfStudy,
                    gradDate,
                    activities,
                    additionalDetails);
            getEnrollmentMapper().insert(enrollment);

            getEnrollmentMapper().flush(uuid);

            getAdaptedResponse().setEntity(convertEnrollmentToJSONObject(enrollment).toString(),
                    MediaType.APPLICATION_JSON);
        }
        catch (IOException ioe)
        {
            String msg = "Unable to parse create enrollment JSON passed in for user: " + uuid + ".";
            log.error(msg, ioe);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }
        catch (ParseException pe)
        {
            String msg = "Unable to parse the date of a new enrollment for person " + uuid + ".";
            log.error(msg, pe);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }
        catch (Exception ex)
        {
            String msg = "Error occurred attempting to write enrollment for person " + uuid + ". Error: " + ex;
            log.error(msg, ex);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        }
    }
}
