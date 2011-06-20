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

import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;

/**
 * Transform MembershipCriteria entities to DTOs.
 * 
 */
public class MembershipCriteriaListToMembershipCriteriaDTOListTransformer implements
        Transformer<List<MembershipCriteria>, List<MembershipCriteriaDTO>>
{

    /**
     * Transform MembershipCriteria entities to DTOs.
     * 
     * @param inTransformType
     *            List of MembershipCriteria
     * @return List of MembershipCriteriaDTOs.
     */
    @Override
    public List<MembershipCriteriaDTO> transform(final List<MembershipCriteria> inTransformType)
    {
        List<MembershipCriteriaDTO> results = new ArrayList<MembershipCriteriaDTO>();

        for (MembershipCriteria mc : inTransformType)
        {
            MembershipCriteriaDTO mcdto = new MembershipCriteriaDTO();
            mcdto.setId(mc.getId());
            mcdto.setCriteria(mc.getCriteria());

            GalleryTabTemplate mcgtt = mc.getGalleryTabTemplate();
            if (mcgtt != null)
            {
                mcdto.setGalleryTabTemplateId(mcgtt.getId());
                mcdto.setGalleryTabTemplateName(mcgtt.getTitle());
            }

            Theme mct = mc.getTheme();
            if (mct != null)
            {
                mcdto.setThemeId(mct.getId());
                mcdto.setThemeName(mct.getName());
            }

            results.add(mcdto);
        }

        return results;
    }
}
