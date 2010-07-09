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
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation checks if a person exist before looking up some information about them.
 * 
 */
public class GetProfileInformationValidation implements ValidationStrategy<ServiceActionContext>
{

    /**
     * Person Mapper.
     */
    private final FindByIdMapper<Person> pMapper;

    /**
     * @param inPersonMapper
     *            person Mapper.
     */
    public GetProfileInformationValidation(final FindByIdMapper<Person> inPersonMapper)
    {
        pMapper = inPersonMapper;
    }

    /**
     * main validation method for seeing if a person exist to lookup.
     * 
     * @param inActionContext
     *            content for action.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final ServiceActionContext inActionContext)
    {
        Long id;

        try
        {
            id = (Long) inActionContext.getParams();

        }
        catch (ClassCastException ex)
        {
            throw new ValidationException("Unable to retreive information expects a Person Id.");
        }

        Person user = pMapper.execute(new FindByIdRequest("Person", id));
        if (user == null)
        {
            throw new ValidationException("Unable to retreive information Person does not exist.");
        }
    }
}
