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
import java.util.Date;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Accept the terms of service for current user.
 */
public class AcceptTermsOfServiceExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     */
    public AcceptTermsOfServiceExecution(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Accept ToS for current user.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return returns null.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        ((ExtendedUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .setToSAcceptance(true);

        Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
        person.setLastAcceptedTermsOfService(new Date());

        personMapper.flush();

        return null;
    }

}
