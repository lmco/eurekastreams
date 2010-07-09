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
package org.eurekastreams.server.persistence;

import java.util.List;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;

/**
 * This class provides the mapper functionality for GadgetDefinition entities.
 */
public class PluginDefinitionMapper extends CommonGadgetDefinitionMapper<PluginDefinition> implements
        GalleryItemMapper<PluginDefinition>
{

    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.getLog(PluginDefinitionMapper.class);

    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public PluginDefinitionMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Retrieve the name of the DomainEntity. This is to allow for the super class to identify the table within
     * hibernate.
     * 
     * @return The name of the domain entity.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "PluginDefinition";
    }

    /**
     * Updates user counts for plugin class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void refreshGadgetDefinitionUserCounts()
    {

        // Gets a List of all Plugin Definitions to update.
        Query q = getEntityManager().createQuery("from PluginDefinition gd");
        List<PluginDefinition> pluginDefinitions = (List<PluginDefinition>) q.getResultList();
        logger.debug("Retrieved: " + pluginDefinitions.size() + " Plugin defs from db");

        // Get a List of Plugin Ids grouped by the person Id.
        // This basically gives us back only one entry even if a person uses the same plugin more then once.
        q = getEntityManager().createQuery(
                "select f.streamPlugin.id "
                        + "from Feed f , IN(f.feedSubscribers) fp group by f.streamPlugin.id, fp.entityId, fp.type");

        List<Long> pluginDefinitionCountPerson = (List<Long>) q.getResultList();

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved: " + pluginDefinitionCountPerson.size() + " UserCounts from db");
        }

        // Loop throught the PDs you need to update.
        for (PluginDefinition pD : pluginDefinitions)
        {
            int userCount = 0;

            // count up the configured pluginsdefs for people.
            for (Long pluginCount : pluginDefinitionCountPerson)
            {
                if (pluginCount == pD.getId())
                {
                    userCount++;
                }
            }

            // set count.
            pD.setNumberOfUsers(userCount);
        }

        getEntityManager().flush();
    }

    /**
     * Soft deletes plugin.
     * 
     * @param inPlugin
     *            The plugin to soft delete.
     */
    @Override
    public void delete(final PluginDefinition inPlugin)
    {
        inPlugin.setShowInGallery(false);
        getEntityManager().persist(inPlugin);
    }
}
