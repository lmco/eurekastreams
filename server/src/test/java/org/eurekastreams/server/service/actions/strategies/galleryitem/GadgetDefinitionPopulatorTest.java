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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the create person strategy.
 */
public class GadgetDefinitionPopulatorTest
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
    private GadgetDefinitionPopulator sut;

    /**
     * GadgetDefinition Mock.
     */
    private GadgetDefinition gadgetDefinitionMock = context.mock(GadgetDefinition.class);

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
        sut = new GadgetDefinitionPopulator(fetcher);
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

        sut.populate(gadgetDefinitionMock, "src/main/webapp/gadget/gadget.xml");

        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with an exception.
     *
     * @throws Exception
     *             expected.
     */
    @Test
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

        sut.populate(gadgetDefinitionMock, "src/main/webapp/gadget/gadget.xml");

        context.assertIsSatisfied();
    }

    /**
     * Test the execute method with metadata is returned.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void testExecute() throws Exception
    {
        final GadgetMetaDataDTO meta = context.mock(GadgetMetaDataDTO.class);

        final List<GadgetMetaDataDTO> metaData = new ArrayList<GadgetMetaDataDTO>();
        metaData.add(meta);

        final String author = "author";
        final String title = "title";
        final String description = "description";

        context.checking(new Expectations()
        {
            {
                oneOf(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaData));

                oneOf(meta).getAuthor();
                will(returnValue(author));

                oneOf(meta).getTitle();
                will(returnValue(title));

                oneOf(meta).getDescription();
                will(returnValue(description));

                oneOf(gadgetDefinitionMock).setGadgetAuthor(author);
                oneOf(gadgetDefinitionMock).setGadgetTitle(title);
                oneOf(gadgetDefinitionMock).setGadgetDescription(description);

                oneOf(gadgetDefinitionMock).getGadgetAuthor();
                will(returnValue(author));

                oneOf(gadgetDefinitionMock).getGadgetTitle();
                will(returnValue(title));

                oneOf(gadgetDefinitionMock).getGadgetDescription();
                will(returnValue(description));

            }
        });

        sut.populate(gadgetDefinitionMock, "src/main/webapp/gadget/gadget.xml");

        context.assertIsSatisfied();
    }
}
