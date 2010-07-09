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
package org.eurekastreams.server.service.tasks;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GadgetDefinitionPopulator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Reindex task test.
 */
public class GadgetDefinitionReindexTaskTest
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
     * Mapper.
     */
    private GadgetDefinitionMapper gadgetDefMapper = context.mock(GadgetDefinitionMapper.class);

    /**
     * Definition Populator.
     */
    private GadgetDefinitionPopulator populator = context.mock(GadgetDefinitionPopulator.class);

    /**
     * System under test.
     */
    private GadgetDefinitionReindexTask sut = new GadgetDefinitionReindexTask(gadgetDefMapper, populator);

    /**
     * Number of gadgets per page.
     */
    static final int NUMBER_OF_DEFS = 10;

    /**
     * Test execute.
     */
    @Test
    public final void executeTest()
    {

        List<GadgetDefinition> defs = new ArrayList<GadgetDefinition>();

        for (int i = 0; i < NUMBER_OF_DEFS; i++)
        {
            defs.add(new GadgetDefinition());
        }

        final int total = 20;
        final int secondPageStartIndex = 10;
        final int secondPageEndIndex = 19;

        final PagedSet<GadgetDefinition> firstPage = new PagedSet<GadgetDefinition>();
        firstPage.setTotal(total);
        firstPage.setFromIndex(0);
        firstPage.setToIndex(9);
        firstPage.setPagedSet(defs);

        final PagedSet<GadgetDefinition> secondPage = new PagedSet<GadgetDefinition>();
        secondPage.setTotal(total);
        secondPage.setFromIndex(secondPageStartIndex);
        secondPage.setToIndex(secondPageEndIndex);
        secondPage.setPagedSet(defs);

        context.checking(new Expectations()
        {
            {
                oneOf(gadgetDefMapper).findAll(firstPage.getFromIndex(), firstPage.getToIndex());
                will(returnValue(firstPage));

                oneOf(gadgetDefMapper).findAll(secondPage.getFromIndex(), secondPage.getToIndex());
                will(returnValue(secondPage));

                exactly(firstPage.getTotal()).of(populator).populate(with(any(GadgetDefinition.class)),
                        with(any(String.class)));

                exactly(firstPage.getTotal()).of(gadgetDefMapper).reindex(with(any(GadgetDefinition.class)));

                oneOf(gadgetDefMapper).flush();
            }
        });

        sut.execute();

        context.assertIsSatisfied();
    }
}
