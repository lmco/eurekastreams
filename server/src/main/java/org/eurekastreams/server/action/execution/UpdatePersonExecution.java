/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.BackgroundMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This class updates a person object based on the Map of fields that come in and match the properties of the Person
 * object.
 *
 */
public class UpdatePersonExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Person mapper.
     */
    private final PersonMapper personMapper;

    /**
     * Background mapper.
     */
    private final BackgroundMapper backgroundMapper;

    /**
     * Persist Resource Execution strategy configured for a person.
     */
    private final TaskHandlerExecutionStrategy personPersister;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            - instance of {@link PersonMapper} for this execution strategy.
     * @param inPersonPersister
     *            - instance of {@link PersistResourceExecution} for a Person.
     * @param inBackgroundMapper
     *            - instance of {@link BackgroundMapper} for this execution strategy.
     */
    public UpdatePersonExecution(final PersonMapper inPersonMapper,
            final TaskHandlerExecutionStrategy inPersonPersister, final BackgroundMapper inBackgroundMapper)
    {
        personMapper = inPersonMapper;
        personPersister = inPersonPersister;
        backgroundMapper = inBackgroundMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method updates the person object with the data from the form.
     */
    @Override
    // TODO: This is a weird mix of wrapping PersistResourceAction with another execution strategy and they are
    // each fiddling with the update in different ways. This needs to be refactored. It's at best non-standard
    // and error-prone in it's current state.
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getActionContext().getParams();
        fields.remove("isAdministrator");
        fields.remove(PersonModelView.CELLPHONE_KEY);
        fields.remove(PersonModelView.FAX_KEY);

        log.debug("Updating person with fields: " + fields.keySet());

        Person person = (Person) personPersister.execute(inActionContext);

        List<BackgroundItem> skills = convertStringToBackgroundItems((String) fields.get(PersonModelView.SKILLS_KEY),
                BackgroundItemType.SKILL);

        Background background = backgroundMapper.findOrCreatePersonBackground(person.getOpenSocialId());
        background.setBackgroundItems(skills, BackgroundItemType.SKILL);

        personMapper.flush();

        return person;
    }

    /**
     * Converts a String of comma separated elements to a list of background items.
     *
     * @param bgItems
     *            String of Background Items.
     * @param type
     *            Type of Background Item.
     * @return List of Background Items.
     */
    private List<BackgroundItem> convertStringToBackgroundItems(final String bgItems, final BackgroundItemType type)
    {
        ArrayList<BackgroundItem> results = new ArrayList<BackgroundItem>();

        String[] bgItemsArray = bgItems.split(",");

        for (String bgItem : bgItemsArray)
        {
            if (!bgItem.trim().isEmpty())
            {
                results.add(new BackgroundItem(bgItem.trim(), type));
            }
        }

        return results;

    }

}
