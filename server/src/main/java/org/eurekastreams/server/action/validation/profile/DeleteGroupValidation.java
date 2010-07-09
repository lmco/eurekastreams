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

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation for Deleting a group.
 * 
 */
public class DeleteGroupValidation implements ValidationStrategy<ActionContext>
{
    /**
     * FindById mapper.
     */
    private final FindByIdMapper<DomainGroup> findByIdDAO;

    /**
     * Constructor.
     * 
     * @param inFindByIdDAO
     *            FindById mapper.
     */
    public DeleteGroupValidation(final FindByIdMapper<DomainGroup> inFindByIdDAO)
    {
        findByIdDAO = inFindByIdDAO;
    }

    /**
     * Ensure that group is present, if not, throw ValidationException.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     */
    @Override
    public void validate(final ActionContext inActionContext)
    {
        DomainGroup group = findByIdDAO.execute(new FindByIdRequest("DomainGroup", ((Long) inActionContext.getParams())
                .longValue()));

        if (null == group)
        {
            throw new ValidationException("Attempt to delete group that is no longer present");
        }

    }
}
