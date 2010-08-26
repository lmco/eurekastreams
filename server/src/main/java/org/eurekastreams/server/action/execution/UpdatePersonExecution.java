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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.BackgroundMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;
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
     * Organization mapper.
     */
    private final OrganizationMapper orgMapper;

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
     * The organization hierarchy traverser builder - needed because this class is reused by all threads, we can't share
     * OrganizationHierarchyTraversers.
     */
    private final OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * Constructor.
     * 
     * @param inOrgMapper
     *            - instance of {@link OrganizationMapper} for this execution strategy.
     * @param inPersonMapper
     *            - instance of {@link PersonMapper} for this execution strategy.
     * @param inPersonPersister
     *            - instance of {@link PersistResourceExecution} for a Person.
     * @param inBackgroundMapper
     *            - instance of {@link BackgroundMapper} for this execution strategy.
     * @param inOrgTraverserBuilder
     *            {@link OrganizationHierarchyTraverserBuilder}.
     */
    public UpdatePersonExecution(final OrganizationMapper inOrgMapper, final PersonMapper inPersonMapper,
            final TaskHandlerExecutionStrategy inPersonPersister, final BackgroundMapper inBackgroundMapper,
            final OrganizationHierarchyTraverserBuilder inOrgTraverserBuilder)
    {
        orgMapper = inOrgMapper;
        personMapper = inPersonMapper;
        personPersister = inPersonPersister;
        backgroundMapper = inBackgroundMapper;
        orgTraverserBuilder = inOrgTraverserBuilder;
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

        String newParentOrgName = (String) fields.get(PersonModelView.ORG_PARENT_KEY);

        List<String> relatedOrgShortNames = (List<String>) fields.get(PersonModelView.RELATED_ORG_KEY);
        List<Organization> relatedOrgs = new ArrayList<Organization>();

        for (String orgName : relatedOrgShortNames)
        {
            relatedOrgs.add(orgMapper.findByShortName(orgName));
        }

        fields.remove(PersonModelView.ORG_PARENT_KEY);
        fields.remove(PersonModelView.RELATED_ORG_KEY);

        Person person = (Person) personPersister.execute(inActionContext);

        String origParentOrgName = person.getParentOrganization().getShortName();

        List<BackgroundItem> skills = convertStringToBackgroundItems((String) fields.get(PersonModelView.SKILLS_KEY),
                BackgroundItemType.SKILL);

        Background background = backgroundMapper.findOrCreatePersonBackground(person.getOpenSocialId());
        background.setBackgroundItems(skills, BackgroundItemType.SKILL);

        // if parent org has changed, update person and update stats for both original and new orgs.
        if (origParentOrgName.compareToIgnoreCase(newParentOrgName) != 0)
        {
            Organization newParentOrg = orgMapper.findByShortName(newParentOrgName);
            Organization origParentOrg = orgMapper.findByShortName(origParentOrgName);

            if (null == newParentOrg)
            {
                // Parent org cannot be null.
                throw new ValidationException();
            }

            person.setParentOrganization(newParentOrg);
            updateOrgStatistics(newParentOrg);
            updateOrgStatistics(origParentOrg);
        }

        person.setRelatedOrganizations(relatedOrgs);

        personMapper.flush();

        return person;
    }

    /**
     * Update an organization's stats.
     * 
     * @param inOrganizaiton
     *            the org to update.
     */
    private void updateOrgStatistics(final Organization inOrganizaiton)
    {
        OrganizationHierarchyTraverser orgTraverser = orgTraverserBuilder.getOrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(inOrganizaiton);
        orgMapper.updateOrganizationStatistics(orgTraverser);
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
