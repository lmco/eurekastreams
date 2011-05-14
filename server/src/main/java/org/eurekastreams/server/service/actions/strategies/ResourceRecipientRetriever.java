/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * RecipientRetriever for activities posted to resources.
 */
public class ResourceRecipientRetriever implements RecipientRetriever
{
    /**
     * Mapper to get Shared Resource.
     */
    private DomainMapper<SharedResourceRequest, SharedResource> streamResourceByUniqueKeyMapper;

    /**
     * Constructor.
     * 
     * @param inStreamResourceByUniqueKeyMapper
     *            Mapper to get Shared Resource
     */
    public ResourceRecipientRetriever(
            final DomainMapper<SharedResourceRequest, SharedResource> inStreamResourceByUniqueKeyMapper)
    {
        streamResourceByUniqueKeyMapper = inStreamResourceByUniqueKeyMapper;
    }

    @Override
    public StreamScope getStreamScope(final ActivityDTO inActivityDTO)
    {
        return streamResourceByUniqueKeyMapper.execute(
                new SharedResourceRequest(inActivityDTO.getDestinationStream().getUniqueIdentifier(), null))
                .getStreamScope();
    }

    @Override
    public Boolean isDestinationStreamPublic(final ActivityDTO inActivityDTO)
    {
        return true;
    }
}
