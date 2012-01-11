/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.validation.start;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;

/**
 * Validation for Deleting a tab.
 */
public class DeleteTabValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * PersonPagePropertiesByIdMapper.
     */
    private DomainMapper<Long, PersonPagePropertiesDTO> personPagePropertiesByIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonPagePropertiesByIdMapper
     *            PersonPagePropertiesByIdMapper.
     */
    public DeleteTabValidation(final DomainMapper<Long, PersonPagePropertiesDTO> inPersonPagePropertiesByIdMapper)
    {
        personPagePropertiesByIdMapper = inPersonPagePropertiesByIdMapper;
    }

    /**
     * Validate tab deletion.
     * 
     * @param inActionContext
     *            action context.
     * @throws ValidationException
     *             On validation error.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        PersonPagePropertiesDTO result = personPagePropertiesByIdMapper.execute(inActionContext.getPrincipal().getId());

        if (result.getTabDTOs().size() <= 1)
        {
            throw new ValidationException("Start page must have at least 1 tab.");
        }
    }

}
