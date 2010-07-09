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
package org.eurekastreams.server.action.authorization.stream;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.NoResultException;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.FindUserStreamSearchById;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;

/**
 * This class contains the authorization strategy that determines if the current user making the request
 * owns the streamsearch that is attempting to be updated.
 *
 */
public class UpdateStreamSearchAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * Key to access map for id of StreamSearch to update.
     */
    private static final String ID_KEY = "id";

    /**
     * Instance of the {@link FindUserStreamSearchById} mapper.
     */
    private final FindUserStreamSearchById streamSearchByUserAndIdDAO;

    /**
     * Constructor.
     * @param inStreamSearchByUserAndIdDAO instance of the {@link FindUserStreamSearchById} mapper.
     */
    public UpdateStreamSearchAuthorization(final FindUserStreamSearchById inStreamSearchByUserAndIdDAO)
    {
        streamSearchByUserAndIdDAO = inStreamSearchByUserAndIdDAO;
    }

    /**
     * {@inheritDoc}.
     * Determine if the current user owns the streamsearch that is being updated.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();
        try
        {
            streamSearchByUserAndIdDAO.execute(new FindUserStreamFilterByIdRequest(
                    inActionContext.getPrincipal().getId(),
                    ((Long) fields.get(ID_KEY)).longValue()));

        }
        catch (NoResultException nre)
        {
            throw new AuthorizationException("Insuffient permissions to modify object.");
        }
    }

}
