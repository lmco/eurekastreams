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
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GadgetDefinitionPopulator;

/**
 * This class provides the job for reindexing gadget defintions.
 *
 */
public class GadgetDefinitionReindexTask
{
    /**
     * Local instance of logger.
     */
    private Log logger = LogFactory.getLog(GadgetDefinitionReindexTask.class);

    /**
     * Local instance of GadgetDefinitionMapper.
     */
    private GadgetDefinitionMapper gadgetDefMapper;

    /**
     * Local instance of stopwatch for timing of the job.
     */
    private StopWatch stopWatch;

    /**
     * Page size.
     */
    private static final int PAGE_SIZE = 10;

    /**
     * Gadget def populator.
     */
    GadgetDefinitionPopulator populator;

    /**
     * Default construction for spring injection.
     */
    public GadgetDefinitionReindexTask()
    {
        // no-op
    }

    /**
     * Constructor needed for injecting the correct mapper.
     *
     * @param inGadgetDefMapper
     *            - instance of the GadgetDefinitionMapper that this task will use to refresh user counts.
     * @param inPopulator
     *            the gadget definition populator.
     */
    public GadgetDefinitionReindexTask(final GadgetDefinitionMapper inGadgetDefMapper,
            final GadgetDefinitionPopulator inPopulator)
    {
        gadgetDefMapper = inGadgetDefMapper;
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
            PagedSet<GadgetDefinition> gadgetDefs = null;

            BasicPager pager = new BasicPager();
            pager.setPageSize(PAGE_SIZE);
            pager.setStartItem(0);
            pager.setEndItem(PAGE_SIZE - 1);
            pager.setMaxCount(PAGE_SIZE);

            int indexedGadgetsCount = 0;

            while (true)
            {
                gadgetDefs = gadgetDefMapper.findAll(pager.getStartItem(), pager.getEndItem());
                pager.setEndItem(gadgetDefs.getToIndex());
                pager.setMaxCount(gadgetDefs.getTotal());
                pager.setPageSize(PAGE_SIZE);

                for (GadgetDefinition def : gadgetDefs.getPagedSet())
                {
                    indexedGadgetsCount++;
                    populator.populate(def, def.getUrl());
                    gadgetDefMapper.reindex(def);
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

            logger.info("Total Gadgets indexed: " + indexedGadgetsCount);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred refreshing the Gadget Definition Index", ex);
        }

        gadgetDefMapper.flush();

        stopWatch.stop();
        logger.info("Gadget Definition Index Refresh Job: elapsed time: "
                + DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
    }
}
