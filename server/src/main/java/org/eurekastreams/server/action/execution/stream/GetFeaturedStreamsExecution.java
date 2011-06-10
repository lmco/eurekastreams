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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.strategies.FeaturedStreamDTOTransientDataPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Return all {@link FeaturedStreamDTO}s.
 * 
 */
public class GetFeaturedStreamsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to retrieve featured stream DTOs.
     */
    private DomainMapper<MapperRequest, List<FeaturedStreamDTO>> featuredStreamDTOMapper;

    /**
     * {@link FeaturedStreamDTOTransientDataPopulator}.
     */
    private FeaturedStreamDTOTransientDataPopulator transientDataPopulator;

    /**
     * Constructor.
     * 
     * @param inFeaturedStreamDTOMapper
     *            Mapper to retrieve featured stream DTOs.
     * @param inTransientDataPopulator
     *            {@link FeaturedStreamDTOTransientDataPopulator}.
     */
    public GetFeaturedStreamsExecution(
            final DomainMapper<MapperRequest, List<FeaturedStreamDTO>> inFeaturedStreamDTOMapper,
            final FeaturedStreamDTOTransientDataPopulator inTransientDataPopulator)
    {
        featuredStreamDTOMapper = inFeaturedStreamDTOMapper;
        transientDataPopulator = inTransientDataPopulator;
    }

    /**
     * Return all {@link FeaturedStreamDTO}s.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return All {@link FeaturedStreamDTO}s.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        return new ArrayList<FeaturedStreamDTO>(transientDataPopulator.execute(inActionContext.getPrincipal().getId(),
                featuredStreamDTOMapper.execute(null)));
    }

}
