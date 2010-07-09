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
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindUserStreamViewById;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;

/**
 * This Authorization Strategy ensures that the user making the request owns the stream view
 * that is being updated.
 *
 */
public class UpdateStreamViewAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * Find by id DAO for {@link StreamView}.
     */
    private final FindUserStreamViewById streamViewDAO;

    /**
     * Constructor.
     * @param inStreamViewDAO - instance of {@link FindUserStreamViewById} mapper.
     */
    public UpdateStreamViewAuthorization(final FindUserStreamViewById inStreamViewDAO)
    {
        streamViewDAO = inStreamViewDAO;
    }

    /**
     * {@inheritDoc}.
     *
     * Ensure that the user making the request, owns the streamview being updated.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();
        try
        {
            StreamView sv = streamViewDAO
                    .execute(new FindUserStreamFilterByIdRequest(
                            inActionContext.getPrincipal().getId(),
                            ((Long) fields.get("id")).longValue()));
            if (sv.getType() != null)
            {
                throw new AuthorizationException(
                        "Insuffient permissions to modify read-only object.");
            }
        }
        catch (NoResultException nre)
        {
            throw new AuthorizationException(
                    "Insuffient permissions to modify object.");
        }
    }

}
