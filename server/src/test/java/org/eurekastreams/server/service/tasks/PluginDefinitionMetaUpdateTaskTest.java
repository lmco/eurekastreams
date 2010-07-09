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

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.PluginDefinitionMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.PluginDefinitionPopulator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * meta update task test.
 */
public class PluginDefinitionMetaUpdateTaskTest
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
    private PluginDefinitionMapper pluginDefMapper = context.mock(PluginDefinitionMapper.class);

    /**
     * Definition Populator.
     */
    private PluginDefinitionPopulator populator = context.mock(PluginDefinitionPopulator.class);

    /**
     * System under test.
     */
    private PluginDefinitionMetaUpdateTask sut = new PluginDefinitionMetaUpdateTask(pluginDefMapper, populator);

    /**
     * Number of plugins per page.
     */
    static final int NUMBER_OF_DEFS = 10;

    /**
     * Test execute.
     */
    @Test
    public final void executeTest()
    {

        List<PluginDefinition> defs = new ArrayList<PluginDefinition>();

        for (int i = 0; i < NUMBER_OF_DEFS; i++)
        {
            defs.add(new PluginDefinition());
        }

        final int total = 20;
        final int secondPageStartIndex = 10;
        final int secondPageEndIndex = 19;

        final PagedSet<PluginDefinition> firstPage = new PagedSet<PluginDefinition>();
        firstPage.setTotal(total);
        firstPage.setFromIndex(0);
        firstPage.setToIndex(9);
        firstPage.setPagedSet(defs);

        final PagedSet<PluginDefinition> secondPage = new PagedSet<PluginDefinition>();
        secondPage.setTotal(total);
        secondPage.setFromIndex(secondPageStartIndex);
        secondPage.setToIndex(secondPageEndIndex);
        secondPage.setPagedSet(defs);

        context.checking(new Expectations()
        {
            {
                oneOf(pluginDefMapper).findAll(firstPage.getFromIndex(), firstPage.getToIndex());
                will(returnValue(firstPage));

                oneOf(pluginDefMapper).findAll(secondPage.getFromIndex(), secondPage.getToIndex());
                will(returnValue(secondPage));

                exactly(firstPage.getTotal()).of(populator).populate(with(any(PluginDefinition.class)),
                        with(any(String.class)));

                oneOf(pluginDefMapper).refreshGadgetDefinitionUserCounts();

                oneOf(pluginDefMapper).flush();
            }
        });

        sut.execute();

        context.assertIsSatisfied();
    }

    /**
     * Test that even if a plugin fails to update the other plugins will have a chance execute.
     */
    @Test
    public final void executeTestEvenifOneFails()
    {

        List<PluginDefinition> defs = new ArrayList<PluginDefinition>();

        for (int i = 0; i < 2; i++)
        {
            defs.add(new PluginDefinition());
        }

        final int total = 2;

        final PagedSet<PluginDefinition> firstPage = new PagedSet<PluginDefinition>();
        firstPage.setTotal(total);
        firstPage.setFromIndex(0);
        firstPage.setToIndex(9);
        firstPage.setPagedSet(defs);

        final ValidationException ve = new ValidationException();
        ve.addError("url", "its messed up");

        context.checking(new Expectations()
        {
            {
                oneOf(pluginDefMapper).findAll(firstPage.getFromIndex(), firstPage.getToIndex());
                will(returnValue(firstPage));

                oneOf(populator).populate(with(any(PluginDefinition.class)), with(any(String.class)));
                will(returnValue(ve));

                oneOf(populator).populate(with(any(PluginDefinition.class)), with(any(String.class)));

                oneOf(pluginDefMapper).refreshGadgetDefinitionUserCounts();

                oneOf(pluginDefMapper).flush();
            }
        });

        sut.execute();

        context.assertIsSatisfied();
    }
}
