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

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Transformer to create MembershipCriteria persistence request from action context containing MembershipContextDTO.
 */
public class ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer implements
        Transformer<ActionContext, PersistenceRequest<MembershipCriteria>>
{
    // NOTE: Don't refactor these mappers to findById mappers, as these mappers should just be returning proxy objects
    // based on id. No need to hit DB to pull objects back.

    /**
     * Theme mapper.
     */
    private DomainMapper<Long, Theme> themeProxyMapper;

    /**
     * GalleryTabTemplate mapper.
     */
    private DomainMapper<Long, GalleryTabTemplate> galleryTabTemplateProxyMapper;

    /**
     * Constructor.
     * 
     * @param inThemeProxyMapper
     *            Theme mapper.
     * @param inGalleryTabTemplateProxyMappery
     *            GalleryTabTemplate mapper.
     */
    public ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer(
            final DomainMapper<Long, Theme> inThemeProxyMapper,
            final DomainMapper<Long, GalleryTabTemplate> inGalleryTabTemplateProxyMappery)
    {
        themeProxyMapper = inThemeProxyMapper;
        galleryTabTemplateProxyMapper = inGalleryTabTemplateProxyMappery;
    }

    /**
     * create MembershipCriteria persistence request from action context containing MembershipContextDTO.
     * 
     * @param inTransformType
     *            ActionContext containing MembershipCriteriaDTO.
     * @return PersistenceRequest for MembershipCriteria.
     */
    @Override
    public PersistenceRequest<MembershipCriteria> transform(final ActionContext inTransformType)
    {
        MembershipCriteriaDTO mcdto = (MembershipCriteriaDTO) inTransformType.getParams();
        Long themeId = mcdto.getThemeId();
        Long gttId = mcdto.getGalleryTabTemplateId();

        MembershipCriteria mc = new MembershipCriteria();

        mc.setTheme(themeId == null || themeId == -1 ? null : themeProxyMapper.execute(themeId));
        mc.setGalleryTabTemplate(gttId == null || gttId == -1 ? null : galleryTabTemplateProxyMapper.execute(gttId));
        mc.setCriteria(mcdto.getCriteria());

        return new PersistenceRequest<MembershipCriteria>(mc);
    }

}
