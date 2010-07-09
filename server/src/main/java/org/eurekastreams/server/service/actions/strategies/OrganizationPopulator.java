/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.execution.PersonLookupUtilityStrategy;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Organization Populator.
 */
public class OrganizationPopulator
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * Organization mapper.
     */
    private OrganizationMapper organizationMapper;

    /**
     * Person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Person lookup action.
     */
    private PersonLookupUtilityStrategy groupAction;

    /**
     * Attribute action.
     */
    private PersonLookupUtilityStrategy attribAction;

    /**
     * Create Person Action factory.
     */
    private CreatePersonActionFactory createPersonActionFactory;

    /**
     * Organization Traverser Builder.
     */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * The organization populator constructor.
     *
     * @param inOrganizationMapper
     *            The organization mapper.
     * @param inPersonMapper
     *            The person mapper.
     * @param inGroupAction
     *            The group action lookup.
     * @param inAttribAction
     *            The attrib action lookup.
     * @param inCreatePersonActionFactory
     *            The create person action factory.
     * @param inOrgTraverserBuilder
     *            The organization hierarchy traverser builder
     */
    public OrganizationPopulator(final OrganizationMapper inOrganizationMapper, final PersonMapper inPersonMapper,
            final PersonLookupUtilityStrategy inGroupAction, final PersonLookupUtilityStrategy inAttribAction,
            final CreatePersonActionFactory inCreatePersonActionFactory,
            final OrganizationHierarchyTraverserBuilder inOrgTraverserBuilder)
    {
        this.createPersonActionFactory = inCreatePersonActionFactory;
        this.organizationMapper = inOrganizationMapper;
        this.personMapper = inPersonMapper;
        this.groupAction = inGroupAction;
        this.attribAction = inAttribAction;
        this.orgTraverserBuilder = inOrgTraverserBuilder;
    }

    /**
     * Create a new person.
     *
     * @param persistResourceExecution
     *            the resource persister for person
     * @param inPerson
     *            The person object.
     * @param inOrganization
     *            The organization object.
     * @param inActionContext
     *            action context
     * @throws Exception
     *             Exception.
     */
    private void createNewPerson(final PersistResourceExecution<Person> persistResourceExecution,
            final Person inPerson, final Organization inOrganization,
            final TaskHandlerActionContext<PrincipalActionContext> inActionContext) throws Exception
    {
        // The person does not exists

        final HashMap<String, Serializable> personData = inPerson.getProperties(Boolean.FALSE);
        personData.put("organization", inOrganization);

        persistResourceExecution.execute(new TaskHandlerActionContext<PrincipalActionContext>(
                new PrincipalActionContext()
                {
                    private static final long serialVersionUID = -8114875921481298742L;

                    @Override
                    public Principal getPrincipal()
                    {
                        throw new RuntimeException("Principal not available.");
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
                }, inActionContext.getUserActionRequests()));

        log.debug("Adding person: " + inPerson.getAccountId());
    }

    /**
     * Given the person already exists, assess the person to see if they should be moved.
     *
     * @param inExistingPerson
     *            The person object.
     * @param inOrganization
     *            The organization object.
     * @param inOrganizationTraverser
     *            The organization traverser.
     */
    private void evaluatePerson(final Person inExistingPerson, final Organization inOrganization,
            final OrganizationHierarchyTraverser inOrganizationTraverser)
    {
        // The person already exists

        // Set their account status to active.
        inExistingPerson.setAccountLocked(false);

        // Check if the person is assigned to the Root Organization (orphaned)
        if (!inExistingPerson.getParentOrganization().equals(organizationMapper.getRootOrganization()))
        {
            // The person is not assigned to the Root Organization
            // Check if the created organization is a child organization of the person's parent organization
            if (inOrganization.getParentOrganization().equals(inExistingPerson.getParentOrganization()))
            {
                // The created organization is a child organization of the person's parent organization
                // Move the person into the created organization

                // Queue up the existing person's org for updated stats
                inOrganizationTraverser.traverseHierarchy(inExistingPerson);

                // Set the parent organization for the person and add this organization
                // to the list of related organizations
                inExistingPerson.setParentOrganization(inOrganization);
                log.debug("Updating person: " + inExistingPerson.getAccountId());
            }

            // Add the created organization to the list of related organizations for this person
            inExistingPerson.addRelatedOrganization(inOrganization);
        }
        else
        {
            // The person is assigned to the Root Organization (orphaned); move the person

            // Queue up the existing person's org for updated stats
            inOrganizationTraverser.traverseHierarchy(inExistingPerson);

            // Set the parent org for the person and add this organization
            // to the list of related organizations
            inExistingPerson.setParentOrganization(inOrganization);
            inExistingPerson.addRelatedOrganization(inOrganization);
            log.debug("Updating person: " + inExistingPerson.getAccountId());
        }
    }

    /**
     *
     * @param ldapQuery
     *            The ldap query string.
     * @param inOrganization
     *            The organization.
     * @param inActionContext
     *            the action context
     * @throws Exception
     *             Exception.
     */
    public void populate(final String ldapQuery, final Organization inOrganization,
            final TaskHandlerActionContext<PrincipalActionContext> inActionContext) throws Exception
    {
        /**
         * Local variable for the organization
         */
        Organization organization = inOrganization;

        PersistResourceExecution<Person> persistResourceExecution = createPersonActionFactory.getCreatePersonAction(
                personMapper, new ReflectiveUpdater());

        // queue up the input org
        OrganizationHierarchyTraverser orgTraverser = orgTraverserBuilder.getOrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(organization);

        PersonLookupUtilityStrategy lookupAction = null;

        // Attribute query assumed if "=" is present
        if (ldapQuery.contains("="))
        {
            lookupAction = attribAction;
        }
        else
        {
            lookupAction = groupAction;
        }

        // Unlimited results
        List<Person> people = lookupAction.getPeople(ldapQuery, Integer.MAX_VALUE);

        for (Person person : people)
        {
            Person existingPerson = personMapper.findByAccountId(person.getAccountId());

            if (existingPerson == null)
            {
                createNewPerson(persistResourceExecution, person, organization, inActionContext);
            }
            else
            {
                evaluatePerson(existingPerson, organization, orgTraverser);
            }
        }
        // Update the statistics for all affected groups
        organizationMapper.updateOrganizationStatistics(orgTraverser);
    }

}
