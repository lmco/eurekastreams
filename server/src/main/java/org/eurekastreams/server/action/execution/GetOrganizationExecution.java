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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.service.actions.strategies.OrganizationLoader;

/**
 * Gets an organization by shortname.
 *
 */
public class GetOrganizationExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * The mapper that will get the organization.
     */
    private OrganizationMapper mapper;

    /**
     * Decorator used for loading Organization.
     */
    private OrganizationLoader organizationLoader;

    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to retrieve the banner id if it is not directly configured.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdStrategy;

    /**
     * Constructor.
     *
     * @param orgmapper
     *            injecting the mapper
     * @param inGetBannerIdStrategy
     *            instance of the {@link GetBannerIdByParentOrganizationStrategy}.
     */
    public GetOrganizationExecution(final OrganizationMapper orgmapper,
            final GetBannerIdByParentOrganizationStrategy inGetBannerIdStrategy)
    {
        mapper = orgmapper;
        getBannerIdStrategy = inGetBannerIdStrategy;
    }

    /**
     * Gets an organization by shortname.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return {@link Organization} represented by given shortname.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        String orgShortName = (String) inActionContext.getParams();

        log.debug("Getting organization: " + orgShortName);

        // get the requested org
        Organization outOrg;

        if (orgShortName == null || orgShortName.equals(""))
        {
            log.debug("No shortname provided, getting root organization.");
            outOrg = mapper.getRootOrganization();
            log.debug("Got root organization: " + outOrg.getShortName());
        }
        else
        {
            outOrg = mapper.findByShortName(orgShortName);
        }

        outOrg.getCapabilities().size();

        // populate person collections (leaders/coordinators via loaders.
        if (organizationLoader != null)
        {
            organizationLoader.load(outOrg);

            outOrg.setBannerEntityId(outOrg.getId());
            if (outOrg.getBannerId() == null)
            {
                getBannerIdStrategy.getBannerId(outOrg.getParentOrgId(), outOrg);
            }
        }

        return outOrg;
    }

    /**
     * @param inOrganizationLoader
     *            the organizationLoader to set
     */
    public void setOrganizationLoader(final OrganizationLoader inOrganizationLoader)
    {
        organizationLoader = inOrganizationLoader;
    }

}
