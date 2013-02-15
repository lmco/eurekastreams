/*
 * Copyright (c) 2010-2013 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.InlineExecutionStrategyExecutor;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.action.request.SendWelcomeEmailRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ReflectiveUpdater;

/**
 * Strategy for creating person record in the system.
 */
public class CreatePersonExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Send welcome email action key.
     */
    private String sendWelcomeEmailAction = null;

    /**
     * Factory to create person.
     */
    private final CreatePersonActionFactory createPersonActionFactory;

    /**
     * Person mapper.
     */
    private final PersonMapper personMapper;

    /**
     * Constructor.
     * 
     * @param inCreatePersonActionFactory
     *            action factory persist user updates.
     * @param inPersonMapper
     *            mapper to get people.
     * @param inSendWelcomeEmailAction
     *            Send welcome email action key.
     */
    public CreatePersonExecution(final CreatePersonActionFactory inCreatePersonActionFactory,
            final PersonMapper inPersonMapper, final String inSendWelcomeEmailAction)
    {
        createPersonActionFactory = inCreatePersonActionFactory;
        personMapper = inPersonMapper;
        sendWelcomeEmailAction = inSendWelcomeEmailAction;
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

        PersistResourceExecution<Person> persistResourceExecution = createPersonActionFactory.getCreatePersonAction(
                personMapper, new ReflectiveUpdater());

        log.debug("Adding to database: " + inPerson.getAccountId());

        Person person = (Person) new InlineExecutionStrategyExecutor().execute(persistResourceExecution,
                inPerson.getProperties(), inActionContext);

        log.info("Added to database: " + inPerson.getAccountId());

        // Send email notification if necessary
        if (createRequest.getSendEmail() && sendWelcomeEmailAction != null && !sendWelcomeEmailAction.isEmpty())
        {
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest(sendWelcomeEmailAction, null, new SendWelcomeEmailRequest(
                            inPerson.getEmail(), inPerson.getAccountId())));
        }

        return person;
    }
}
