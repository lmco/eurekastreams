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
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Upload servlet for person avatar.
 *
 */
@SuppressWarnings("serial")
public class UploadPersonAvatarServlet extends UploadAvatarServlet<Person>
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
    protected Person getDomainEntity(final String inName, final HttpServletRequest request) throws ServletException
    {
        PersonMapper personMapper = (PersonMapper) getSpringContext().getBean("jpaPersonMapper");

        Person outPerson = personMapper.findByAccountId(inName);

        if (outPerson == null)
        {
            throw new ServletException("User:  " + inName + " not found");
        }
        return outPerson;
    }


    /**
     * Gets the action.
     * @param request the request.
     * @return the action
     */
    @Override
    protected TaskHandlerServiceAction getAction(final HttpServletRequest request)
    {
        return (TaskHandlerServiceAction) getSpringContext().getBean("savePersonAvatar");
    }
}
