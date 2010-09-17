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

import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Can the user modify or delete the stream.
 *
 */
public class ModifyStreamForCurrentUserAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to retrieve and save the page that holds the streams.
     */
    private final FindByIdMapper<Person> personMapper;

    /**
     * Request transformer.
     */
    private final RequestTransformer requestTransformer;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            person mapper.
     * @param inRequestTransformer request transformer.
     */
    public ModifyStreamForCurrentUserAuthorization(final FindByIdMapper<Person> inPersonMapper,
            final RequestTransformer inRequestTransformer)
    {
        requestTransformer = inRequestTransformer;
        personMapper = inPersonMapper;
    }

    /**
     * Adds a stream for the current user.
     *
     * @param inActionContext
     *            the action context.
     * @exception AuthorizationException
     *                exception.
     */
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        Person person = personMapper.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));

        inActionContext.getState().put("person", person);

        boolean found = false;
        List<Stream> streams = person.getStreams();
        Long streamId = (Long) requestTransformer.transform(inActionContext);

        for (Stream s : streams)
        {
            if (s.getId() == streamId)
            {
                found = true;
                break;
            }
        }

        if (!found)
        {
            throw new AuthorizationException(inActionContext.getPrincipal().getAccountId()
                    + " cannot modify stream " + streamId);
        }
    }
}
