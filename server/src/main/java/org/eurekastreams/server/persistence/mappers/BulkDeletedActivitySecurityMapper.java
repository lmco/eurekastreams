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

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;

/**
 * Returns activity security DTOs for deleted activity.
 */
public class BulkDeletedActivitySecurityMapper implements DomainMapper<List<Long>, List<ActivitySecurityDTO>>
{
    /**
     * Refreshes deleted activity.
     * 
     * @param inRequest
     *            the list of IDs to create deleted security DTOs for.
     * @return the deleted security DTOs.
     */
    public List<ActivitySecurityDTO> execute(final List<Long> inRequest)
    {
        List<ActivitySecurityDTO> deletedDTOs = new ArrayList<ActivitySecurityDTO>();

        for (Long deletedActivityId : inRequest)
        {
            deletedDTOs.add(new ActivitySecurityDTO(deletedActivityId, 0L, false, false));
        }

        return deletedDTOs;
    }
}
