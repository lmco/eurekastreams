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
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.persistence.mappers.cache.PopulatePeopleWithSkeletonRelatedOrgsCacheMapper;

/**
 * Strategy to retrieve a person from the database by their id.
 * 
 */
public class GetPersonExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.make();

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper mapper = null;

    /**
     * Mapper to add skeleton parent organizations onto Person objects.
     */
    private final PopulateOrgChildWithSkeletonParentOrgsCacheMapper skeletonParentOrgPopulator;

    /**
     * Mapper to populate a person's related orgs with skeleton organizations from cache.
     */
    private final PopulatePeopleWithSkeletonRelatedOrgsCacheMapper skeletonRelatedOrgsMapper;

    /**
     * Strategy to retrieve the banner id if it is not directly configured.
     */
    private final GetBannerIdByParentOrganizationStrategy getBannerIdStrategy;

    /**
     * Constructor that sets up the mapper.
     * 
     * @param inMapper
     *            - instance of PersonMapper
     * @param inSkeletonRelatedOrgsMapper
     *            mapper to populate a person with skeleton related parent organizations
     * @param inSkeletonParentOrgPopulator
     *            mapper to populate parent org of people with a skeleton org
     * @param inGetBannerIdStrategy
     *            instance of the {@link GetBannerIdByParentOrganizationStrategy}.
     */
    public GetPersonExecution(final PersonMapper inMapper,
            final PopulatePeopleWithSkeletonRelatedOrgsCacheMapper inSkeletonRelatedOrgsMapper,
            final PopulateOrgChildWithSkeletonParentOrgsCacheMapper inSkeletonParentOrgPopulator,
            final GetBannerIdByParentOrganizationStrategy inGetBannerIdStrategy)
    {

        mapper = inMapper;
        skeletonRelatedOrgsMapper = inSkeletonRelatedOrgsMapper;
        skeletonParentOrgPopulator = inSkeletonParentOrgPopulator;
        getBannerIdStrategy = inGetBannerIdStrategy;
    }

    /**
     * Retrieve a person from the database by their id, or current user if id is null.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return Person from the database by their id, or current user if id is null.
     */
    @Override
    public Person execute(final PrincipalActionContext inActionContext)
    {
        Person result = null;
        String identifierParam = (String) inActionContext.getParams();

        // Null parameter indicates request for person from start page so load up current
        // user with tabs/gadgets/tasks. Non-null requests load specified Person and skip
        // tabs/gadgets/tasks loading
        if (identifierParam == null)
        {
            result = mapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

            if (result != null)
            {
                // Trigger loading of the tabs and gadget tasks.
                for (Tab tab : result.getTabs(TabGroupType.START))
                {
                    for (Gadget gadget : tab.getGadgets())
                    {
                        gadget.getGadgetDefinition();
                    }
                }
            }
        }
        else
        {
            result = mapper.findByAccountId(identifierParam);

            if (result != null && result.getBackground() != null)
            {
                result.getBackground().getBackgroundItems(BackgroundItemType.SKILL).size();
            }
        }

        if (result != null)
        {
            if (log.isTraceEnabled())
            {
                log.trace("Attempting to populate skeleton Related Organizations for Person " + result.toString());
            }
            skeletonRelatedOrgsMapper.execute(result);

            if (log.isTraceEnabled())
            {
                log.trace("Attempting to populate skeleton Organizations for Person " + result.toString());
            }

            skeletonParentOrgPopulator.populateParentOrgSkeleton(result);
        }

        // Set the transient banner id on the person with the first parent org that
        // has a banner id configured starting with the direct parent and walking up
        // the tree.
        if (result != null)
        {
            getBannerIdStrategy.getBannerId(result.getParentOrgId(), result);
        }

        return result;
    }

}
