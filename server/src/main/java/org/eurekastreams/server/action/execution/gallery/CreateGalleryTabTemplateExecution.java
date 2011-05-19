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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Create and store GalleryTabTemplate.
 * 
 */
public class CreateGalleryTabTemplateExecution implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Category key.
     */
    private static final String CATEGORY_KEY = "category";

    /**
     * Description key.
     */
    private static final String DESCRIPTION_KEY = "description";

    /**
     * Tab key.
     */
    private static final String TAB_KEY = "tab";

    /**
     * GalleryTabTemplate id key.
     */
    private static final String GALLERYTABTEMPLATE_ID_KEY = "id";

    /**
     * Mapper to find tab by id.
     */
    private DomainMapper<FindByIdRequest, Tab> findTabByIdMapper;

    /**
     * Mapper to find GalleryTabTemplate by id.
     */
    private DomainMapper<FindByIdRequest, GalleryTabTemplate> getGalleryTabTemplateByIdMapper;

    /**
     * Mapper used to look up the theme category.
     */
    private GalleryItemCategoryMapper galleryItemCategoryMapper;

    /**
     * Mapper for persisting GalleryTabTemplate.
     */
    private DomainMapper<PersistenceRequest<GalleryTabTemplate>, Boolean> persistMapper;

    /**
     * Constructor.
     * 
     * @param inFindTabByIdMapper
     *            Mapper to find tab by id.
     * @param inGetGalleryTabTemplateByIdMapper
     *            Mapper to find gallery tab template by id.
     * @param inGalleryItemCategoryMapper
     *            Mapper used to look up the theme category.
     * @param inPersistMapper
     *            Mapper for persisting GalleryTabTemplate.
     */
    public CreateGalleryTabTemplateExecution(final DomainMapper<FindByIdRequest, Tab> inFindTabByIdMapper,
            final DomainMapper<FindByIdRequest, GalleryTabTemplate> inGetGalleryTabTemplateByIdMapper,
            final GalleryItemCategoryMapper inGalleryItemCategoryMapper,
            final DomainMapper<PersistenceRequest<GalleryTabTemplate>, Boolean> inPersistMapper)
    {
        findTabByIdMapper = inFindTabByIdMapper;
        getGalleryTabTemplateByIdMapper = inGetGalleryTabTemplateByIdMapper;
        galleryItemCategoryMapper = inGalleryItemCategoryMapper;
        persistMapper = inPersistMapper;
    }

    /**
     * Create new GalleryTabTemplate item based on parameters. NOTE: This creates a new COPY of tab template referenced
     * by incoming tab
     * 
     * @param inActionContext
     *            The action context.
     * @return True on success.
     */
    @Override
    public Serializable execute(final ServiceActionContext inActionContext)
    {
        // convert the params to a map
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        // grab param values.
        String category = (String) fields.get(CATEGORY_KEY);
        String description = (String) fields.get(DESCRIPTION_KEY);

        String tabStringId = (String) fields.get(TAB_KEY);
        Long tabId = StringUtils.isNotEmpty(tabStringId) && !StringUtils.equalsIgnoreCase(tabStringId, "-1") ? Long
                .parseLong(tabStringId) : null;

        String galleryTabTemplateStringId = (String) fields.get(GALLERYTABTEMPLATE_ID_KEY);
        Long galleryTabTemplateId = StringUtils.isNotEmpty(galleryTabTemplateStringId) ? Long
                .parseLong(galleryTabTemplateStringId) : null;

        // find/create the GalleryTabTemplate.
        GalleryTabTemplate gtt = getGalleryTabTemplateByIdMapper.execute(galleryTabTemplateId == null ? null
                : new FindByIdRequest("GalleryTabTemplate", galleryTabTemplateId));

        // if required set the tab template.
        if (tabId != null)
        {
            // look up source tab by id.
            Tab tab = findTabByIdMapper.execute(new FindByIdRequest("Tab", tabId));

            // create new tabTemplate from source.
            TabTemplate newTabTemplate = new TabTemplate(tab.getTemplate());

            gtt.setTabTemplate(newTabTemplate);
            gtt.setTitle(newTabTemplate.getTabName());
        }

        // get/create the category and set it.
        GalleryItemCategory galleryItemCategory = galleryItemCategoryMapper.findByName(GalleryItemType.TAB, category);
        gtt.setCategory(galleryItemCategory);

        // set the description
        gtt.setDescription(description);

        // persist the new GalleryTabTemplate to datastore.
        return persistMapper.execute(new PersistenceRequest<GalleryTabTemplate>(gtt));
    }
}
