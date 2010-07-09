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
package org.eurekastreams.server.action.validation.start;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation for AddTabValidation Execution.
 * 
 */
public class AddTabValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * The DataMapper that lets look up how many tabs a person has.
     */
    private FindByIdMapper<Person> personMapper = null;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            for looking up the Person who will get the new tab
     */
    public AddTabValidation(final FindByIdMapper<Person> inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @throws ValidationException
     *             if inputs don't meet validation standards.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {

        //TODO #performance make customized request to get tab count instead of pulling back whole object.
        Person user = (Person) personMapper.execute(new FindByIdRequest("Person", 
                inActionContext.getPrincipal().getId()));
        
        if (user.getTabs(TabGroupType.START).size() >= Person.TAB_LIMIT)
        {
            throw new ValidationException(Person.TAB_LIMIT_MESSAGE);
        }

        if (((String) inActionContext.getParams()).length() > TabTemplate.MAX_TAB_NAME_LENGTH)
        {
            throw new ValidationException(TabTemplate.MAX_TAB_NAME_MESSAGE);
        }

    }
}
