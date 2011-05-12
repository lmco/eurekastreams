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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * This class uploads an avatar.
 * 
 */

@SuppressWarnings("serial")
public class UploadBannerServlet extends UploadImageServlet<DomainEntity>
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(UploadBannerServlet.class);

    /**
     * Gets the domain entity.
     * 
     * @param inName
     *            the name used to find the domain entity
     * @param request
     *            the http request object.
     * @return the domain entity
     * @throws ServletException
     *             when anything goes wrong
     */
    @SuppressWarnings("deprecation")
    @Override
    protected DomainEntity getDomainEntity(final String inName, final HttpServletRequest request)
            throws ServletException
    {
        PlatformTransactionManager transMgr = (PlatformTransactionManager) getSpringContext().getBean(
                "transactionManager");
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("getOrg");
        transDef.setReadOnly(true);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        String type = request.getParameter("type");

        GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorMapper = //
        (GetAllPersonIdsWhoHaveGroupCoordinatorAccess) getSpringContext().getBean(
                "getAllPersonIdsWhoHaveGroupCoordinatorAccess");

        DomainGroupMapper groupMapper = (DomainGroupMapper) getSpringContext().getBean("jpaGroupMapper");

        String entityName = request.getParameter("entityName");

        if (type.equals("DomainGroup"))
        {
            DomainGroup group = groupMapper.findByShortName(entityName);

            if (group == null)
            {
                throw new ServletException("Group:  " + entityName + " not found");
            }

            if (groupCoordinatorMapper.hasGroupCoordinatorAccessRecursively(inName, group.getId()))
            {
                transMgr.commit(transStatus);
                return group;
            }

            throw new ServletException("User " + inName + " is not a coordinator of group:  " + group.getName());
        }

        else
        {

            throw new ServletException("Type of object is " + type + " is not supported");
        }

    }

    /**
     * Gets the file path string.
     * 
     * @param inImageId
     *            the domain entity id
     * @return the file path string
     */
    @Override
    protected String getFilePath(final String inImageId)
    {
        return "n" + inImageId;
    }

    /**
     * Gets the action.
     * 
     * @param request
     *            the request.
     * @return the action
     */
    @Override
    protected ServiceAction getAction(final HttpServletRequest request)
    {
        String type = request.getParameter("type");
        return (ServiceAction) getSpringContext().getBean("update" + type + "Banner");
    }

    /**
     * Gets the response string.
     * 
     * @param inDomainEntity
     *            the entity.
     * @return the response string.
     */
    @Override
    protected String getResponseString(final DomainEntity inDomainEntity)
    {
        // client doesn't need any text to display,
        // but have to return something, so just return an empty string
        return "";
    }
}
