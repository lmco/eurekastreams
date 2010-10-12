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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.OrganizationMapper;

/**
 * Organization Creator.
 *
 */
public class OrganizationCreator extends OrganizationPersister
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(OrganizationCreator.class);

    /**
     * Key value for organization shortName.
     */
    private static final String SHORTNAME_KEY = "shortName";

    /**
     * Message for duplicate organization short name.
     */
    private static final String DUP_SHORTNAME_MSG = "Organization short name already present in system.";

    /** Factory for creating traversers. */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * Constructor.
     *
     * @param inOrganizationMapper
     *            The org mapper.
     */
    public OrganizationCreator(final OrganizationMapper inOrganizationMapper)
    {
        super(inOrganizationMapper);
    }

    /**
     * Returns Organization based on id passed in inFields.
     *
     * @param inActionContext
     *            The action context
     * @param inFields
     *            the property map.
     * @return Organization base on id passed in inFields.
     */
    @Override
    public Organization get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        // create the org
        Organization org = new Organization();

        Organization parentOrg = null;
        String parentOrgName = (String) inFields.get("orgParent");

        if (parentOrgName.equals(""))
        {
            parentOrg = getOrgMapper().getRootOrganization();
        }
        else
        {
            parentOrg = getOrgMapper().findByShortName(parentOrgName);
        }

        org.setParentOrganization(parentOrg);

        StreamScope orgScope = new StreamScope(ScopeType.ORGANIZATION, (String) inFields.get("shortName"));

        org.setStreamScope(orgScope);

        Set<StreamScope> defaultScopeList = new HashSet<StreamScope>();
        defaultScopeList.add(orgScope);

        org.setCapabilities(new ArrayList<BackgroundItem>());

        return org;
    }

    /**
     * Persists modified object.
     *
     * @param inActionContext
     *            the action context
     * @param inOrganization
     *            The organization.
     * @throws Exception
     *             on error
     */
    @Override
    protected void persistOrg(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Organization inOrganization) throws Exception
    {
        // Verify that org with given short name doesn't already exist.
        ValidationException validationException = new ValidationException();

        if (getOrgMapper().findByShortName(inOrganization.getShortName()) != null)
        {
            validationException.addError(SHORTNAME_KEY, DUP_SHORTNAME_MSG);
        }

        if (!validationException.getErrors().isEmpty())
        {
            throw validationException;
        }

        getOrgMapper().insert(inOrganization);

        // update the statistics with the new count
        getOrgMapper().updateChildOrganizationCount(inOrganization.getParentOrganization());
        
        // sets the destination entity id for the organization's stream scope
        inOrganization.getStreamScope().setDestinationEntityId(inOrganization.getId());

        // kick off an async action to update the coordinators' activity search
        // strings
        log.info("Queuing up async job to update activity search " + "strings for coordinators of org #"
                + inOrganization.getId());
        queueAsyncAction(inActionContext, inOrganization, true, null);
    }
}
