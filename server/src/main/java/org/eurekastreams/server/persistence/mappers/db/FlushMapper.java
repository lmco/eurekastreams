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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Mapper used to update entities by calling flush. This takes persistence request so it can be easily swapped out with
 * InsertMapper for generic create/update actions.
 * 
 */
@SuppressWarnings("unchecked")
public class FlushMapper extends BaseArgDomainMapper<PersistenceRequest, Boolean>
{
    @Override
    public Boolean execute(final PersistenceRequest inRequest)
    {
        flush();
        return true;
    }

}
