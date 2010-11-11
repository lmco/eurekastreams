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
package org.eurekastreams.server.action.execution;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.server.service.actions.strategies.PersonDecorator;

/**
 * Returns {@link PersonPagePropertiesDTO} for current user.
 * 
 */
public class GetPersonPagePropertiesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Transformer to convert person to PersonPageProperties.
     */
    private Transformer<Person, PersonPagePropertiesDTO> transformer;

    /**
     * Decorates the person object.
     */
    private PersonDecorator decorator = null;

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Person> personByIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonByIdMapper
     *            {@link FindByIdMapper}.
     * @param inTransformer
     *            Transformer to convert person to PersonPageProperties.
     * @param inDecorator
     *            the decorator the use on the person.
     */
    public GetPersonPagePropertiesExecution(final FindByIdMapper<Person> inPersonByIdMapper,
            final Transformer<Person, PersonPagePropertiesDTO> inTransformer, final PersonDecorator inDecorator)
    {
        personByIdMapper = inPersonByIdMapper;
        transformer = inTransformer;
    }

    /**
     * Return PersonPageProptertiesDTO for current user.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return {@link PersonPagePropertiesDTO} for current user.
     */
    @Override
    public PersonPagePropertiesDTO execute(final PrincipalActionContext inActionContext)
    {
        Person p = personByIdMapper.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));

        // TODO: This doesn't seem like a great place to hit filesystem, but added to prevent regression.
        if (null != decorator && null != p)
        {
            try
            {
                decorator.decorate(p);
            }
            catch (Exception e)
            {
                throw new ExecutionException(e);
            }
        }

        long start = System.currentTimeMillis();
        PersonPagePropertiesDTO result = transformer.transform(p);
        log.debug("Transform Person to PersonPageProperties: " + (System.currentTimeMillis() - start) + "(ms)");

        return result;
    }
}
