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

import java.util.Collections;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Adds tab from gallery to user's start page.
 * 
 */
public class AddTabFromGalleryTabTemplateIdExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Find by id mapper.
     */
    private DomainMapper findById;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inFindById
     *            Find by id mapper.
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public AddTabFromGalleryTabTemplateIdExecution(final DomainMapper inFindById,
            final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        findById = inFindById;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * Add tab to user's start page based on the GalleryTabTemplate id passed in. NOTE: This creates a copy of the
     * TabTemplate, the new tab is not linked to original TabTemplate id passed in.
     * 
     * @param inActionContext
     *            The action context.
     * 
     * @return True if successful.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(final PrincipalActionContext inActionContext)
    {
        Person currentUser = (Person) findById.execute(new FindByIdRequest("Person", inActionContext.getPrincipal()
                .getId()));

        GalleryTabTemplate gtt = (GalleryTabTemplate) findById.execute(new FindByIdRequest("GalleryTabTemplate",
                (Long) inActionContext.getParams()));

        TabTemplate newTabTemplate = new TabTemplate(gtt.getTabTemplate());
        newTabTemplate.setGalleryTabTemplate(gtt);
        for (Gadget gadget : newTabTemplate.getGadgets())
        {
            gadget.setOwner(currentUser);
        }

        currentUser.getStartTabGroup().addTab(new Tab(newTabTemplate));

        deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + currentUser.getId()));

        return true;
    }
}
