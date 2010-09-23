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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.action.request.SendWelcomeEmailRequest;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.service.actions.strategies.ReflectiveUpdater;

/**
 * Strategy for creating person record in the system.
 *
 */
public class CreatePersonExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Send welcome email action key.
     */
    private String sendWelcomeEmailAction = null;

    /**
     * Persist resource action.
     */
    private PersistResourceExecution<Person> persistResourceExecution;

    /**
     * Factory to create person.
     */
    private CreatePersonActionFactory createPersonActionFactory;

    /**
     * Person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Mapper to find orgs by id.
     */
    private FindByIdMapper<Organization> findByIdMapper;

    /**
     * Constructor.
     *
     * @param inCreatePersonActionFactory
     *            action factory persist user updates.
     * @param inPersonMapper
     *            mapper to get people.
     * @param inSendWelcomeEmailAction
     *            Send welcome email action key.
     * @param inFindByIdMapper
     *            Mapper to get orgs by id
     */
    public CreatePersonExecution(final CreatePersonActionFactory inCreatePersonActionFactory,
            final PersonMapper inPersonMapper, final String inSendWelcomeEmailAction,
            final FindByIdMapper<Organization> inFindByIdMapper)
    {
        createPersonActionFactory = inCreatePersonActionFactory;
        personMapper = inPersonMapper;
        sendWelcomeEmailAction = inSendWelcomeEmailAction;
        findByIdMapper = inFindByIdMapper;
    }

    /**
     * Add person to the system.
     *
     * @param inActionContext
     *            The action context
     *
     * @return true on success.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        CreatePersonRequest createRequest = (CreatePersonRequest) inActionContext.getActionContext().getParams();
        Person inPerson = createRequest.getPerson();
        Organization org = findByIdMapper
                .execute(new FindByIdRequest("Organization", createRequest.getOrganizationId()));

        persistResourceExecution = createPersonActionFactory.getCreatePersonAction(personMapper,
                new ReflectiveUpdater());

        log.debug("Adding to database: " + inPerson.getAccountId());
        final HashMap<String, Serializable> personData = inPerson.getProperties(Boolean.FALSE);
        personData.put("organization", org);

        persistResourceExecution.execute(new TaskHandlerActionContext<PrincipalActionContext>(
                new PrincipalActionContext()
                {
                    private static final long serialVersionUID = 9196683601970713330L;

                    @Override
                    public Principal getPrincipal()
                    {
                        throw new RuntimeException("No principal available for this execution.");
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
                }, null));
        log.info("Added to database: " + inPerson.getAccountId());

        // Send email notification if necessary
        if (createRequest.getSendEmail() && sendWelcomeEmailAction != null && !sendWelcomeEmailAction.isEmpty())
        {
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest(sendWelcomeEmailAction, null, new SendWelcomeEmailRequest(
                            inPerson.getEmail(), inPerson.getAccountId())));
        }

        return true;
    }
}
