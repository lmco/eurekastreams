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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.UpdateOrganizationNameRequest;
import org.eurekastreams.server.domain.DomainFormatUtility;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

/**
 * Class to update an Organization.
 * 
 */
public class OrganizationUpdater extends OrganizationPersister
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get org model view from cache (or database if not in cache).
     */
    private GetOrganizationsByIds cachedOrganizationMapper;

    /**
     * key for passing original coordinators in map.
     */
    private static final String ORIG_CORD_IDS_KEY = "__origCoordIds";

    /**
     * Cache mapper to delete activity search strings for people that are coordinators of the organization being
     * updated.
     */
    private ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate
    // line break
    clearActivityStreamSearchStringsForOrgCoordinatorMapper;

    /**
     * Constructor.
     * 
     * @param inOrganizationMapper
     *            The org mapper.
     * @param inPersonMapper
     *            The person mapper.
     * @param inCachedOrganizationMapper
     *            The organization mapper.
     * @param inClearActivityStreamSearchStringsForOrgCoordinatorMapper
     *            the cache mapper to use to clear out the activity search strings for the coordinators of the org being
     *            updated
     */
    public OrganizationUpdater(final OrganizationMapper inOrganizationMapper, final PersonMapper inPersonMapper,
            final GetOrganizationsByIds inCachedOrganizationMapper,
            final ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate
            // line break
            inClearActivityStreamSearchStringsForOrgCoordinatorMapper)
    {
        super(inOrganizationMapper);
        cachedOrganizationMapper = inCachedOrganizationMapper;
        clearActivityStreamSearchStringsForOrgCoordinatorMapper =
        // line break
        inClearActivityStreamSearchStringsForOrgCoordinatorMapper;
    }

    /**
     * Returns Organization base on id passed in inFields.
     * 
     * @param inActionContext
     *            Action context
     * @param inFields
     *            the property map.
     * @return Organization base on id passed in inFields.
     */
    @Override
    public Organization get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        long id = Long.parseLong(inFields.get(OrganizationModelView.ID_KEY).toString());
        Organization entity = getOrgMapper().findById(id);

        Set<Person> originalCoordinators = entity.getCoordinators();
        Set<Long> originalCoordinatorIds = new HashSet<Long>(originalCoordinators.size());
        for (Person p : originalCoordinators)
        {
            originalCoordinatorIds.add(p.getId());
        }
        inFields.put(ORIG_CORD_IDS_KEY, (Serializable) originalCoordinatorIds);

        // clear the activity search strings for all coordinators, in case we're
        // updating the coordinator list
        clearActivityStreamSearchStringsForOrgCoordinatorMapper.execute(id);

        return entity;
    }

    /**
     * This method is different from its parent in that it does not attempt to bootstrap people into the organization.
     * We're just updating, not LDAP work gets done here.
     * 
     * @param inActionContext
     *            The action context
     * @param inFields
     *            The property map.
     * @param inOrganization
     *            The organization.
     * @throws Exception
     *             If error occurs.
     */
    @Override
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final Organization inOrganization) throws Exception
    {
        // Checks to see if the name has changed, and queue up action to update
        // cache if so
        List<Long> ids = new ArrayList<Long>();
        ids.add(inOrganization.getId());
        List<OrganizationModelView> orgs = cachedOrganizationMapper.execute(ids);
        if (orgs.size() > 0 && inFields.containsKey(OrganizationModelView.NAME_KEY)
                && orgs.get(0).getName() != (String) inFields.get(OrganizationModelView.NAME_KEY))
        {
            // puts async action on queue
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("updateCachedOrganizationNameAction", null,
                            new UpdateOrganizationNameRequest(inOrganization.getId(), inOrganization.getName())));
        }

        // TODO there are no keywords from what i can tell in an organization.
        inOrganization.setCapabilities(DomainFormatUtility.splitCapabilitiesString((String) inFields.get("keywords")));

        // call concrete class to persist the org
        persistOrg(inActionContext, inOrganization);

        // kick off an async action to update the coordinators' activity search strings
        queueAsyncAction(inActionContext, inOrganization, (Set<Long>) inFields.get(ORIG_CORD_IDS_KEY));
    }

    /**
     * Persists modified object.
     * 
     * @param inActionContext
     *            the action context
     * @param inOrganization
     *            The organization.
     */
    @Override
    protected void persistOrg(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Organization inOrganization)
    {
        try
        {
            getOrgMapper().flush();
        }
        catch (InvalidStateException e)
        {
            log.error("Failed to persist Organization", e);
            InvalidValue[] invalidValues = e.getInvalidValues();
            ValidationException validationException = new ValidationException();

            for (InvalidValue invalidValue : invalidValues)
            {
                validationException.addError(invalidValue.getPropertyName(), invalidValue.getMessage());
            }

            throw validationException;
        }
        catch (PersistenceException e)
        {
            log.error("Failed to persist Organization", e);
        }
    }
}
