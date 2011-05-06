/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;

/**
 * Strategy to extract the list of people ids that shared a shared resource, given a shared resource dto.
 */
public class SharedResourceDTOToSharerPeopleIdsTransformer implements Transformer<SharedResourceDTO, List<Long>>
{
    /**
     * Transform the input SharedResourceDTO to a list of IDs of people that sharer it.
     * 
     * @param inSharedResource
     *            the shared resource to transform
     * @return a list of people ids that shared the shared resource - never null
     */
    @Override
    public List<Long> transform(final SharedResourceDTO inSharedResource)
    {
        if (inSharedResource == null || inSharedResource.getSharerPersonIds() == null)
        {
            return new ArrayList<Long>();
        }
        return inSharedResource.getSharerPersonIds();
    }
}
