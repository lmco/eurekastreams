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
package org.eurekastreams.server.persistence.mappers;

import java.util.List;

/**
 * Get the list of activity for everyone.
 *
 */
public class GetEveryoneActivityMapper implements DomainMapper<Object, List<Long>>
{
    /**
     * Get the stream id mapper.
     */
    private ChainedDomainMapper<Object, Long> getEveryoneStreamIdMapper;
    /**
     * Get the actual activities.
     */
    private ChainedDomainMapper<Long, List<Long>> getEveryoneActivityMapper;

    /**
     * Default constructor.
     * @param inGetEvenyoneStreamIdMapper the id mapper.
     * @param inGetEveryoneActivityMapper the activity mapper.
     */
    public GetEveryoneActivityMapper(final ChainedDomainMapper<Object, Long> inGetEvenyoneStreamIdMapper,
            final ChainedDomainMapper<Long, List<Long>> inGetEveryoneActivityMapper)
    {
        getEveryoneStreamIdMapper = inGetEvenyoneStreamIdMapper;
        getEveryoneActivityMapper = inGetEveryoneActivityMapper;
    }

    /**
     * Get the ID and feed it into the other mapper.
     * @param inRequest nothing useful.
     * @return the list of activity IDs
     */
    @Override
    public List<Long> execute(final Object inRequest)
    {
        Long id = getEveryoneStreamIdMapper.execute(null);
        return getEveryoneActivityMapper.execute(id);
    }
}
