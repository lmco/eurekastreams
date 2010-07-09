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

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.PluginDefinitionMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.PluginDefinitionPopulator;


/**
 * This class provides the job for updating user counts nightly and updating any meta data stored in the DB.
 * 
 */
public class PluginDefinitionMetaUpdateTask
{
    /**
     * Local instance of logger.
     */
    private Log logger = LogFactory.getLog(PluginDefinitionMetaUpdateTask.class);

    /**
     * Local instance of PluginDefinitionMapper.
     */
    private PluginDefinitionMapper pluginDefMapper;

    /**
     * Local instance of stopwatch for timing of the job.
     */
    private StopWatch stopWatch;

    /**
     * Page size.
     */
    private static final int PAGE_SIZE = 10;

    /**
     * Plugin def populator.
     */
    PluginDefinitionPopulator populator;

    /**
     * Default construction for spring injection.
     */
    public PluginDefinitionMetaUpdateTask()
    {
        // no-op
    }

    /**
     * Constructor needed for injecting the correct mapper.
     * 
     * @param inPluginDefMapper
     *            - instance of the GadgetDefinitionMapper that this task will use to refresh user counts.
     * @param inPopulator
     *            the Plugin definition populator.
     */
    public PluginDefinitionMetaUpdateTask(final PluginDefinitionMapper inPluginDefMapper,
            final PluginDefinitionPopulator inPopulator)
    {
        pluginDefMapper = inPluginDefMapper;
        stopWatch = new StopWatch();
        populator = inPopulator;
    }

    /**
     * Perform the job functionality.
     */
    public void execute()
    {
        stopWatch.reset();
        stopWatch.start();

        try
        {
            PagedSet<PluginDefinition> pluginDefs = null;

            BasicPager pager = new BasicPager();
            pager.setPageSize(PAGE_SIZE);
            pager.setStartItem(0);
            pager.setEndItem(PAGE_SIZE - 1);
            pager.setMaxCount(PAGE_SIZE);

            int pluginsUpdatedCount = 0;

            while (true)
            {
                pluginDefs = pluginDefMapper.findAll(pager.getStartItem(), pager.getEndItem());
                pager.setEndItem(pluginDefs.getToIndex());
                pager.setMaxCount(pluginDefs.getTotal());
                pager.setPageSize(PAGE_SIZE);

                for (PluginDefinition def : pluginDefs.getPagedSet())
                {
                    pluginsUpdatedCount++;
                    try
                    {
                        populator.populate(def, def.getUrl());
                    }
                    catch (ValidationException ve)
                    {
                        logger.warn("Plugin:'" + def.getUrl() + "' failed to validate and did not update.", ve);
                    }
                    catch (Exception ex)
                    {
                        logger.error("Plugin Update failures", ex);
                    }
                }

                if (pager.isNextPageable())
                {
                    pager.nextPage();
                }
                else
                {
                    break;
                }
            }

            logger.info("Total Plugins updated: " + pluginsUpdatedCount);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred refreshing the Plugin Definition meta data", ex);
        }

        pluginDefMapper.flush();

        pluginDefMapper.refreshGadgetDefinitionUserCounts();

        stopWatch.stop();
        logger.info("Plugin Definition update Job: elapsed time: "
                + DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
    }
}
