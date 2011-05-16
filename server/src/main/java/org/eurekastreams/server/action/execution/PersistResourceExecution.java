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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ResourcePersistenceStrategy;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Persist a resource.
 * 
 * @param <T>
 *            the type of resource to persist
 */
public class PersistResourceExecution<T> implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * the person creator factory. the PersistResourceAction class depends on an object of the same type, but spring
     * wouldn't allow me to instantiate an object of the same type that it is currently instantiating, so i get around
     * that by using the factory.
     * 
     */
    private CreatePersonActionFactory factory;

    /**
     * the person mapper.
     * 
     */
    private PersonMapper personMapper;

    /**
     * the persistence strategy.
     */
    private ResourcePersistenceStrategy<T> persistenceStrategy;

    /**
     * the strategy used to set the resource's properties.
     */
    private UpdaterStrategy updater;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            The PersonMapper.
     * @param inFactory
     *            The CreatePersonActionFactory.
     * @param inUpdater
     *            The UpdaterStrategy.
     * @param inPersistenceStrategy
     *            The ResourcePersistenceStrategy to be used.
     */
    @SuppressWarnings("unchecked")
    public PersistResourceExecution(final PersonMapper inPersonMapper, final CreatePersonActionFactory inFactory,
            final UpdaterStrategy inUpdater, final ResourcePersistenceStrategy inPersistenceStrategy)
    {
        personMapper = inPersonMapper;
        factory = inFactory;
        updater = inUpdater;
        persistenceStrategy = inPersistenceStrategy;
    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        try
        {
            // convert the params to a map
            Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getActionContext()
                    .getParams();
            Map<String, Serializable> newFields = new HashMap<String, Serializable>();

            // make sure all lists have attached, existing domain objects
            for (String key : fields.keySet())
            {
                // replace sets of Person new or attached entities (as opposed to detached entities)
                if (fields.get(key) instanceof Set)
                {
                    Set<Person> verifiedPersons = reformatSet(inActionContext, (Set<Person>) fields.get(key));
                    newFields.put(key, (Serializable) verifiedPersons);
                }
                else
                {
                    newFields.put(key, fields.get(key));
                }
            }

            // get the resource to persist
            T resource = persistenceStrategy.get(inActionContext, newFields);

            // set the properties on the resource
            updater.setProperties(resource, newFields);

            // persist the organization
            persistenceStrategy.persist(inActionContext, newFields, resource);

            return (Serializable) resource;
        }
        catch (Exception e)
        {
            if (e instanceof ValidationException)
            {
                ValidationException ve = (ValidationException) e;
                Set<Entry<String, String>> errors = ve.getErrors().entrySet();
                StringBuffer b = new StringBuffer();
                for (Entry<String, String> entry : errors)
                {
                    b.append(entry.getKey() + ":" + entry.getValue() + " ");
                }
                log.error(b.toString());
            }
            throw new ExecutionException(e);
        }
    }

    /**
     * Returns a set of people that are created in DB is needed.
     * 
     * @param inActionContext
     *            the action context
     * @param requestedPersons
     *            The set of requested persons.
     * @return Set of "created" person objects.
     * @throws Exception
     *             If error occurs.
     */
    private Set<Person> reformatSet(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Set<Person> requestedPersons) throws Exception
    {
        // create the coordinator lists
        Set<Person> verifiedPersons = new HashSet<Person>();

        // if a requested coordinator is not already in the database,
        // then create a new Person object
        for (Person requestedPerson : requestedPersons)
        {
            Person verfiedCoordinator = personMapper.findByAccountId(requestedPerson.getAccountId());
            if (verfiedCoordinator == null)
            {

                final HashMap<String, Serializable> personData = requestedPerson.getProperties();

                // to avoid a circular dependency, must get a new person creator from the factory rather than
                // have it injected by spring
                PersistResourceExecution<Person> personCreator = factory.getCreatePersonAction(personMapper, updater);

                verfiedCoordinator = (Person) personCreator
                        .execute(new TaskHandlerActionContext<PrincipalActionContext>(new PrincipalActionContext()
                        {
                            private static final long serialVersionUID = -4244956205984596485L;

                            @Override
                            public Principal getPrincipal()
                            {
                                return inActionContext.getActionContext().getPrincipal();
                            }

                            @Override
                            public Serializable getParams()
                            {
                                return personData;
                            }

                            @Override
                            public Map<String, Object> getState()
                            {
                                return null;
                            }

                            @Override
                            public String getActionId()
                            {
                                return null;
                            }

                            @Override
                            public void setActionId(final String inActionId)
                            {

                            }
                        }, inActionContext.getUserActionRequests()));
            }
            verifiedPersons.add(verfiedCoordinator);
        }

        return verifiedPersons;
    }
}
