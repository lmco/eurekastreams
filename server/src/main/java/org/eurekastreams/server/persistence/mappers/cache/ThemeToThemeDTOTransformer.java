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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.ThemeDTO;

/**
 * Transform Theme to ThemeDTO.
 * 
 */
public class ThemeToThemeDTOTransformer implements Transformer<List<Theme>, List<ThemeDTO>>
{

    /**
     * Transform MembershipCriteria entities to DTOs.
     * 
     * @param inTransformType
     *            List of MembershipCriteria
     * @return List of MembershipCriteriaDTOs.
     */
    @Override
    public List<ThemeDTO> transform(final List<Theme> inTransformType)
    {
        List<ThemeDTO> results = new ArrayList<ThemeDTO>();

        for (Theme theme : inTransformType)
        {
            ThemeDTO dto = new ThemeDTO();
            dto.setId(theme.getId());
            dto.setName(theme.getName());

            results.add(dto);
        }

        return results;
    }
}