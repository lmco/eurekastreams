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
package org.eurekastreams.server.action.authorization.profile;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Checks Authorization if user attempts top update an enrollment.
 * 
 */
public class PersistEducationAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * The mapper to get the enrollment.
     */
    private final FindByIdMapper<Enrollment> enrollmentFindMapper;

    /**
     * @param inEnrollmentFindMapper
     *            find by Id mapper for enrollment.
     */
    public PersistEducationAuthorizationStrategy(final FindByIdMapper<Enrollment> inEnrollmentFindMapper)
    {
        enrollmentFindMapper = inEnrollmentFindMapper;
    }

    /**
     * Check if user is authorized.
     * 
     * @param inActionContext
     *            the context with the request info in it.
     * @throws AuthorizationException
     *             if the user is not authorized for the action.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        HashMap<String, Serializable> formdata = (HashMap<String, Serializable>) inActionContext.getParams();

        if (formdata.containsKey("id"))
        {
            Long enrollmentId = (Long) formdata.get("id");
            Long entityID = inActionContext.getPrincipal().getId();

            Enrollment enrollment;

            // Check if we have Enrollment in our state if so then use that.
            if (inActionContext.getState().containsKey("EnrollmentToUpdate"))
            {
                enrollment = (Enrollment) inActionContext.getState().get("EnrollmentToUpdate");
            }
            else
            {
                enrollment = enrollmentFindMapper.execute(new FindByIdRequest("Enrollment", enrollmentId));
                inActionContext.getState().put("EnrollmentToUpdate", enrollment);
            }

            if (enrollment.getPerson().getId() != entityID)
            {
                throw new AuthorizationException("User with id:" + entityID
                        + " attempted to update enrollment with the id of " + enrollmentId);
            }
        }
    }
}
