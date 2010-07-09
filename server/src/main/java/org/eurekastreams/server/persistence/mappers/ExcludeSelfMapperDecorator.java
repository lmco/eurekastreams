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

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator to remove the ID used for lookup from the returned list. (Intended for excluding a person from their list
 * of followed people.)
 */
public class ExcludeSelfMapperDecorator implements DomainMapper<Long, List<Long>>
{
    /** Decorated mapper. */
    private DomainMapper<Long, List<Long>> mapper;

    /**
     * Constructor.
     *
     * @param inMapper
     *            Decorated mapper.
     */
    public ExcludeSelfMapperDecorator(final DomainMapper<Long, List<Long>> inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> execute(final Long inRequest)
    {
        List<Long> fullList = mapper.execute(inRequest);
        if (fullList.isEmpty())
        {
            return fullList;
        }
        else
        {
            // We can't assume the list being returned is mutable or that it "belongs" to us to alter, so create a new
            // list. We do expect that the list will contain the input ID, so we don't scan for it first. But we
            // preallocate the full size just in case it isn't.
            List<Long> newList = new ArrayList<Long>(fullList.size());
            for (Long id : fullList)
            {
                if (!id.equals(inRequest))
                {
                    newList.add(id);
                }
            }
            return newList;
        }
    }
}
