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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Reorders the stream searches that are displayed for a user on the Activity page.
 *
 */
public class SetStreamSearchOrderExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper used to retrieve and save the page that holds the searches.
     */
    private final PersonMapper personMapper;

    /**
     * Default constructor for the {@link SetStreamSearchOrderExecution} class.
     * @param inPersonMapper - instance of the person mapper for this strategy.
     */
    public SetStreamSearchOrderExecution(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * {@inheritDoc}.
     * Move the search instance within the list of searches.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("entering stream search reorder action execution");
        SetStreamFilterOrderRequest request = (SetStreamFilterOrderRequest) inActionContext.getParams();

        Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

        List<StreamSearch> searches = person.getStreamSearches();

        // Find the search to be moved
        int oldIndex = -1;

        for (int i = 0; i < searches.size(); i++)
        {
            if (searches.get(i).getId() == request.getFilterId())
            {
                oldIndex = i;
            }
        }

        StreamSearch movingView = searches.get(oldIndex);

        // move the search
        searches.remove(oldIndex);
        searches.add(request.getNewIndex(), movingView);

        person.setStreamSearchHiddenLineIndex(request.getHiddenLineIndex());

        personMapper.flush();

        return Boolean.TRUE;
    }

}
