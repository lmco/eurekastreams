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
import java.util.ArrayList;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for CreateGalleryTabTemplateExecution.
 * 
 */
public class CreateGalleryTabTemplateExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to find tab by id.
     */
    private DomainMapper<FindByIdRequest, Tab> findTabByIdMapper = context
            .mock(DomainMapper.class, "findTabByIdMapper");

    /**
     * Mapper to find GalleryTabTemplate by id.
     */
    private DomainMapper<FindByIdRequest, GalleryTabTemplate> getGalleryTabTemplateByIdMapper = context.mock(
            DomainMapper.class, "getGalleryTabTemplateByIdMapper");

    /**
     * Mapper used to look up the theme category.
     */
    private GalleryItemCategoryMapper galleryItemCategoryMapper = context.mock(GalleryItemCategoryMapper.class,
            "galleryItemCategoryMapper");

    /**
     * Mapper for persisting GalleryTabTemplate.
     */
    private DomainMapper<PersistenceRequest<GalleryTabTemplate>, Boolean> insertMapper = context.mock(
            DomainMapper.class, "insertMapper");

    /**
     * System under test.
     */
    private CreateGalleryTabTemplateExecution sut = new CreateGalleryTabTemplateExecution(findTabByIdMapper,
            getGalleryTabTemplateByIdMapper, galleryItemCategoryMapper, insertMapper);

    /**
     * Mock tab.
     */
    private Tab mockTab = context.mock(Tab.class);

    /**
     * Mock TabTemplate.
     */
    private TabTemplate mockTabTemplate = context.mock(TabTemplate.class);

    /**
     * Mock GalleryTabTemplate.
     */
    private GalleryTabTemplate mockGalleryTabTemplate = context.mock(GalleryTabTemplate.class);

    /**
     * Mock GalleryItemCategory.
     */
    private GalleryItemCategory mockGalleryItemCategory = context.mock(GalleryItemCategory.class);

    /**
     * The mock user information from the session.
     */
    private ServiceActionContext actionContext = context.mock(ServiceActionContext.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("description", "description goes here");
        formData.put("category", "BLAH");
        formData.put("tab", "5");

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(formData));

                oneOf(getGalleryTabTemplateByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(mockGalleryTabTemplate));

                oneOf(findTabByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(mockTab));

                oneOf(mockTab).getTemplate();
                will(returnValue(mockTabTemplate));

                allowing(mockTabTemplate).getTabName();
                will(returnValue("tab name"));

                oneOf(mockTabTemplate).getTabLayout();
                will(returnValue(Layout.THREECOLUMN));

                oneOf(mockTabTemplate).getGadgets();
                will(returnValue(new ArrayList<Gadget>()));

                oneOf(galleryItemCategoryMapper).findByName(GalleryItemType.TAB, "BLAH");
                will(returnValue(mockGalleryItemCategory));

                oneOf(mockGalleryTabTemplate).setCategory(mockGalleryItemCategory);
                oneOf(mockGalleryTabTemplate).setDescription("description goes here");
                oneOf(mockGalleryTabTemplate).setTabTemplate(with(any(TabTemplate.class)));
                oneOf(mockGalleryTabTemplate).setTitle("tab name");

                oneOf(insertMapper).execute(with(any(PersistenceRequest.class)));
                will(returnValue(true));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
