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
import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedPersonModelViewCacheMapper;
import org.eurekastreams.server.persistence.mappers.db.ReorderStreamsDbMapper;

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
     * Mapper used to retrieve the ordered list of PersonStreams for a person by id.
     */
    private final DomainMapper<Long, List<PersonStream>> getOrderedPersonStreamListForPersonByIdMapper;

    /**
     * The reorder mapper.
     */
    private ReorderStreamsDbMapper reorderMapper;

    /**
     * mapper to remove a personmodelview from cache by person id.
     */
    private RemoveCachedPersonModelViewCacheMapper removeCachedPersonModelViewByIdCacheMapper;

    /**
     * Constructor.
     * 
     * @param inGetOrderedPersonStreamListForPersonByIdMapper
     *            injecting the mapper
     * @param inReorderMapper
     *            the reorder mapper.
     * @param inRemoveCachedPersonModelViewByIdCacheMapper
     *            mapper to remove cached personmodelview
     */
    public SetStreamOrderExecution(
            final DomainMapper<Long, List<PersonStream>> inGetOrderedPersonStreamListForPersonByIdMapper,
            final ReorderStreamsDbMapper inReorderMapper,
            final RemoveCachedPersonModelViewCacheMapper inRemoveCachedPersonModelViewByIdCacheMapper)
    {
        getOrderedPersonStreamListForPersonByIdMapper = inGetOrderedPersonStreamListForPersonByIdMapper;
        reorderMapper = inReorderMapper;
        removeCachedPersonModelViewByIdCacheMapper = inRemoveCachedPersonModelViewByIdCacheMapper;
    }

    /**
     * {@inheritDoc}. Move the Stream view order on the Activity page.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("entering");
        SetStreamOrderRequest request = (SetStreamOrderRequest) inActionContext.getParams();
        Long personId = inActionContext.getPrincipal().getId();
        List<PersonStream> streams = getOrderedPersonStreamListForPersonByIdMapper.execute(personId);

        // Find the tab to be moved
        int oldIndex = -1;

        for (int i = 0; i < streams.size(); i++)
        {
            if (streams.get(i).getStreamId() == request.getStreamId())
            {
                log.debug("Found item at index: " + i);
                oldIndex = i;
                break;
            }
        }

        PersonStream movingStream = streams.get(oldIndex);

        // move the tab
        streams.remove(oldIndex);
        streams.add(request.getNewIndex(), movingStream);

        // now have a list the way we want it - let the reordermapper commit it
        reorderMapper.execute(personId, streams, request.getHiddenLineIndex());

        // remove the person from cache
        removeCachedPersonModelViewByIdCacheMapper.execute(personId);

        return Boolean.TRUE;
    }
}
