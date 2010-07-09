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
package org.eurekastreams.server.action.execution.gallery;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemPopulator;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemProvider;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemSaver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for PersistGalleryItemExecutionStrategy.
 */
public class PersistGalleryItemExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemProvider<PluginDefinition> pluginItemGetter = context.mock(GalleryItemProvider.class,
            "pluginPro");

    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemPopulator<PluginDefinition> pluginItemPopulator = context.mock(GalleryItemPopulator.class,
            "pluginPop");
    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemSaver<PluginDefinition> pluginItemSaver = context.mock(GalleryItemSaver.class, "pluginSaver");
    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemProvider<Theme> galleryItemGetter = context.mock(GalleryItemProvider.class);

    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemPopulator<Theme> galleryItemPopulator = context.mock(GalleryItemPopulator.class);

    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemSaver<Theme> galleryItemSaver = context.mock(GalleryItemSaver.class);

    /**
     * The mock mapper to be used by the action.
     */
    private GalleryItemCategoryMapper galleryItemCategoryMapper = context.mock(GalleryItemCategoryMapper.class);

    /**
     * Subject under test.
     */
    private PersistGalleryItemExecution<Theme> sutTheme = null;

    /**
     * sut for plugins.
     */
    private PersistGalleryItemExecution<PluginDefinition> sutPlugin = null;

    /**
     * The mock user information from the session.
     */
    private ServiceActionContext userDetails = context.mock(ServiceActionContext.class);

    /**
     *
     */
    @Before
    public final void setup()
    {
        sutTheme = new PersistGalleryItemExecution<Theme>(galleryItemGetter, galleryItemPopulator, galleryItemSaver,
                galleryItemCategoryMapper, GalleryItemType.THEME);

        sutPlugin = new PersistGalleryItemExecution<PluginDefinition>(pluginItemGetter, pluginItemPopulator,
                pluginItemSaver, galleryItemCategoryMapper, GalleryItemType.PLUGIN);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testExecuteWithUrl() throws Exception
    {
        final String themeName = "Las Vegas";
        final String themeDesc = "Las Vegas sky line";
        final String personName = "Phil Plait";
        final String personEmail = "phil.plait@awesome.com";
        final String themeUrl = "src/main/webapp/themes/vegas.xml";
        final String themeCategory = "CITY";

        final Theme theme = new Theme(themeUrl, themeName, themeDesc, null, null, null, personName, personEmail);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("url", themeUrl);
        formData.put("category", themeCategory);
        final GalleryItemCategory galleryItemCategory = new GalleryItemCategory(themeCategory);

        final String url = themeUrl;
        final String category = themeCategory;

        final Map<String, Serializable> fields = new HashMap<String, Serializable>();
        fields.put("url", url);
        fields.put("category", category);

        context.checking(new Expectations()
        {
            {
                oneOf(userDetails).getParams();
                will(returnValue(fields));

                oneOf(galleryItemGetter).provide(userDetails, formData);
                will(returnValue(theme));

                oneOf(galleryItemPopulator).populate(theme, url);

                oneOf(galleryItemCategoryMapper).findByName(GalleryItemType.THEME, category);
                will(returnValue(galleryItemCategory));

                oneOf(galleryItemSaver).save(theme);
            }
        });

        // Make the call
        Theme actual = (Theme) sutTheme.execute(userDetails);

        context.assertIsSatisfied();

        assertSame(theme, actual);
        assertEquals("property should be gotten", themeName, theme.getName());
        assertEquals("property should be gotten", themeDesc, theme.getDescription());
        assertEquals("property should be gotten", personName, theme.getAuthorName());
        assertEquals("property should be gotten", personEmail, theme.getAuthorEmail());
        assertEquals("property should be gotten", themeUrl, theme.getUrl());
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testExecuteWithPlugin() throws Exception
    {

        final GalleryItemCategory galleryItemCategory = new GalleryItemCategory("CITY");

        final String urlPath = "src/main/webapp/plugin/plugin.xml";

        final PluginDefinition pluginDef = new PluginDefinition();
        pluginDef.setUpdateFrequency(9L);
        pluginDef.setUrl(urlPath);
        pluginDef.setCategory(galleryItemCategory);

        final String username = "validuser";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("url", urlPath);
        formData.put("category", "CITY");

        context.checking(new Expectations()
        {
            {
                oneOf(userDetails).getParams();
                will(returnValue(formData));

                oneOf(pluginItemGetter).provide(userDetails, formData);
                will(returnValue(pluginDef));

                oneOf(galleryItemCategoryMapper).findByName(GalleryItemType.PLUGIN, "CITY");
                will(returnValue(galleryItemCategory));

                oneOf(pluginItemPopulator).populate(pluginDef, urlPath);

                oneOf(pluginItemSaver).save(pluginDef);
            }
        });

        Serializable[] params = { formData };

        // Make the call
        PluginDefinition actual = (PluginDefinition) sutPlugin.execute(userDetails);

        context.assertIsSatisfied();

        assertEquals("property should be gotten", new Long(9), pluginDef.getUpdateFrequency());
        assertEquals("property should be gotten", urlPath, pluginDef.getUrl());
    }
}
