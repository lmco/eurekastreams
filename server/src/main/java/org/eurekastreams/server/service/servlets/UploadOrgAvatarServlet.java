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
package org.eurekastreams.server.service.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.OrganizationMapper;

/**
 * Upload servlet for org avatar.
 *
 */
@SuppressWarnings("serial")
public class UploadOrgAvatarServlet extends UploadAvatarServlet<Organization>
{
    /**
     * Gets the domain entity.
     *
     * @param inName
     *            the name used to find the domain entity
     * @param request
     *            the http request object.
     * @return the domain entity
     *
     * @throws ServletException
     *             when anything goes wrong
     */
    @Override
    protected Organization getDomainEntity(final String inName, final HttpServletRequest request)
            throws ServletException
    {

        OrganizationMapper mapper = (OrganizationMapper) getSpringContext().getBean("jpaOrganizationMapper");

        Organization org = mapper.findByShortName(request.getParameter("orgName"));

        if (org == null)
        {
            throw new ServletException("Org:  " + inName + " not found");
        }

        return org;

    }


    /**
     * Gets the action.
     * @param request the request.
     * @return the action
     */
    @Override
    protected TaskHandlerServiceAction getAction(final HttpServletRequest request)
    {
        return (TaskHandlerServiceAction) getSpringContext().getBean("saveOrgAvatar");
    }
}
