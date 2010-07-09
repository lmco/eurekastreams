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

import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.EnrollmentMapper;

/**
 * Authorization for Insert/Update/Delete education.
 *
 */
public class ModifyEducationAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Transform the request.
     */
    private RequestTransformer transformer;
    /**
     * Enrollment Mapper.
     */
    private EnrollmentMapper mapper;

    /**
     * Default constructor.
     *
     * @param inMapper
     *            mapper.
     * @param inTransformer
     *            request transformer.
     */
    public ModifyEducationAuthorizationStrategy(final EnrollmentMapper inMapper, final RequestTransformer inTransformer)
    {
        mapper = inMapper;
        transformer = inTransformer;
    }

    /**
     * Authorize.
     *
     * @param inActionContext
     *            action context.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        Long id = (Long) transformer.transform(inActionContext);

        List<Enrollment> enrollments = mapper.findPersonEnrollmentsByOpenSocialId(inActionContext.getPrincipal()
                .getOpenSocialId());

        for (Enrollment enrollment : enrollments)
        {
            if (enrollment.getId() == id)
            {
                return;
            }
        }

        throw new AuthorizationException("Only the owner of an enrollment can delete it.");
    }

}
