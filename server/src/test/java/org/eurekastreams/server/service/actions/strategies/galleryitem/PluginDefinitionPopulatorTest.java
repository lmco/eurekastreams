/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.LinkedList;

/**
 * Tests the create person strategy.
 */
public class PluginDefinitionPopulatorTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private PluginDefinitionPopulator sut;

    /**
     * GadgetDefinition Mock.
     */
    private PluginDefinition pluginDefinitionMock = new PluginDefinition();

    /**
     * Mock metadata fetcher.
     */
    private GadgetMetaDataFetcher fetcher = context.mock(GadgetMetaDataFetcher.class);

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new PluginDefinitionPopulator(fetcher, 9L, BaseObjectType.NOTE);
    }

    /**
     * Test the execute method with no metadata returned.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void testExecuteEmpty() throws Exception
    {
        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaData));
            }
        });

        sut.populate(pluginDefinitionMock,  "src/test/resources/plugins/goodplugin.xml");
        assertTrue(pluginDefinitionMock.getObjectType() == BaseObjectType.NOTE);
        assertTrue(pluginDefinitionMock.getUpdateFrequency() == 9L);
        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with no metadata returned.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @Test(expected = ValidationException.class)
    public final void testExecuteFileNotFound() throws Exception
    {
        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaData));
            }
        });

        try
        {
            sut.populate(pluginDefinitionMock,  "src/test/resources/badurlxml");
        }
        catch (ValidationException ve)
        {
            assertTrue(ve.getErrors().get("url").equals(sut.CANT_FIND_PLUGIN));
            throw ve;
        }
        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with an exception.
     * 
     * @throws Exception
     *             expected.
     */
    @Test(expected = ValidationException.class)
    public final void testExecuteException() throws Exception
    {
        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();

        context.checking(new Expectations()
        {
            {

                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(throwException(new Exception()));
            }
        });

        try
        {
            sut.populate(pluginDefinitionMock,  "src/test/resources/plugins/goodplugin.xml");
        }
        catch (ValidationException ve)
        {
            assertTrue(ve.getErrors().get("url").equals("Error retreiving plugin data."));
            throw ve;
        }

        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with metadata is returned.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecute() throws Exception
    {
        final GadgetMetaDataDTO meta = new GadgetMetaDataDTO(pluginDefinitionMock);

        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();
        metaData.add(meta);

        final Long updateFrequency = 0L;

        final UserPrefDTO userPref = new UserPrefDTO();
        final UserPrefDTO userPref2 = new UserPrefDTO();

        userPref.setName("updateFrequency");
        userPref.setDefaultValue(updateFrequency.toString());

        userPref2.setName("objectType");
        userPref2.setDefaultValue("BOOKMARK");

        final List<UserPrefDTO> preferences = new LinkedList();
        preferences.add(userPref);
        preferences.add(userPref2);
        meta.setUserPrefs(preferences);

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaData));
            }
        });

        sut.populate(pluginDefinitionMock,  "src/test/resources/plugins/goodplugin.xml");

        assertTrue(pluginDefinitionMock.getObjectType() == BaseObjectType.BOOKMARK);
        assertTrue(pluginDefinitionMock.getUpdateFrequency() == 0L);

        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with metadata is returned.
     * 
     * @throws Exception
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = ValidationException.class)
    public final void testAllVEFailExecute() throws Exception
    {
        final GadgetMetaDataDTO meta = new GadgetMetaDataDTO(pluginDefinitionMock);

        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();
        metaData.add(meta);

        final String invalidValue = "NOT VALID VALUE";

        final UserPrefDTO userPref = new UserPrefDTO();
        final UserPrefDTO userPref2 = new UserPrefDTO();

        userPref.setName("updateFrequency");
        userPref.setDefaultValue(invalidValue);

        userPref2.setName("objectType");
        userPref2.setDefaultValue(invalidValue);

        final List<UserPrefDTO> preferences = new LinkedList();
        preferences.add(userPref);
        preferences.add(userPref2);
        meta.setUserPrefs(preferences);

        context.checking(new Expectations()
        {
            {

                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaData));

            }
        });

        try
        {
            sut.populate(pluginDefinitionMock,  "src/test/resources/plugins/badplugin.xml");
        }
        catch (ValidationException ve)
        {
            assertTrue(ve.getErrors().size() == 1);
            assertTrue(ve.getErrors().get("url").equals(
                    sut.UPDATE_FREQUENCY_ERROR + sut.OBJECTTYPE_ERROR + sut.FEATURE_ERROR));
            throw ve;
        }

        context.assertIsSatisfied();
    }
}
