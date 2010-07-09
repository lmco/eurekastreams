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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.GadgetDefinition;

/**
 * This class provides the mapper functionality for GadgetDefinition entities.
 */
public class GadgetDefinitionMapper extends CommonGadgetDefinitionMapper<GadgetDefinition> implements
        GalleryItemMapper<GadgetDefinition>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.getLog(GadgetDefinitionMapper.class);

    /**
     * Paramter for retrieving paged sets based on category.
     */
    private static final String CATEGORY_PAGED_PARAMETER = "category";

    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public GadgetDefinitionMapper(final QueryOptimizer inQueryOptimizer)
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
        return "GadgetDefinition";
    }

    /**
     * This method is called from a job that will refresh the gadgetdefinition counts for all of the gadgetdefs in the
     * system. This helps to ensure that the gallery can display the correct number of users.
     */
    @SuppressWarnings("unchecked")
    public void refreshGadgetDefinitionUserCounts()
    {
        Query q = getEntityManager().createQuery("from GadgetDefinition gd");
        List<GadgetDefinition> gadgetDefs = (List<GadgetDefinition>) q.getResultList();
        logger.debug("Retrieved: " + gadgetDefs.size() + " Gadget defs from db");

        Query gadgetQuery;
        int numUsersCount;
        for (GadgetDefinition currentGadgetDef : gadgetDefs)
        {
            gadgetQuery = getEntityManager().createQuery(
                    "select g.owner.id " + "from Gadget g " + "where g.gadgetDefinition.id=:gadgetDefId "
                            + "and g.owner.id is not null " + "group by g.owner.id, g.gadgetDefinition.id")
                    .setParameter("gadgetDefId", currentGadgetDef.getId());
            try
            {
                numUsersCount = gadgetQuery.getResultList().size();

                logger.debug("Retrieved: " + numUsersCount + " users for the current gadget def: "
                        + currentGadgetDef.getUrl());
            }
            catch (NoResultException nrex)
            {
                // This scenario occurs when no one has an instance of the current gadget.
                // Catching this exception is the only way to test for this situation.
                numUsersCount = 0;
            }
            currentGadgetDef.setNumberOfUsers(numUsersCount);
        }

        getEntityManager().flush();
    }

    /**
     * Delete a gadget definition.
     * 
     * @param inGadgetDefinition
     *            The gadget definition to delete.
     */
    @Override
    public void delete(final GadgetDefinition inGadgetDefinition)
    {
        getEntityManager().remove(inGadgetDefinition);
    }

}
