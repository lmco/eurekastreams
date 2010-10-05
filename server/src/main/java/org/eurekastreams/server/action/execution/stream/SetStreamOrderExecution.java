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
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.ReorderStreams;

/**
 * Reorders the streams displayed on the activity page..
 * 
 */
public class SetStreamOrderExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper used to retrieve and save the page that holds the tabs.
     */
    private final FindByIdMapper<Person> personMapper;

    /**
     * The reorder mapper.
     */
    private ReorderStreams reorderMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            injecting the mapper
     * @param inReorderMapper
     *            the reorder mapper.
     */
    public SetStreamOrderExecution(final FindByIdMapper<Person> inPersonMapper, final ReorderStreams inReorderMapper)
    {
        personMapper = inPersonMapper;
        reorderMapper = inReorderMapper;
    }

    /**
     * {@inheritDoc}. Move the Stream view order on the Activity page.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("entering");
        SetStreamOrderRequest request = (SetStreamOrderRequest) inActionContext.getParams();

        Person person = personMapper.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));

        List<Stream> streams = person.getStreams();

        // Find the tab to be moved
        int oldIndex = -1;

        for (int i = 0; i < streams.size(); i++)
        {
            if (streams.get(i).getId() == request.getStreamId())
            {
                log.debug("Found item at index: " + i);
                oldIndex = i;
                break;
            }
        }

        Stream movingStream = streams.get(oldIndex);

        // move the tab
        streams.remove(oldIndex);
        streams.add(request.getNewIndex(), movingStream);

        reorderMapper.execute(person.getId(), streams, request.getHiddenLineIndex());

        return Boolean.TRUE;
    }

}
